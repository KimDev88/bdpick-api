//package com.bdpick.repository;
//
//import com.bdpick.domain.AdImage;
//import com.bdpick.domain.ShopImage;
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Mono;
//
//@Repository
//public interface AdImageRepository extends ReactiveCrudRepository<AdImage, Long> {
//
//    <S extends AdImage> Mono<S> save(S image);
//
//    @Query("SELECT SEQ_AD_IMAGE.nextval FROM DUAL")
//    Mono<Long> getSequence();
//}