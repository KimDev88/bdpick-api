package com.bdpick.repository;

import com.bdpick.domain.entity.shop.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;

/**
 * Shop Repository
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ShopRepository {
    private final Stage.SessionFactory factory;

    /**
     * create shop
     *
     * @param shop entity
     * @return saved entity
     */
    public CompletionStage<Shop> save(Shop shop, Stage.Session session) {
        return session.persist(shop)
                .thenApply(unused -> shop);
    }

    /**
     * find shop by id
     *
     * @param shop shop
     * @return found shop
     */
    public Mono<Shop> findShop(Shop shop) {
        factory.withSession(session -> {
            return session.find(Shop.class, shop.getId())
                    .thenAccept(findedShop -> {
                        System.out.println("shop = " + findedShop);
                    });
        }).toCompletableFuture().join();
        return Mono.just(shop);
    }

    /**
     * find shop by registerNumber
     *
     * @param shop    shop
     * @param session session
     * @return shop
     */
    public CompletionStage<Shop> findShopByRegisterNumber(Shop shop, Stage.Session session) {
        return session.createQuery("SELECT s FROM Shop s WHERE registerNumber = :registerNumber", Shop.class)
                .setParameter("registerNumber", shop.getRegisterNumber())
                .getSingleResultOrNull()
                .thenApply(rtnShop -> {
                    System.out.println("rtnShop = " + rtnShop);
                    return rtnShop;
                });
    }

    /**
     * find shop by userId
     *
     * @param userId  userId
     * @param session session
     * @return found shop
     */
    public CompletionStage<Shop> findShopByUserId(String userId, Stage.Session session) {
        return session.createQuery("SELECT s FROM Shop s  join fetch s.user WHERE s.user.id = :userId ", Shop.class)
                .setParameter("userId", userId)
                .getSingleResultOrNull()
                // 이미지 리스트 lazy loading
                .thenCompose(shop -> Stage.fetch(shop.getImageList())
                        .thenApply(shopImages -> {
                            shop.setImageList(shopImages);
                            return shop;
                        }))
                .thenApply(rtnShop -> {
                    log.info("rtnShop = " + rtnShop);
                    return rtnShop;
                });
    }
}