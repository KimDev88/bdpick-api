package com.bdpick.advertisement.service;


import com.bdpick.advertisement.domain.AdImage;
import com.bdpick.advertisement.domain.ShopAd;
import com.bdpick.advertisement.domain.enumeration.AdFileType;
import com.bdpick.advertisement.repository.impl.AdImageRepositoryImpl;
import com.bdpick.advertisement.repository.impl.ShopAdRepositoryImpl;
import com.bdpick.advertisement.service.impl.ShopAdService;
import com.bdpick.advertisement.web.rest.dto.ShopAdDto;
import com.bdpick.advertisement.web.rest.mapper.ShopAdMapper;
import com.bdpick.common.BdConstants;
import com.bdpick.common.BdUtil;
import com.bdpick.common.domain.BdFile;
import com.bdpick.common.domain.Image;
import com.bdpick.common.web.rest.dto.Pageable;
import com.bdpick.shop.domain.Shop;
import com.bdpick.shop.service.impl.ShopService;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.reactive.stage.Stage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopAdServiceImpl implements ShopAdService {
    private final Stage.SessionFactory factory;
    private final ShopAdRepositoryImpl shopAdRepository;
    private final AdImageRepositoryImpl adImageRepository;
    private final ShopService shopService;

    /**
     * 매장 홍보 생성
     *
     * @param shopAd 홍보 정보
     * @return 생성된 홍보 id
     */
    public Mono<ShopAd> createShopAd(Map<String, Object> headerMap, Flux<FilePart> filePartFlux, Flux<String> typeFlux, ShopAd shopAd) {
        {
            // FIXME headerMap을 이용하여 shopId 조회하여 해당 shop 정보 shopAd의 add 필요
                                /*
                                  광고 저장
                                  1. shop_ad table insert
                                  2. keyword table insert
                                  3. ad_keyword table insert
                                 */
            try {
                Long shopId = shopService.getShopIdByHeaderMap(headerMap);
                Shop shop = new Shop();
                shop.setId(shopId);
                shopAd.setShop(shop);
            } catch (Exception e) {
                log.error("error ", e);
                return Mono.error(e);
            }
            return factory.withTransaction((session, transaction) ->
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
                                                    BdFile createdFile = BdUtil.uploadFile(bdFile, type, BdConstants.DIRECTORY_NAME_IMAGES).block();
                                                    AdImage adImage = new AdImage();
                                                    Image image = new Image();
                                                    image.setBdFile(createdFile);
                                                    image.setDisplayOrder(1D);
                                                    adImage.setShopAd(shopAd);
                                                    adImage.setType(AdFileType.A1);
                                                    adImage.setImage(image);
                                                    adImageRepository.save(adImage, session);
                                                });
                                    })
                                    .thenApply(unused -> Mono.just(shopAd))
                    )
                    .exceptionally(Mono::error)
                    .toCompletableFuture().join();
        }
    }

    /**
     * find last created shopAd
     *
     * @return last created entity
     */
    public Mono<ShopAd> findLastShopAd() {
        return factory.withSession(shopAdRepository::findLastShopAd)
                .thenApply(Mono::just)
                .exceptionally(Mono::error)
                .toCompletableFuture().join();

    }

    /**
     * find shop ads with pageable
     *
     * @param pageable pageable
     * @return shop ad flux
     */
    public Flux<ShopAdDto> findShopAds(Map<String, Object> headerMap, Pageable pageable) {
        try {
            long shopId = shopService.getShopIdByHeaderMap(headerMap);
            Shop shop = new Shop();
            shop.setId(shopId);

            return factory.withSession(session ->
                            shopAdRepository.findShopAds(pageable, shop, session)
                    )
                    .thenApply(shopAds ->
                            Flux.fromArray(ShopAdMapper.INSTANCE.adToDto(shopAds).toArray(new ShopAdDto[0])))
                    .exceptionally(Flux::error)
                    .toCompletableFuture().join();
        } catch (Exception e) {
            log.error("error", e);
            return Flux.error(e);
        }

    }
}
