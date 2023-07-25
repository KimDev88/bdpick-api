package com.bdpick.repository;

import com.bdpick.domain.entity.advertisement.ShopAd;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ShopAdRepository {
    private final Stage.SessionFactory factory;

    /**
     * create shop ad
     *
     * @param shopAd entity
     * @return saved entity
     */
    public Mono<ShopAd> save(ShopAd shopAd) {
        factory.withTransaction((session, transaction) ->
                session.persist(shopAd)).toCompletableFuture().join();
        return Mono.just(shopAd);
    }
}
