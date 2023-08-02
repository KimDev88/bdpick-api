package com.bdpick.service;

import com.bdpick.domain.entity.User;
import com.bdpick.repository.SignRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * sign service class
 */
@Service
@RequiredArgsConstructor
public class SignService {
    private final Stage.SessionFactory factory;
    private final SignRepository signRepository;

    /**
     * sign up
     *
     * @param user 등록할 user
     * @return 등록된 user
     */
    public Mono<User> up(@RequestBody User user) {
        return factory.withTransaction(session
                        -> signRepository.up(user, session))
                .thenApply(Mono::justOrEmpty)
                .toCompletableFuture().join();
    }

    /**
     * check is id available
     *
     * @param id checking id
     * @return true : available , false : unavailable
     */
    public Mono<Boolean> isAvailableId(String id) {
        return factory.withSession(session
                -> session.find(User.class, id)
                .thenApply(Objects::isNull)
                .thenApply(Mono::just)).toCompletableFuture().join();

    }

}
