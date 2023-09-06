package com.bdpick.service;

import com.bdpick.common.MailService;
import com.bdpick.domain.UserType;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.Verify;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
public class SignServiceTest {
    @Autowired
    private SignService signService;
    @Autowired
    private MailService mailService;

    private User user;
    private Verify verify;

    @BeforeEach
    void setUp() {
        String email = "yong2407@hanmail.net";

        user = new User();
        user.setId("su2407");
        user.setType(UserType.N);
        user.setEmail(email);
        user.setPassword("gs225201");
        user.setUuid("TEST");

        verify = new Verify();
        verify.setEmail(email);
        verify.setCode("DM778W");

    }

    @Test
    public void test() {
        signService.test();
//        StepVerifier.create(signService.test(user))
//                .expectNext()
//                .verifyComplete();
    }


    /**
     * sign up
     */
    @Test
    @Transactional
    public void up() {
        StepVerifier.create(signService.up(user))
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
                .expectNextMatches(stringObjectMap ->
                        stringObjectMap != null && !stringObjectMap.isEmpty()
                )
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
    @Test
    public void sendMail() {
        StepVerifier.create(signService.sendMail(user))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    public void verifyEmail() {
        StepVerifier.create(signService.verifyEmail(verify))
                .expectNext(true)
                .verifyComplete();
    }


}
