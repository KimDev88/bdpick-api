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

    /**
     * find user by email
     *
     * @param email   search email
     * @param session session
     * @return found user
     */
    public CompletionStage<User> findByEmail(String email, Stage.Session session) {
        return session.createQuery("select u from User u where email = :email", User.class)
                .setParameter("email", email)
                .getSingleResultOrNull()
                .thenApply(user -> {
                    System.out.println("user = " + user);
                    return user;
                });
//        return factory.openSession(session -> {
//            return session.createQuery("select u from User u where email = :email", User.class)
//                    .setParameter("email", email)
//                    .getSingleResultOrNull()
//                    .thenApply(user -> {
//                        System.out.println("user = " + user);
//                        return user;
//                    });
//        }).toCompletableFuture().join();

//        return session.createQuery("select u from User u where email = :email", User.class)
//                .setParameter("email", email)
//                .getSingleResultOrNull()
//                .thenApply(user -> {
//                    System.out.println("user = " + user);
//                    return user;
//                }).toCompletableFuture().join();
    }
}
