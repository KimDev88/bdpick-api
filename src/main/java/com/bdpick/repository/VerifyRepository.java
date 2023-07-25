//package com.bdpick.repository;
//
//import com.bdpick.domain.Verify;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import reactor.core.publisher.Mono;
//
//public interface VerifyRepository extends ReactiveCrudRepository<Verify, String> {
//
//    Mono<Verify> findFirstByEmailOrderByCreatedAtDesc(String email);
//}
