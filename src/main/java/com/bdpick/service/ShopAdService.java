package com.bdpick.service;


import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.ShopAdRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;

@Service
@RequiredArgsConstructor
public class ShopAdService {

    private final ShopAdRepository shopAdRepository;


    public Mono<CommonResponse> createShopAd(ShopAd shopAd) {
        CommonResponse commonResponse = new CommonResponse();
//        Long shopId = jwtService.getShopIdByHeaderMap(headerMap);
        Long shopId = 1L;
        shopAd.setShopId(shopId);
        return shopAdRepository.save(shopAd)
                .then(Mono.just(commonResponse.setData(shopAd.getId())));


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
    }
}
