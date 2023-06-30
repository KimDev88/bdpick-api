package com.bdpick.repository;

import com.bdpick.domain.User;
import com.bdpick.domain.Verify;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;

public interface VerifyRepository extends ReactiveCrudRepository<Verify, String> {

    Mono<Verify> findFirstByEmailOrderByCreatedAtDesc(String email);
}
