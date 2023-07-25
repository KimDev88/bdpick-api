//package com.bdpick.repository;
//
//import com.bdpick.domain.keyword.Keyword;
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Mono;
//
//@Repository
//public interface KeywordRepository extends ReactiveCrudRepository<Keyword, Long> {
//
//    @Query("SELECT SEQ_KEYWORD.nextval FROM DUAL")
//    Mono<Long> getSequence();
//
//    Mono<Keyword> findKeywordByKeyword(String keyword);
//}