package com.bdpick.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

/**
 * user service test class
 */
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    /**
     * id로 해당 user 조회 테스트
     * <br/>
     * 해당 아이디의 회원이 존재하지 않을 경우 user == null
     *
     * @param id user id
     */
    @ParameterizedTest
//    @ValueSource(strings = {"1", "su240", ""})
    @ValueSource(strings = {"1", "su240", ""})
    public void findByid(String id) {
        StepVerifier.create(userService.findById(id))
//                .expectNextMatches(user -> id.equals(user.getId()))
                .verifyComplete();

    }
}
