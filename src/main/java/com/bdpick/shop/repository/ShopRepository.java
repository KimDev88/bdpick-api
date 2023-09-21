package com.bdpick.shop.repository;

import com.bdpick.shop.domain.Shop;
import org.hibernate.reactive.stage.Stage;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;

/**
 * shop repository interface
 */
public interface ShopRepository {
    /**
     * create shop
     *
     * @param shop entity
     * @return saved entity
     */
    CompletionStage<Shop> save(Shop shop, Stage.Session session);
    /**
     * find shop by id
     *
     * @param shop shop
     * @return found shop
     */
    Mono<Shop> findShop(Shop shop);
    /**
     * find shop by registerNumber
     *
     * @param shop    shop
     * @param session session
     * @return shop
     */
    CompletionStage<Shop> findShopByRegisterNumber(Shop shop, Stage.Session session);
    /**
     * find shop by userId
     *
     * @param userId  userId
     * @param session session
     * @return found shop
     */
    CompletionStage<Shop> findShopByUserId(String userId, Stage.Session session);
    /**
     * find shop is last created
     *
     * @param session session
     * @return shop
     */
    CompletionStage<Shop> findShopIsLastCreated(Stage.Session session);
}
