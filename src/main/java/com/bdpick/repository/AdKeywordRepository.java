//package com.bdpick.repository;
//
//import com.bdpick.domain.advertisement.AdKeyword;
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Mono;
//
//@Repository
//public interface AdKeywordRepository extends ReactiveCrudRepository<AdKeyword, Long> {
//
//    @Query("SELECT SEQ_AD_KEYWORD.nextval FROM DUAL")
//    Mono<Long> getSequence();
//
//    Mono<AdKeyword> findAdKeywordByKeywordIdAndAdId(Long keywordId, Long adId);
//}