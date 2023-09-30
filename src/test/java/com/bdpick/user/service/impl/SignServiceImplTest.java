package com.bdpick.user.service.impl;

import com.bdpick.mail.service.impl.MailServiceImpl;
import com.bdpick.common.security.JwtService;
import com.bdpick.user.web.rest.mapper.UserMapper;
import com.bdpick.user.domain.User;
import com.bdpick.user.domain.enumeration.UserType;
import com.bdpick.user.web.rest.dto.Token;
import com.bdpick.user.web.rest.dto.UserDto;
import com.bdpick.user.domain.Device;
import com.bdpick.user.domain.Verify;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.Objects;

/**
 * sign service test class
 */
@SpringBootTest
public class SignServiceImplTest {
    @Autowired
    private SignServiceImpl signService;
    @Autowired
    private MailServiceImpl mailServiceImpl;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private DeviceServiceImpl deviceService;

    private UserDto user;
    private Verify verify;
    private Device device;

    @BeforeEach
    void setUp() {
        String email = "yong2407@hanmail.net";
        String userId = "su2407";

        user = new UserDto();
        user.setId(userId);
        user.setType(UserType.N);
        user.setEmail(email);
        user.setPassword("gs225201");
        user.setUuid("TEMP");

        verify = new Verify();
        verify.setEmail(email);
        verify.setCode("DM778W");

        device = new Device();
        device.setUser(user);
        device.setUuid("TEST");


    }

    /**
     * sign up
     */
    @Test
    public void up() {
        User param = UserMapper.INSTANCE.DtoToUser(user);
        StepVerifier.create(signService.up(param))
                .expectNextMatches(user1 -> user1 != null && user1.getId() != null)
                .verifyComplete();

        // 해당 계정이 존재하는지 테스트 후 존재하지 않을 경우에만 생성
//        StepVerifier.create(signService.isAvailableId(user.getId()))
//                .expectNextMatches(aBoolean -> {
//                    System.out.println("aBoolean = " + aBoolean);
//                    // 해당 계정 존재하지 않을 경우
//                    if (aBoolean) {
//                        StepVerifier.create(signService.up(user))
//                                .expectNextMatches(user1 -> user1 != null && user1.getId() != null)
//                                .verifyComplete();
//                    }
//                    return true;
//                }).verifyComplete();


    }

    /**
     * sign in test
     */
    @Test
    public void in() {
        StepVerifier.create(signService.in(user))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    /**
     * renew token
     */
    @Test
    public void renewToken() {
        // 토큰 검증을 위해 실제 로그인 시 사용한 refresh token 조회
        Device rtnDevice = deviceService.findDeviceByUserAndUuid(device).block();
        Token token = new Token(jwtService.createAccessToken(user.getId()), Objects.requireNonNull(rtnDevice).getRefreshToken());
        StepVerifier.create(signService.renewToken(token))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    /**
     * check is available id
     */
    @Test
    public void isAvailableId() {
        StepVerifier.create(signService.isAvailableId(user.getId()))
                .expectNextMatches(aBoolean -> {
                    System.out.println("aBoolean = " + aBoolean);
                    return true;
                })
                .verifyComplete();
    }

    /**
     * find is email exist
     */
//    @Test
//    public void findEmailExist() {
//        StepVerifier.create(signService.findEmailExist(user.getEmail()))
//                .expectNext(true)
//                .verifyComplete();
//    }

    /**
     * send mail test
     */
    @Test
    public void sendMail() {
        StepVerifier.create(signService.sendMail(user))
                .expectNext(true)
                .verifyComplete();
    }

    /**
     * verify email test
     */
    @Test
    public void verifyEmail() {
        StepVerifier.create(signService.verifyEmail(verify))
                .expectNext(true)
                .verifyComplete();
    }


}
