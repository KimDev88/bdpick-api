package com.bdpick.repository;

import com.bdpick.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {


    @Override
    Flux<User> findAll();

//    Mono<User> findById(String userId);

    Mono<User> findByIdAndPassword(String userId, String password);

    @Override
    <S extends User> Mono<S> save(S entity);

    Mono<User> findByEmail(String email);
}
