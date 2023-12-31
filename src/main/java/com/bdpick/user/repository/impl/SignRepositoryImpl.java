package com.bdpick.user.repository.impl;

import com.bdpick.user.domain.User;
import com.bdpick.user.repository.SignRepository;
import lombok.NonNull;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletionStage;

/**
 * sign repository class
 */
@Repository
public class SignRepositoryImpl implements SignRepository {
    /**
     * sign up
     *
     * @param user    등록할 user
     * @param session session
     * @return 등록된 user
     */
    public CompletionStage<User> up(User user, @NonNull Stage.Session session) {
        return session.persist(user)
                .thenApply(unused -> user);
    }

}
