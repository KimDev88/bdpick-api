package com.bdpick.repository;

import com.bdpick.domain.ShopImage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ShopImageRepository extends ReactiveCrudRepository<ShopImage, Long> {


//    @Query("INSERT INTO SHOP_IMAGE (ID, TYPE, SHOP_ID, FILE_ID, DISPLAY_ORDER, CREATED_AT) " +
//            "VALUES(SEQ_SHOP_IMAGE.NEXTVAL, :#{#image.type}, :#{#image.shopId}, :#{#image.fileId}, :#{#image.displayOrder}, :#{#image.createdAt})")
    @Override
    <S extends ShopImage> Mono<S> save(S image);

    @Query("SELECT SEQ_SHOP_IMAGE.nextval FROM DUAL")
    Mono<Long> getSequence();
}