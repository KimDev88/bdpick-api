package com.bdpick.repository;

import com.bdpick.config.TestConfiguration;
import com.bdpick.user.domain.User;
import com.bdpick.user.repository.impl.UserRepositoryImpl;
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
public class UserRepositoryImplTest {
    @Autowired
    private Stage.SessionFactory factory;
    @Autowired
    private UserRepositoryImpl userRepository;


    String email = "yong2407@hanmail.net";

    @Test
    public void findByEmail() {
        User user = factory.withSession(session
                -> userRepository.findByEmail(email, session)).toCompletableFuture().join();
        Assertions.assertEquals(user.getEmail(), email);


    }
}
