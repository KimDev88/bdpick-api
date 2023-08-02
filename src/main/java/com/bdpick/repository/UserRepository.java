package com.bdpick.repository;

import com.bdpick.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletionStage;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    public CompletionStage<User> findById(String id, Stage.Session session) {
        return session.find(User.class, id);
    }
}
