package com.bdpick.service;


import com.bdpick.common.BdUtil;
import com.bdpick.domain.FileType;
import com.bdpick.domain.entity.AdImage;
import com.bdpick.domain.entity.BdFile;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.*;
import io.vertx.sqlclient.Tuple;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ShopAdService {
    private final Stage.SessionFactory factory;
    private final ShopAdRepository shopAdRepository;
    private final ShopRepository shopRepository;
    private final KeywordRepository keywordRepository;
    private final FileRepository fileRepository;
    private final AdImageRepository adImageRepository;


    /**
     * 매장 홍보 생성
     *
     * @param shopAd 홍보 정보
     * @return 생성된 홍보 id
     */
    @Transactional
    public Mono<CommonResponse> createShopAd(Map<String, Object> headerMap, Flux<FilePart> filePartFlux, Flux<String> typeFlux, ShopAd shopAd) {
        CommonResponse commonResponse = new CommonResponse();
        return factory.withTransaction((session, transaction) ->
                        // FIXME headerMap을 이용하여 shopId 조회하여 해당 shop 정보 shopAd의 add 필요
                /*
                  광고 저장
                  1. shop_ad table insert
                  2. keyword table insert
                  3. ad_keyword table insert
                 */
                        shopAdRepository.save(shopAd, session)
                                .thenRun(() -> {
                                    // 광고 이미지 저장
                                    List<FilePart> filePartList = filePartFlux.toStream().toList();
                                    List<String> typeFluxList = typeFlux.toStream().toList();
                                    IntStream
                                            .range(0, Math.min(filePartList.size(), typeFluxList.size()))
                                            .mapToObj(i -> Tuple.of(filePartList.get(i), typeFluxList.get(i)))
                                            .forEach(tuple -> {
                                                /*
                                                  전달받은 filePart, fileType으로 파일 업로드 후 db 저장
                                                  <br/>
                                                  1. file upload
                                                  2. file table insert
                                                  3. ad_image table insert
                                                 */
                                                FilePart bdFile = tuple.get(FilePart.class, 0);
                                                String type = tuple.get(String.class, 1);
                                                BdFile createdFile = BdUtil.uploadFile(bdFile, type, "images").block();
                                                AdImage adImage = new AdImage();
                                                adImage.setBdFile(createdFile);
                                                adImage.setShopAd(shopAd);
                                                adImage.setType(FileType.A1);
                                                adImage.setDisplayOrder(1D);
                                                adImageRepository.save(adImage, session);
                                            });
                                })
                                .handle((unused, throwable) -> {
                                    if (throwable != null) {
                                        transaction.markForRollback();
                                    }
                                    return Mono.just(commonResponse);
                                }))
                .toCompletableFuture().join();
    }
}
