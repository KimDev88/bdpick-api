package com.bdpick.repository;

import com.bdpick.domain.advertisement.ShopAd;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ShopAdRepository extends ReactiveCrudRepository<ShopAd, Long> {

    @Query("SELECT SEQ_SHOP_AD.nextval FROM DUAL")
    Mono<Long> getSequence();
}