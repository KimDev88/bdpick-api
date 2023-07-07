package com.bdpick.controller;

import com.bdpick.common.BdUtil;
import com.bdpick.common.security.JwtService;
import com.bdpick.domain.BdFile;
import com.bdpick.domain.FileType;
import com.bdpick.domain.ShopImage;
import com.bdpick.domain.advertisement.AdKeyword;
import com.bdpick.domain.advertisement.ShopAd;
import com.bdpick.domain.keyword.Keyword;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

@RestController
//@RequestMapping(value = PREFIX_API_URL + "/shop-ad", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequestMapping(value = PREFIX_API_URL + "/shop-ads")

@RequiredArgsConstructor
public class ShopAdController {
    private final ShopAdRepository shopAdRepository;
    private final ShopAdDtoRepository shopAdDtoRepository;
    private final ShopImageRepository shopImageRepository;
    private final KeywordRepository keywordRepository;
    private final AdKeywordRepository adKeywordRepository;
    private final FileRepository fileRepository;
    private final JwtService jwtService;
    private final R2dbcEntityTemplate template;

    @GetMapping
    public Mono<CommonResponse> selectShopAds() {


        return template.getDatabaseClient().sql("""
                        SELECT SA.*, SI.ID AS IMAGE_ID, "FILE".ID AS FILE_ID, "FILE".URI
                        FROM SHOP_AD SA
                                 JOIN SHOP_IMAGE SI on SA.SHOP_ID = SI.SHOP_ID AND SI.TYPE LIKE 'A%'
                                 JOIN "FILE" ON SI.FILE_ID = "FILE".ID
                                 LEFT JOIN AD_KEYWORD AK on SA.ID = AK.AD_ID
                                JOIN KEYWORD K on AK.KEYWORD_ID = K.ID""")
                .fetch().all()
                .map(stringObjectMap -> {
                    System.out.println("stringObjectMap = " + stringObjectMap);
                    return stringObjectMap;
                })
                .then(Mono.just(new CommonResponse()));

//        return shopAdDtoRepository.findShopAdDtosByShopIdIsNotNullOrderByCreatedAtDesc()
//                .map(shopAdDto -> {
//                    return shopAdDto;
//                }).then(Mono.just(new CommonResponse()));

    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Transactional
    public Mono<CommonResponse> createShopAd(@RequestHeader Map<String, Object> headerMap,
                                             @RequestPart("files") Flux<FilePart> filePartFlux,
                                             @RequestPart("fileTypes") Flux<String> typeFlux,
                                             @RequestPart("shop") ShopAd shopAd) {
        CommonResponse response = new CommonResponse();
        AtomicLong createdAdId = new AtomicLong();

        return Flux.zip(filePartFlux, typeFlux)
                .flatMap(objects -> BdUtil.uploadFile(objects.getT1(), objects.getT2(), "images")).
                zipWith(fileRepository.getSequence().repeat())
                .map(objects -> {
                    BdFile file = objects.getT1();
                    long fileSeq = objects.getT2();
                    file.setNew(true);
                    file.setId(fileSeq);
                    return file;
                })
                // 파일 insert
                .flatMap(fileRepository::save)
                .transformDeferredContextual((bdFileFlux, contextView) -> {
                    Mono<Long> shopId = Mono.just(contextView.get("shopId"));
                    return bdFileFlux.zipWith(Mono.just(shopId).repeat());
                })
                .flatMap(objects -> Mono.just(objects.getT1()).zipWith(objects.getT2()))
                .map(objects -> {
                    BdFile file = objects.getT1();
                    long shopId = objects.getT2();
                    ShopImage shopImage = new ShopImage();
                    shopImage.setNew(true);
                    shopImage.setShopId(shopId);
                    shopImage.setFileId(file.getId());
                    shopImage.setType(FileType.valueOf(file.getFileType()));
                    shopImage.setDisplayOrder(1);
                    shopImage.setCreatedAt(LocalDateTime.now());
                    return shopImage;
                })
                .zipWith(shopImageRepository.getSequence().repeat())
                .flatMap(objects -> {
                    ShopImage shopImage = objects.getT1();
                    long shopImageId = objects.getT2();
                    shopImage.setId(shopImageId);
                    return shopImageRepository.save(shopImage);
                }).then(shopAdRepository.getSequence())

                .transformDeferredContextual((sequenceMono, contextView) -> sequenceMono
                        .map(adId -> {
                            shopAd.setNew(true);
                            shopAd.setId(adId);
                            shopAd.setShopId(contextView.get("shopId"));
                            shopAd.setCreatedAt(LocalDateTime.now());
                            shopAd.setUpdatedAt(LocalDateTime.now());
                            return shopAd;
                        }))
                .flatMap(shopAdRepository::save)
                .flatMapMany(shopAd1 -> {
                    createdAdId.set(shopAd1.getId());
                    List<String> keywordList = shopAd1.getKeywordList();
                    return Flux.fromArray(keywordList.toArray());
                }).flatMap(keyword -> {
                    String keywordStr = (String) keyword;
                    Keyword keywordObj = new Keyword();
                    keywordObj.setNew(true);
                    keywordObj.setKeyword(keywordStr);
                    keywordObj.setCreatedAt(LocalDateTime.now());
                    return keywordRepository.findKeywordByKeyword(keywordStr).switchIfEmpty(Mono.just(keywordObj));
                })
                .flatMap(keyword -> {
                    // 키워드가 없을 경우 생성
                    if (keyword.getId() == null) {
                        return Mono.defer(keywordRepository::getSequence)
                                .flatMap(id -> {
                                    keyword.setId(id);
                                    return keywordRepository.save(keyword);
                                });
                    } else {
                        return Mono.just(keyword);
                    }
                }).zipWith(adKeywordRepository.getSequence())
                .flatMap(keyword -> {
                    AdKeyword adKeyword = new AdKeyword();
                    adKeyword.setId(keyword.getT2());
                    adKeyword.setNew(true);
                    adKeyword.setKeywordId(keyword.getT1().getId());
                    adKeyword.setAdId(createdAdId.get());
                    adKeyword.setCreatedAt(LocalDateTime.now());
                    return adKeywordRepository.save(adKeyword);
                })
                .contextWrite(context -> context.put("shopId", jwtService.getShopIdByHeaderMap(headerMap)))
                .then(Mono.just(response));

    }
}
