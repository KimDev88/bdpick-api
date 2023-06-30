package com.bdpick.controller;

//import com.bdpick.common.security.JwtService;

import com.bdpick.common.security.JwtService;
import com.bdpick.domain.Token;
import com.bdpick.domain.User;
import com.bdpick.domain.Verify;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.UserRepository;
import com.bdpick.repository.VerifyRepository;
import dev.snowdrop.vertx.mail.MailClient;
import dev.snowdrop.vertx.mail.SimpleMailMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.*;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

@Slf4j
@RestController
@RequestMapping(PREFIX_API_URL + "/sign")
public class SignController {
    private final UserRepository userRepository;
    private final VerifyRepository verifyRepository;

    private final JwtService jwtService;
    private final MailClient mailClient;

    static private final String ERROR_NO_USER = "NO_USER";
    static private final String ERROR_NOT_CORRECT = "NOT_CORRECT";
    static private final String ERROR_EMAIL_EXIST = "EMAIL_EXIST";

    private Mono<Boolean> findEmailExist(String email) {
        return userRepository.findByEmail(email)
                .hasElement();
    }

    public SignController(UserRepository userRepository, VerifyRepository verifyRepository, JwtService jwtService, MailClient mailClient) {
        this.userRepository = userRepository;
        this.verifyRepository = verifyRepository;
        this.jwtService = jwtService;
        this.mailClient = mailClient;
    }

    @PostMapping("up")
    @Transactional
    public Mono<CommonResponse> up(@RequestBody User user) {
        CommonResponse response = new CommonResponse();
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setNew(true);

        return findEmailExist(user.getEmail())
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        response.setError("이미 사용중인 이메일입니다.", false);
                    } else {
                        userRepository.save(user).hasElement().subscribe();
                        response.setData(true);
                    }
                    return Mono.just(response);
                });
    }

    @PostMapping("in")
    @Transactional
    public Mono<CommonResponse> in(@RequestBody User user) {
        CommonResponse response = new CommonResponse();
        String id = user.getId();
        String password = user.getPassword();

        return userRepository.findById(id)
                .defaultIfEmpty(new User())
                .<User>handle((existedUser, sink) -> {
                    if (existedUser.getId() == null) {
                        sink.error(new RuntimeException(ERROR_NO_USER));

                    } else if (!Objects.equals(existedUser.getPassword(), password)) {
                        sink.error(new RuntimeException(ERROR_NOT_CORRECT));
                    } else {
                        String accessToken = jwtService.createAccessToken(id);
                        String refreshToken = jwtService.createRefreshToken();
                        Token token = new Token(accessToken, refreshToken);
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("token", token);
                        resultMap.put("userType", existedUser.getType());
                        response.setData(resultMap);
                        existedUser.setToken(refreshToken);
                        sink.next(existedUser);
                    }
                })
                .flatMap(userRepository::save)
                .then(Mono.just(response))
                .onErrorResume(throwable -> {
                    log.error("error : ", throwable);
                    if (throwable instanceof RuntimeException) {
                        String message = throwable.getMessage();
                        switch (message) {
                            case ERROR_NO_USER:
                                response.setError("해당 계정이 존재하지 않습니다.", false);
                                break;
                            case ERROR_NOT_CORRECT:
                                response.setError("아이디와 패스워드를 확인해주세요.", false);
                                break;
                            default:
                                throw new RuntimeException(throwable);
                        }
                        return Mono.just(response);
                    } else {
                        throw new RuntimeException(throwable);
                    }
                });


    }

    /**
     * check user is existed
     *
     * @param id user id
     * @return true or false
     */
    @GetMapping("check/{id}")
    public Mono<CommonResponse> isAvailableId(@PathVariable("id") String id) {
        return userRepository
                .findById(id)
                .hasElement()
                .map(aBoolean -> !aBoolean)
                .map(CommonResponse::new);
    }

    @Transactional
    @PostMapping("send-mail")
    public Mono<CommonResponse> sendMail(@RequestBody User user) {
        CommonResponse response = new CommonResponse();
        String email = Optional.of(user.getEmail()).orElseThrow(NoSuchFieldError::new);
        String code = RandomStringUtils.randomAlphanumeric(6, 6);
        return findEmailExist(email).doOnNext(aBoolean -> {
                    if (aBoolean) {
                        throw new RuntimeException(ERROR_EMAIL_EXIST);
                    }
                })
                .then(Mono.defer(() -> {
                    Verify verify = new Verify();
                    verify.setCode(code);
                    verify.setEmail(email);
                    verify.setCreatedAt(LocalDateTime.now());

                    return verifyRepository
                            .save(verify)
                            .hasElement()
                            .map(CommonResponse::new);
                }))
                .doFinally((signalType) -> {
                    mailClient.send(new SimpleMailMessage()
                            .setFrom("no-reply@bdpick.com")
                            .setTo(List.of(email))
                            .setSubject("BDPICK 인증번호")
                            .setText(code)).subscribe();
                })
                .onErrorResume(throwable -> {
                            log.error("error : ", throwable);
                            if (throwable instanceof RuntimeException && throwable.getMessage().equals(ERROR_EMAIL_EXIST)) {
                                return Mono.just(response.setError("이미 사용중인 이메일입니다.", false));
                            }
                            throw new RuntimeException(throwable);
                        }
                );


    }

    @PostMapping("verify-mail")
    public Mono<CommonResponse> verifyEmail(@RequestBody Verify verify) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(3);
        // 해당 이메일의 가장 최근 데이터 1건 조회
        return verifyRepository.findFirstByEmailOrderByCreatedAtDesc(verify.getEmail())
                // 해당 데이터와 코드 비교 && 현재시간보다 3분전의 시간
                .map(
                        verify1 -> verify1.getCode().equals(verify.getCode()) && verify1.getCreatedAt().isAfter(localDateTime)
                ).map(CommonResponse::new);

    }

}
