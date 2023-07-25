//package com.bdpick.repository;
//
//import com.bdpick.domain.BdFile;
//import com.bdpick.domain.Shop;
//import org.reactivestreams.Publisher;
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@Repository
//public interface ShopRepository extends ReactiveCrudRepository<Shop, Long> {
//
////    @Query("INSERT INTO SHOP (ID, USER_ID, REGIST_NUMBER, NAME, OWNER_NAME, TYPE, TEL, ADDRESS_ID, ADDRESS_NAME, CREATED_AT, UPDATED_AT)" +
////            " VALUES (SEQ_SHOP.NEXTVAL, :#{#shop.userId}, :#{#shop.registNumber}, :#{#shop.name}, :#{#shop.ownerName}" +
////            ", :#{#shop.type}, :#{#shop.tel}, :#{#shop.addressId}, :#{#shop.addressName}, :#{#shop.createdAt}, :#{#shop.updatedAt})")
////    @Override
////    <S extends Shop> Mono<S> save(S shop);
//
//        @Query("SELECT SEQ_SHOP.nextval FROM DUAL")
//    Mono<Long> getSequence();
//    Mono<Shop> findShopByRegistNumber(String registNumber);
//    Mono<Shop> findShopByUserId(String userId);
//
//}