package com.bdpick.advertisement.web.rest;

import com.bdpick.advertisement.domain.ShopAd;
import com.bdpick.advertisement.service.ShopAdServiceImpl;
import com.bdpick.advertisement.web.rest.dto.ShopAdDto;
import com.bdpick.advertisement.web.rest.mapper.ShopAdMapper;
import com.bdpick.common.request.CommonResponse;
import com.bdpick.common.web.rest.dto.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

@RestController
@RequestMapping(value = PREFIX_API_URL + "/shop-ads")
@RequiredArgsConstructor
@Slf4j
public class ShopAdResource {
    private final ShopAdServiceImpl shopAdService;

    /**
     * 가게 광고를 생성한다.
     *
     * @param headerMap    token map
     * @param filePartFlux multipart file flux
     * @param typeFlux     file type flux
     * @param shopAd       shop ad dto
     * @return true, error
     */

//    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @PostMapping
    public Mono<CommonResponse> createShopAd(@RequestHeader Map<String, Object> headerMap,
                                             @RequestPart(value = "files") Flux<FilePart> filePartFlux,
                                             @RequestPart(value = "fileTypes") Flux<String> typeFlux,
                                             @RequestPart(value = "shopAd") ShopAdDto shopAd
    ) {
        CommonResponse commonResponse = new CommonResponse();
        ShopAd param = ShopAdMapper.INSTANCE.DtoToAd(shopAd);
        return shopAdService.createShopAd(headerMap, filePartFlux, typeFlux, param)
                .map(commonResponse::setData)
                .onErrorResume(throwable
                        -> {
                    log.error("error", throwable);
                    return Mono.just(commonResponse.setError().setMessage(throwable.getMessage()));
                });
    }

    /**
     * find shop ads with pageable
     *
     * @param headerMap headerMap
     * @param pageable  pageable
     * @return shop ads
     */
    @GetMapping
    public Mono<CommonResponse> findShopAds(@RequestHeader Map<String, Object> headerMap, Pageable pageable) {
        CommonResponse commonResponse = new CommonResponse();
        return shopAdService.findShopAds(headerMap, pageable)
                .collectList()
                .map(commonResponse::setData)
                .onErrorResume(throwable -> {
                    log.error("error", throwable);
                    return Mono.just(commonResponse.setError().setMessage(throwable.getMessage()));
                });
    }
}
