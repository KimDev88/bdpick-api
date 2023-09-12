package com.bdpick.repository;

import com.bdpick.config.TestConfiguration;
import com.bdpick.domain.entity.User;
import org.hibernate.reactive.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * user repository test class
 */
@SpringBootTest
//    @DataJpaTest
@Import(TestConfiguration.class)
public class UserRepositoryTest {
    @Autowired
    private Stage.SessionFactory factory;
    @Autowired
    private UserRepository userRepository;


    String email = "yong2407@hanmail.net";

    @Test
    public void findByEmail() {
        User user = factory.withSession(session
                -> userRepository.findByEmail(email, session)).toCompletableFuture().join();
        Assertions.assertEquals(user.getEmail(), email);


    }
}
