package com.bdpick.service;

import com.bdpick.domain.entity.User;
import com.bdpick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * user service class
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final Stage.SessionFactory factory;
    private final UserRepository userRepository;

    /**
     * find user by id
     *
     * @param id user id
     * @return find user
     */
    public Mono<User> findById(String id) {
        return factory.withSession(session ->
                        userRepository.findById(id, session))
                .thenApply(Mono::justOrEmpty)
                .toCompletableFuture().join();
    }
}
