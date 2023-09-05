package com.bdpick.service;

import com.bdpick.common.BdConstants;
import com.bdpick.common.MailService;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.Verify;
import com.bdpick.repository.SignRepository;
import com.bdpick.repository.UserRepository;
import com.bdpick.repository.VerifyRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * sign service class
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SignService {
    private final Stage.SessionFactory factory;
    private final SignRepository signRepository;
    private final UserRepository userRepository;
    private final VerifyRepository verifyRepository;
    private final MailService mailService;

    /**
     * sign up
     *
     * @param user 등록할 user
     * @return 등록된 user
     */
    public Mono<User> up(@RequestBody User user) {
        return factory.withTransaction(session
                        -> signRepository.up(user, session))
                .thenApply(Mono::justOrEmpty)
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }
//
//    public Mono<CommonResponse> in(@RequestBody User user) {
//        CommonResponse response = new CommonResponse();
//        String userId = user.getId();
//        String password = user.getPassword();
//        String uuid = user.getUuid();
//        AtomicReference<Long> deviceId = new AtomicReference<>();
//        AtomicReference<String> refreshToken = new AtomicReference<>();
//
//        return userRepository.findById(userId)
//                .defaultIfEmpty(new User())
//                .<User>handle((existedUser, sink) -> {
//                    if (existedUser.getId() == null) {
//                        sink.error(new RuntimeException(ERROR_NO_USER));
//
//                    } else if (!Objects.equals(existedUser.getPassword(), password)) {
//                        sink.error(new RuntimeException(ERROR_NOT_CORRECT));
//                    } else {
//                        String accessToken = jwtService.createAccessToken(userId);
//                        refreshToken.set(jwtService.createRefreshToken());
//                        Token token = new Token(accessToken, refreshToken.get());
//                        Map<String, Object> resultMap = new HashMap<>();
//                        resultMap.put("token", token);
//                        resultMap.put("userType", existedUser.getType());
//                        response.setData(resultMap);
//                        sink.next(existedUser);
//                    }
//                })
//                .flatMap(userRepository::save)
//                .then(deviceRepository.findDeviceByUserIdAndUuid(userId, uuid))
//                .doOnNext(device -> deviceId.set(device.getId()))
//                .hasElement()
//                .flatMap(aBoolean ->
//                {
//                    // 데이터 존재하지 않을 경우 시퀀스 생성
//                    if (!aBoolean) {
//                        return deviceRepository.getSequence().zipWith(Mono.just(true));
//                    } else return Mono.just(deviceId.get()).zipWith(Mono.just(false));
//                    // 데이터 존재할 경우
//                })
//                .flatMap(objects -> {
//                    Long id = objects.getT1();
//                    Device device = new Device();
//                    device.setNew(objects.getT2());
//                    device.setId(id);
//                    device.setUserId(userId);
//                    device.setUuid(uuid);
//                    device.setRefreshToken(refreshToken.get());
//                    device.setCreatedAt(LocalDateTime.now());
//                    return deviceRepository.save(device);
//                })
//                .then(Mono.just(response))
//                .onErrorResume(throwable -> {
//                    log.error("error : ", throwable);
//                    if (throwable instanceof RuntimeException) {
//                        String message = throwable.getMessage();
//                        switch (message) {
//                            case ERROR_NO_USER:
//                                response.setError("해당 계정이 존재하지 않습니다.", false);
//                                break;
//                            case ERROR_NOT_CORRECT:
//                                response.setError("아이디와 패스워드를 확인해주세요.", false);
//                                break;
//                            default:
//                                throw new RuntimeException(throwable);
//                        }
//                        return Mono.just(response);
//                    } else {
//                        throw new RuntimeException(throwable);
//                    }
//                });
//
//
//    }

    /**
     * check is id available
     *
     * @param id checking id
     * @return true : available , false : unavailable
     */
    public Mono<Boolean> isAvailableId(String id) {
        return factory.withSession(session
                        -> session.find(User.class, id)
                )
                .thenApply(Objects::isNull)
                .thenApply(Mono::just)
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
    @Transactional
    public Mono<Boolean> sendMail(@RequestBody User user) {
        String email = Optional.of(user.getEmail()).orElseThrow(NoSuchFieldError::new);
        String code = RandomStringUtils.randomAlphanumeric(6, 6);
        String subject = "BDPICK 인증번호";

        return factory.withTransaction(session
                        -> userRepository.findByEmail(email, session)
                        .thenAccept((foundUser) -> {
                            if (foundUser == null) {
                                throw new RuntimeException(BdConstants.Exception.KEY_NO_USER);
                            }
                            Verify verify = new Verify();
                            verify.setCode(code);
                            verify.setEmail(email);
                            session.persist(verify);
                        }))
                .thenApply(unused -> {
                    mailService.sendMail(List.of(email), subject, code);
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
        return factory.withSession(session -> {
                    // 해당 이메일과 코드로 가장 최근 데이터 1건 조회
                    return verifyRepository.findLastByEmailAndCode(verify, session);
                })
                // 생성된 데이터가 현재 시간 -3분보다 이후에 생성됐는지 확인
                .thenApply(foundVerify -> Mono.just(foundVerify != null && foundVerify.getCreatedAt().isAfter(localDateTime)))
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }
}
