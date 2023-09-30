package com.bdpick.user.service.impl;

import com.bdpick.mail.service.impl.MailServiceImpl;
import com.bdpick.common.security.JwtService;
import com.bdpick.user.web.rest.dto.SignInDto;
import com.bdpick.user.web.rest.dto.Token;
import com.bdpick.user.web.rest.dto.UserDto;
import com.bdpick.user.adaptor.UserProducer;
import com.bdpick.user.domain.Device;
import com.bdpick.user.domain.User;
import com.bdpick.user.domain.Verify;
import com.bdpick.user.web.rest.mapper.UserMapper;
import com.bdpick.user.domain.enumeration.EmailType;
import com.bdpick.user.repository.impl.DeviceRepositoryImpl;
import com.bdpick.user.repository.impl.SignRepositoryImpl;
import com.bdpick.user.repository.impl.UserRepositoryImpl;
import com.bdpick.user.repository.impl.VerifyRepositoryImpl;
import com.bdpick.user.service.SignService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.bdpick.common.BdConstants.Exception.*;

/**
 * sign service class
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SignServiceImpl implements SignService {
    private final Stage.SessionFactory factory;
    private final SignRepositoryImpl signRepository;
    private final UserRepositoryImpl userRepository;
    private final VerifyRepositoryImpl verifyRepository;
    private final MailServiceImpl mailServiceImpl;
    private final JwtService jwtService;
    private final DeviceRepositoryImpl deviceRepository;
    private final UserProducer userProducer;

    /**
     * sign up
     *
     * @param user 등록할 user
     * @return 등록된 user
     */
    public Mono<User> up(@NonNull User user) {

        return factory.withTransaction(session
                        -> signRepository.up(user, session))
                .thenApply(createdUser -> {
                    try {
                        userProducer.createUser(createdUser.getId());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return Mono.justOrEmpty(createdUser);

                })
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }

    /**
     * sign in
     *
     * @param user user
     * @return SignInDto
     * token : accessToken, refreshToken
     * Type : userType
     */
    public Mono<SignInDto> in(@NonNull UserDto user) {
        SignInDto signInDto = new SignInDto();
        String userId = user.getId();
        String password = user.getPassword();
        String uuid = user.getUuid();
        AtomicReference<String> refreshToken = new AtomicReference<>();

        return factory.withTransaction(session ->
                        userRepository.findById(userId, session)
                                .thenCompose(rtnUser -> {
                                    log.info("rtnUser = " + rtnUser);
                                    // 해당 아이디가 존재하지 않을 경우
                                    if (rtnUser == null) {
                                        throw new RuntimeException(KEY_NO_USER);
                                    }
                                    // 입력 패스워드가 일치하지 않을 경우
                                    else if (!rtnUser.getPassword().equals(password)) {
                                        throw new RuntimeException(KEY_NOT_CORRECT);
                                    } else {

                                        String accessToken = jwtService.createAccessToken(userId);
                                        refreshToken.set(jwtService.createRefreshToken());
                                        Token token = new Token(accessToken, refreshToken.get());
                                        signInDto.setToken(token);
                                        signInDto.setUserType(rtnUser.getType());

                                        Device device = new Device();
                                        device.setUser(rtnUser);
                                        device.setUuid(uuid);
                                        device.setPushToken("TEMP");
                                        device.setRefreshToken(refreshToken.get());

                                        return deviceRepository.findDeviceByUserAndUuid(device, session)
                                                .thenCompose(foundDevice -> {
                                                    log.info("foundDevice = " + foundDevice);
                                                    // 기존 데이터가 없을 경우 Create
                                                    if (foundDevice == null) {
                                                        foundDevice = device;
                                                    }
                                                    // 기존 데이터가 존재할 경우 update
                                                    else {
                                                        foundDevice.setPushToken("CHANGE");
                                                        foundDevice.setRefreshToken(refreshToken.get());
                                                    }
                                                    return deviceRepository.save(foundDevice, session)
                                                            .thenCompose(unused -> {
                                                                try {
                                                                    userProducer.logIn(userId);
                                                                } catch (JsonProcessingException e) {
                                                                    throw new RuntimeException(e);
                                                                }
                                                                return CompletableFuture.completedStage(signInDto);
                                                            });
                                                });
                                    }
                                })
                )
                .thenApply(Mono::just)
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }

    /**
     * access 토큰 만료 시 리프레시 토큰으로 새 토큰을 발급하고 리턴한다.
     *
     * @param token token
     * @return SignInDto
     */
    public Mono<SignInDto> renewToken(@NonNull Token token) {
        String refreshToken = token.getRefreshToken();
        String accessToken = token.getAccessToken();
        String userId = jwtService.getUserIdByToken(accessToken);
        AtomicReference<UserDto> rtnUser = new AtomicReference<>();

        try {
            jwtService.verifyToken(refreshToken);

            Device device = new Device();
            User user = new User();
            user.setId(userId);
            device.setRefreshToken(refreshToken);
            device.setUser(user);

            factory.withTransaction(session -> deviceRepository.findDeviceByUserIdAndRefreshToken(device, session)
                            .thenCompose(foundDevice -> {
                                if (foundDevice != null) {
                                    return userRepository.findById(userId, session)
                                            .thenApply(foundUser -> {
                                                UserDto userDto = UserMapper.INSTANCE.userToDto(foundUser, foundDevice.getUuid());
//                                                userDto.setUuid(foundDevice.getUuid());
                                                return userDto;
                                            });
                                }
                                // 리프레시 토큰이 db와 일치하지 않을 경우
                                else {
                                    throw new RuntimeException(KEY_TOKEN_IS_NOT_CORRECT);
                                }
                            }))
                    .thenApply(userDto -> {
                        rtnUser.set(userDto);
                        return rtnUser;
                    })
                    .toCompletableFuture().join();
            return in(rtnUser.get());

        } catch (Exception e) {
            log.error("error : ", e);
            // 리프레시 토큰 만료 시
            if (e instanceof ExpiredJwtException) {
                return Mono.error(new RuntimeException(KEY_TOKEN_EXPIRED));
            } else {
                return Mono.error(e);
            }
        }
    }

    /**
     * check is id available
     *
     * @param id checking id
     * @return true : available , false : unavailable
     */
    public Mono<Boolean> isAvailableId(@NonNull String id) {
        return factory.withSession(session
                        -> session.find(User.class, id)
                )
                .thenApply(Objects::isNull)
                .thenApply(aBoolean -> {
                    if (aBoolean) {
                        try {
                            userProducer.verifyId(id);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return Mono.just(aBoolean);
                })
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }

    /**
     * check is email exist
     *
     * @param email email
     * @return true, false
     */
//    public Mono<Boolean> findEmailExist(String email) {
//        return factory.withSession(session
//                        -> userRepository.findByEmail(email, session)
//                })
//                .thenApply(user -> Mono.just(user != null))
//                .toCompletableFuture().join();
//
//    }
//    public CompletionStage<Boolean> findEmailExist(String email, Stage.Session session) {
//        return userRepository.findByEmail(email, session)
//                .thenApply(Objects::nonNull);
//    }

    /**
     * send mail
     *
     * @param user user
     * @return true : success, false : fail
     */
    public Mono<Boolean> sendMail(@NonNull User user) {
        // fixme userId 데이터 들어있는지 확인 필요
        String userId = user.getId();
        String email = Optional.of(user.getEmail()).orElseThrow(NoSuchFieldError::new).trim();
        String code = RandomStringUtils.randomAlphanumeric(6, 6);
        String subject = "BDPICK 인증번호";

        return factory.withTransaction(session
                        -> userRepository.findByEmail(email, session)
                        .thenAccept((foundUser) -> {
//                            if (foundUser == null) {
//                                throw new RuntimeException(KEY_NO_USER);
//                            }
                            Verify verify = new Verify();
                            verify.setCode(code);
                            verify.setEmail(email);
                            session.persist(verify);
                        }))
                .thenApply(unused -> {
                    try {
                        userProducer.sendMail(user.getId(), email, EmailType.MAIL_VERIFIED, subject, code);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return Mono.just(true);
                })
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }

    /**
     * verify email and code
     *
     * @param verify verify entity
     * @return response
     * data - true : success, false : fail
     */
    public Mono<Boolean> verifyEmail(@NonNull Verify verify) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(3);
        verify.setEmail(verify.getEmail().trim());
        return factory.withSession(session -> {
                    // 해당 이메일과 코드로 가장 최근 데이터 1건 조회
                    return verifyRepository.findLastByEmailAndCode(verify, session);
                })
                // 생성된 데이터가 현재 시간 -3분보다 이후에 생성됐는지 확인
                .thenApply(foundVerify -> {
                    boolean isVerified = foundVerify != null && foundVerify.getCreatedAt().isAfter(localDateTime);
                    if (isVerified) {
                        try {
                            userProducer.verifyMail(foundVerify.getEmail());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return Mono.just(isVerified);
                })
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }
}
