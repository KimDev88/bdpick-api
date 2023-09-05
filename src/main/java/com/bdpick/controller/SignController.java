package com.bdpick.controller;

import com.bdpick.common.BdConstants;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.Verify;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.domain.request.ResponseCode;
import com.bdpick.service.SignService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * sign controller class
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(PREFIX_API_URL + "/sign")
public class SignController {
    private final SignService signService;
    private static final String MSG_NOT_EXIST_EMAIL = "해당 이메일이 존재하지않습니다.";

//
//    private Mono<Boolean> findEmailExist(String email) {
//        return userRepository.findByEmail(email)
//                .hasElement();
//    }


    /**
     * sing up user
     *
     * @param user user entity
     * @return true : success, false : fail
     */
    @PostMapping("up")
    @Transactional
    public Mono<CommonResponse> up(@RequestBody User user) {
        CommonResponse commonResponse = new CommonResponse();

        return signService.up(user)
                .map(createdUser -> commonResponse.setData(true))
                .onErrorResume(throwable -> {
                    // 이미 등록된 계정일 경우
                    if (throwable.getMessage().startsWith(BdConstants.Exception.NAME_EXCEPTION_DUPLICATE_DATA)) {
                        return Mono.just(commonResponse
                                .setData(false)
                                .setCode(ResponseCode.CODE_DATA_DUPLICATE)
                                .setMessage(ResponseCode.MESSAGE_DATA_DUPLICATE));
                    }
                    return Mono.just(commonResponse.setError().setData(false).setMessage(throwable.getMessage()));
                });
    }

    //
//    @PostMapping("in")
//    @Transactional
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
//
//

    /**
     * check user is existed
     *
     * @param id user id
     * @return true or false
     */
    @GetMapping("check/{id}")
    public Mono<CommonResponse> isAvailableId(@PathVariable("id") String id) {
        CommonResponse commonResponse = new CommonResponse();
        return signService.isAvailableId(id)
                .map(commonResponse::setData)
                .onErrorResume(throwable -> {
                    log.error("error", throwable);
                    return Mono.just(commonResponse.setError(throwable.getMessage()));
                });
    }


    /**
     * send email to user
     *
     * @param user user with email
     * @return response
     * data - true : success, false : fail
     */
    @Transactional
    @PostMapping("/send-mail")
    public Mono<CommonResponse> sendMail(@RequestBody User user) {
        CommonResponse response = new CommonResponse();
        return signService.sendMail(user)
                .map(aBoolean -> response.setData(true))
                .onErrorResume(throwable -> {
                            log.error("error : ", throwable);
                            // 해당 이메일의 회원이 존재하지 않을 경우
                            if (throwable instanceof RuntimeException
                                    && throwable.getCause() != null
                                    && throwable.getCause().getMessage().equals(BdConstants.Exception.KEY_NO_USER)) {
                                return Mono.just(response.setError(MSG_NOT_EXIST_EMAIL, false));
                            }
                            return Mono.just(response.setError().setMessage(throwable.getMessage()).setData(false));
                        }
                );
    }

    /**
     * verify email
     *
     * @param verify verify
     * @return response
     * data - true : success, false : fail
     */
    @PostMapping("/verify-mail")
    public Mono<CommonResponse> verifyEmail(@RequestBody Verify verify) {
        CommonResponse commonResponse = new CommonResponse();
        return signService.verifyEmail(verify)
                .map(commonResponse::setData)
                .onErrorResume(throwable -> {
                    log.error("error", throwable);
                    return Mono.just(commonResponse.setError(throwable.getMessage()));
                });
    }
//
//    @PostMapping("renew")
//    public Mono<CommonResponse> renewToken(@RequestBody Token token) {
//        CommonResponse response = new CommonResponse();
//        String refreshToken = token.getRefreshToken();
//        String accessToken = token.getAccessToken();
//        AtomicReference<String> uuid = new AtomicReference<>();
//        String userId = jwtService.getUserIdByToken(accessToken);
//
//        try {
//            jwtService.verifyToken(refreshToken);
//            return deviceRepository.findDeviceByUserIdAndRefreshToken(userId, refreshToken)
//                    .doOnNext(device -> uuid.set(device.getUuid()))
//                    .hasElement()
//                    .flatMap(isExist -> {
//                        if (isExist) {
//                            return userRepository.findById(userId);
//                        } else {
//                            return Mono.error(new RuntimeException("TOKEN_IS_NOT_CORRECT"));
//                        }
//                    })
//                    .flatMap(user -> {
//                        user.setUuid(uuid.get());
//                        return this.in(user);
//                    });
//        } catch (Exception e) {
//            log.error("error : ", e);
//            // JWT 만료 시
//            if (e instanceof ExpiredJwtException) {
//                response.setError().setCode(ResponseCode.CODE_UNAUTHORIZED);
//            } else {
//                response.setError();
//            }
//
//        }
//        return Mono.just(response);
//    }

}
