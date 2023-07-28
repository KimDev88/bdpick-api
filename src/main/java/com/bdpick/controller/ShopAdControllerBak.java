//package com.bdpick.controller;
//
//import com.bdpick.common.BdUtil;
//import com.bdpick.common.security.JwtService;
//import com.bdpick.domain.AdImage;
//import com.bdpick.domain.entity.BdFile;
//import com.bdpick.domain.FileType;
//import com.bdpick.domain.entity.advertisement.AdKeyword;
//import com.bdpick.domain.entity.advertisement.Keyword;
//import com.bdpick.domain.keyword.Keyword;
//import com.bdpick.domain.request.CommonResponse;
//import com.bdpick.dto.ShopAdDto;
//import com.bdpick.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
//import org.springframework.http.MediaType;
//import org.springframework.http.codec.multipart.FilePart;
//import org.springframework.r2dbc.core.DatabaseClient;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicLong;
//
//import static com.bdpick.common.BdConstants.PREFIX_API_URL;
//
//@RestController
////@RequestMapping(value = PREFIX_API_URL + "/shop-ad", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
//@RequestMapping(value = PREFIX_API_URL + "/shop-ads")
//
//@RequiredArgsConstructor
//public class ShopAdControllerBak {
//    private final ShopAdRepository.java shopAdRepository;
//    private final ShopAdDtoRepository shopAdDtoRepository;
//    private final AdImageRepository adImageRepository;
//    private final KeywordRepository keywordRepository;
//    private final AdKeywordRepository adKeywordRepository;
//    private final FileRepository fileRepository;
//    private final JwtService jwtService;
//    private final R2dbcEntityTemplate template;
//    private final DatabaseClient client;
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
//
//    @GetMapping
//    public Mono<CommonResponse> selectShopAds() {
//        CommonResponse response = new CommonResponse();
//        return selectShopAdDto(null)
//                .collectList()
//                .map(response::setData);
//    }
//
//    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
//    @Transactional
//    public Mono<CommonResponse> createShopAd(@RequestHeader Map<String, Object> headerMap,
//                                             @RequestPart("files") Flux<FilePart> filePartFlux,
//                                             @RequestPart("fileTypes") Flux<String> typeFlux,
//                                             @RequestPart("shop") Keyword shopAd) {
//        CommonResponse response = new CommonResponse();
//        AtomicLong createdAdId = new AtomicLong();
//
//        return shopAdRepository.getSequence()
//                .transformDeferredContextual((sequenceMono, contextView) -> sequenceMono
//                        .map(adId -> {
//                            shopAd.setNew(true);
//                            shopAd.setId(adId);
//                            shopAd.setShopId(contextView.get("shopId"));
//                            shopAd.setCreatedAt(LocalDateTime.now());
//                            shopAd.setUpdatedAt(LocalDateTime.now());
//                            return shopAd;
//                        }))
//                .flatMap(shopAdRepository::save)
//                .flatMapMany(shopAd1 -> {
//                    createdAdId.set(shopAd1.getId());
//                    List<String> keywordList = shopAd1.getKeywordList();
//                    return Flux.fromArray(keywordList.toArray());
//                }).flatMap(keyword -> {
//                    String keywordStr = (String) keyword;
//                    Keyword keywordObj = new Keyword();
//                    keywordObj.setNew(true);
//                    keywordObj.setKeyword(keywordStr);
//                    keywordObj.setCreatedAt(LocalDateTime.now());
//                    return keywordRepository.findKeywordByKeyword(keywordStr).switchIfEmpty(Mono.just(keywordObj));
//                })
//                .flatMap(keyword -> {
//                    // 키워드가 없을 경우 생성
//                    if (keyword.getId() == null) {
//                        return Mono.defer(keywordRepository::getSequence)
//                                .flatMap(id -> {
//                                    keyword.setId(id);
//                                    return keywordRepository.save(keyword);
//                                });
//                    } else {
//                        return Mono.just(keyword);
//                    }
//                })
//                .mapNotNull(Keyword::getId)
//                .distinct()
//                .flatMap((keywordId) -> {
//                    // 해당 키워드가 존재할 경우
//                    return adKeywordRepository.findAdKeywordByKeywordIdAndAdId(keywordId, createdAdId.get())
//                            // 해당 키워드가 존재하지 않을 경우
//                            .switchIfEmpty(adKeywordRepository.getSequence()
//                                    .map(adKeywordId -> {
//                                        AdKeyword adKeyword = new AdKeyword();
//                                        adKeyword.setId(adKeywordId);
//                                        adKeyword.setNew(true);
//                                        adKeyword.setKeywordId(keywordId);
//                                        adKeyword.setAdId(createdAdId.get());
//                                        adKeyword.setCreatedAt(LocalDateTime.now());
//                                        return adKeyword;
//                                    })
//                                    .flatMap(adKeywordRepository::save));
//                })
//                .thenMany(Flux.zip(filePartFlux, typeFlux))
//                .flatMap(objects -> BdUtil.uploadFile(objects.getT1(), objects.getT2(), "images")).
//                zipWith(fileRepository.getSequence().repeat())
//                .map(objects -> {
//                    BdFile file = objects.getT1();
//                    long fileSeq = objects.getT2();
//                    file.setNew(true);
//                    file.setId(fileSeq);
//                    return file;
//                })
//                // 파일 insert
//                .flatMap(fileRepository::save)
//                .map(file -> {
//                    AdImage adImage = new AdImage();
//                    adImage.setNew(true);
////                    adImage.setShopId(shopId);
//                    adImage.setFileId(file.getId());
//                    adImage.setType(FileType.valueOf(file.getFileType()));
//                    adImage.setDisplayOrder(1);
//                    adImage.setCreatedAt(LocalDateTime.now());
//                    return adImage;
//                })
//                .zipWith(adImageRepository.getSequence().repeat())
//                .flatMap(objects -> {
//                    AdImage adImage = objects.getT1();
//                    long shopImageId = objects.getT2();
//                    adImage.setId(shopImageId);
//                    adImage.setAdId(createdAdId.get());
//                    return adImageRepository.save(adImage);
//                })
//
//                .contextWrite(context -> context.put("shopId", jwtService.getShopIdByHeaderMap(headerMap)))
//                .then(Mono.just(response.setData(true)));
//
//    }
//}
