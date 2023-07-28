package com.bdpick.controller;

import com.bdpick.domain.entity.Keyword;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.service.ShopAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

@RestController
//@RequestMapping(value = PREFIX_API_URL + "/shop-ad", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequestMapping(value = PREFIX_API_URL + "/shop-ads")

@RequiredArgsConstructor
public class ShopAdController {
    private final ShopAdService shopAdService;
    //    private final ShopAdRepository.java shopAdRepository;
//    private final ShopAdDtoRepository shopAdDtoRepository;
//    private final AdImageRepository adImageRepository;
//    private final KeywordRepository keywordRepository;
//    private final AdKeywordRepository adKeywordRepository;
//    private final FileRepository fileRepository;
//    private final JwtService jwtService;
//    private final R2dbcEntityTemplate template;
//    private final DatabaseClient client;
//    private final ShopAdService shopAdService;
//
//    Flux<ShopAdDto> selectShopAdDto(Pageable pageable) {
//        if (pageable == null) {
//            pageable = Pageable.ofSize(500);
//        }
//
//        return client.sql("""
//                        SELECT DISTINCT SA.*, AI.ID AS IMAGE_ID, "FILE".ID AS FILE_ID, "FILE".URI AS FILE_URI
//                        FROM SHOP_AD SA
//                                 JOIN AD_IMAGE AI on SA.ID = AI.AD_ID
//                                 JOIN "FILE" ON AI.FILE_ID = "FILE".ID
//                                 LEFT JOIN AD_KEYWORD AK on SA.ID = AK.AD_ID
//                                JOIN KEYWORD K on AK.KEYWORD_ID = K.ID
//                        ORDER BY SA.ID DESC
//                        OFFSET :offset ROWS FETCH FIRST :size ROWS ONLY
//                                """)
//                .bind("offset", pageable.getOffset())
//                .bind("size", pageable.getPageSize())
//                .fetch()
//                .all()
////                .repeat(30)
//                .flatMap(stringObjectMap -> {
//                    ShopAdDto dto = BdUtil.objectConvert(stringObjectMap, ShopAdDto.class);
//                    return client.sql("""
//                                    SELECT K.KEYWORD FROM AD_KEYWORD AK
//                                              JOIN KEYWORD K on AK.KEYWORD_ID = K.ID
//                                    WHERE AD_ID = :adId
//                                    """)
//                            .bind("adId", stringObjectMap.get("id"))
//                            .fetch().all()
//                            .map(objectMap -> String.format("#%s", objectMap.get("KEYWORD")))
//                            .collectList()
//                            .map(list -> {
//                                dto.setKeywordList(list);
//                                dto.setKeywords(String.join(" ", list));
//                                return dto;
//                            });
//                });
//    }

//    @GetMapping
//    public Mono<CommonResponse> selectShopAds() {
//        CommonResponse response = new CommonResponse();
//        return selectShopAdDto(null)
//                .collectList()
//                .map(response::setData);
//    }

    /**
     * 가게 광고를 생성한다.
     *
     * @param headerMap    token map
     * @param filePartFlux multipart file flux
     * @param typeFlux     file type flux
     * @param shopAd       shop ad dto
     * @return true, error
     */
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Mono<CommonResponse> createShopAd(@RequestHeader Map<String, Object> headerMap,
                                             @RequestPart("files") Flux<FilePart> filePartFlux,
                                             @RequestPart("fileTypes") Flux<String> typeFlux,
                                             @RequestPart("shop") ShopAd shopAd) {
        return shopAdService.createShopAd(headerMap, filePartFlux, typeFlux, shopAd);

    }
}
