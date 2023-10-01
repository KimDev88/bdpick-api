package com.bdpick.shop.repository.impl;

import com.bdpick.shop.domain.Shop;
import com.bdpick.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Shop Repository
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ShopRepositoryImpl implements ShopRepository {
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
        factory.withSession(session -> session.find(Shop.class, shop.getId())
                        .thenAccept(findedShop -> System.out.println("shop = " + findedShop)))
                .toCompletableFuture().join();
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
                .thenCompose(rtnShop -> {
                    if (rtnShop != null) {
                        return Stage.fetch(rtnShop.getImageList())
                                .thenApply(imageList -> {
                                    rtnShop.setImageList(imageList);
                                    return rtnShop;
                                });
                    }
                    return CompletableFuture.completedStage(null);
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
                .thenCompose(shop -> {
                            if (shop != null) {
                                return Stage.fetch(shop.getImageList())
                                        .thenApply(shopImages -> {
                                            shop.setImageList(shopImages);
                                            return shop;
                                        });
                            } else return CompletableFuture.completedStage(null);
                        }
                )
                .thenApply(rtnShop -> {
                    log.info("rtnShop = " + rtnShop);
                    return rtnShop;
                });
    }

    /**
     * find shop is last created
     *
     * @param session session
     * @return shop
     */
    public CompletionStage<Shop> findShopIsLastCreated(Stage.Session session) {
        return session.createQuery("select s from Shop s order by s.createdAt desc", Shop.class)
                .setMaxResults(1)
                .getSingleResultOrNull()
                .thenCompose(shop ->
                        {
                            if (shop != null) {
                                return Stage.fetch(shop.getImageList())
                                        .thenApply(imageList -> {
                                            shop.setImageList(imageList);
                                            return shop;
                                        });
                            } else {
                                return CompletableFuture.completedStage(null);
                            }
                        }
                );
    }
}