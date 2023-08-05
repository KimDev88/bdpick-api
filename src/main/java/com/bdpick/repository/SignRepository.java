package com.bdpick.repository;

import com.bdpick.domain.entity.User;
import org.hibernate.reactive.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletionStage;

/**
 * sign repository class
 */
@Repository
public class SignRepository {
    /**
     * sign up
     *
     * @param user    등록할 user
     * @param session session
     * @return 등록된 user
     */
    public CompletionStage<User> up(User user, @NotNull Stage.Session session) {
        return session.persist(user)
                .thenApply(unused -> user);
    }

}
