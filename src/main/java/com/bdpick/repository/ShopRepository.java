package com.bdpick.repository;

import com.bdpick.domain.entity.Shop;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Shop Repository
 */
@Repository
public class ShopRepository {
    private final Stage.SessionFactory factory;

    ShopRepository(Stage.SessionFactory factory) {
        this.factory = factory;
    }

    /**
     * create shop
     *
     * @param shop entity
     * @return saved entity
     */
    public Mono<Shop> save(Shop shop) {
        factory.withTransaction((session, transaction) ->
                session.persist(shop)).toCompletableFuture().join();
        return Mono.just(shop);
    }

    public Mono<Shop> findShop(Shop shop){
        factory.withSession(session -> {
            return session.find(Shop.class, shop.getId())
                    .thenAccept(findedShop -> {
                        System.out.println("shop = " + findedShop);
                    });
        }).toCompletableFuture().join();
        return Mono.just(shop);
    }

}