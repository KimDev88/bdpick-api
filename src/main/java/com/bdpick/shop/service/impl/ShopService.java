package com.bdpick.shop.service.impl;

import com.bdpick.common.request.CommonResponse;
import com.bdpick.shop.domain.Shop;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * ship service interface class
 */
public interface ShopService {
    Mono<Shop> selectMyShop(Map<String, Object> headerMap);

    Mono<Shop> selectShopIsLastCreated();

    Mono<Shop> createShop(Map<String, Object> headerMap,
                          Flux<FilePart> files,
                          Flux<String> filesTypes,
                          Shop shop);

    Mono<CommonResponse> checkRegisterNumber(Shop shop);

    Long getShopIdByUserId(String userId);

    Long getShopIdByHeaderMap(Map<String, Object> headerMap) throws Exception;

}
