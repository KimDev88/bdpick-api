package com.bdpick.user.web.rest;

import com.bdpick.common.BdConstants;
import com.bdpick.user.web.rest.dto.Token;
import com.bdpick.user.web.rest.dto.UserDto;
import com.bdpick.user.domain.User;
import com.bdpick.user.domain.Verify;
import com.bdpick.common.request.CommonResponse;
import com.bdpick.common.request.ResponseCode;
import com.bdpick.user.service.impl.SignServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.bdpick.common.BdConstants.Exception.*;
import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * sign controller class
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(PREFIX_API_URL + "/sign")
public class SignResource {
    private final SignServiceImpl signService;

    /**
     * sing up user
     *
     * @param user user entity
     * @return true : success, false : fail
     */
    @PostMapping("up")
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
                                .setMessage(MSG_DATA_DUPLICATE));
                    }
                    return Mono.just(commonResponse.setError().setData(false).setMessage(throwable.getMessage()));
                });
    }

    /**
     * sign in
     *
     * @param user user
     * @return SignInDto
     * token : accessToken, refreshToken
     * Type : userType
     */
    @PostMapping("in")
    public Mono<CommonResponse> in(@RequestBody UserDto user) {
        CommonResponse commonResponse = new CommonResponse();

        return signService.in(user)
                .map(commonResponse::setData)
                .onErrorResume(throwable -> {
                    log.info("throwable = " + throwable);
                    // 아이디가 존재하지 않을 경우
                    if (throwable.getCause().getMessage().equals(KEY_NO_USER)) {
                        commonResponse.setError(MSG_NO_USER, null);
                    }
                    // 아이디 패스워드가 일치하지 않을 경우
                    else if (throwable.getCause().getMessage().equals(KEY_NOT_CORRECT)) {
                        commonResponse.setError(MSG_NOT_CORRECT, null);

                    } else {
                        commonResponse.setError(throwable.getMessage(), null);
                    }
                    return Mono.just(commonResponse);
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
    @PostMapping("/send-mail")
    public Mono<CommonResponse> sendMail(@RequestBody User user) {
        CommonResponse response = new CommonResponse();
        return signService.sendMail(user)
                .map(aBoolean -> response.setData(true))
                .onErrorResume(throwable -> {
                            log.error("error : ", throwable);
                            // 해당 이메일 회원이 이미 존재할 경우
                            if (throwable instanceof RuntimeException
                                    && throwable.getCause() != null
                                    && throwable.getCause().getMessage().equals(BdConstants.Exception.KEY_EMAIL_EXIST)) {
                                return Mono.just(response.setError(MSG_EMAIL_EXIST, false));
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

    /**
     * access 토큰 만료 시 리프레시 토큰으로 새 토큰을 발급하고 리턴한다.
     *
     * @param token token
     * @return response - SignInDto
     */
    @PostMapping("renew")
    public Mono<CommonResponse> renewToken(@RequestBody Token token) {
        CommonResponse response = new CommonResponse();
        return signService.renewToken(token)
                .map(response::setData)
                .onErrorResume(throwable -> {
                    log.error("error", throwable);
                    response.setError(throwable.getMessage());
                    // 리프레시 토큰이 만료됐을 경우
                    if (throwable.getMessage().equals(KEY_TOKEN_EXPIRED)) {
                        response.setError(ResponseCode.CODE_UNAUTHORIZED, MSG_TOKEN_EXPIRED, null);
                    }
                    // 리프레시 토큰이 db와 일치하지 않을 경우
                    else if (throwable.getCause() != null &&
                            throwable.getCause().getMessage().equals(KEY_TOKEN_IS_NOT_CORRECT)) {
                        response.setError(MSG_TOKEN_IS_NOT_CORRECT, null);
                    }
                    return Mono.just(response);
                });
    }
}
