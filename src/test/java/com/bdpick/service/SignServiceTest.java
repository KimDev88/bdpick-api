package com.bdpick.service;

import com.bdpick.domain.UserType;
import com.bdpick.domain.entity.User;
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

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("su240");
        user.setType(UserType.N);
        user.setEmail("yong2407@hanmail.net");
        user.setPassword("gs225201");

    }


    /**
     * sign up
     */
    @Test
    @Transactional
    public void up() {
        // 해당 계정이 존재하는지 테스트 후 존재하지 않을 경우에만 생성
        StepVerifier.create(signService.isAvailableId(user.getId()))
                .expectNextMatches(aBoolean -> {
                    System.out.println("aBoolean = " + aBoolean);
                    // 해당 계정 존재하지 않을 경우
                    if (aBoolean) {
                        StepVerifier.create(signService.up(user))
                                .expectNextMatches(user1 -> user1 != null && user1.getId() != null)
                                .verifyComplete();
                    }
                    return true;
                }).verifyComplete();


    }

    @Test
    public void isAvailableId() {
        StepVerifier.create(signService.isAvailableId(user.getId()))
                .expectNextMatches(aBoolean -> {
                    System.out.println("aBoolean = " + aBoolean);
                    return true;
                })
                .verifyComplete();
    }
}
