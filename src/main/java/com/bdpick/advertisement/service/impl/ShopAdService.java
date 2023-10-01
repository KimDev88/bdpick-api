package com.bdpick.advertisement.service.impl;

import com.bdpick.advertisement.domain.ShopAd;
import com.bdpick.advertisement.web.rest.dto.ShopAdDto;
import com.bdpick.common.web.rest.dto.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ShopAdService {
    Mono<ShopAd> createShopAd(Map<String, Object> headerMap, Flux<FilePart> filePartFlux, Flux<String> typeFlux, ShopAd shopAd);

    Mono<ShopAd> findLastShopAd();

    Flux<ShopAdDto> findShopAds(Map<String, Object> headerMap, Pageable pageable);


}
