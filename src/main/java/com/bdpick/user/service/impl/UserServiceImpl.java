package com.bdpick.user.service.impl;

import com.bdpick.user.domain.User;
import com.bdpick.user.repository.impl.UserRepositoryImpl;
import com.bdpick.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * user service class
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Stage.SessionFactory factory;
    private final UserRepositoryImpl userRepository;

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
