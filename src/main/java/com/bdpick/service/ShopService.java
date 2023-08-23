package com.bdpick.service;

import com.bdpick.common.BdConstants;
import com.bdpick.common.BdUtil;
import com.bdpick.domain.ShopFileType;
import com.bdpick.domain.entity.BdFile;
import com.bdpick.domain.entity.common.Image;
import com.bdpick.domain.entity.shop.Shop;
import com.bdpick.domain.entity.shop.ShopImage;
import com.bdpick.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final Stage.SessionFactory factory;
    private final ShopRepository shopRepository;

//    /**
//     * create shop
//     *
//     * @param shop entity
//     * @return created entity
//     */
//    public Mono<CommonResponse> createShop(Shop shop) {
//        CommonResponse commonResponse = new CommonResponse();
//        return Mono.just(commonResponse.setData(shopRepository.save(shop)));
//    }

    /**
     * create shop
     *
     * @param headerMap  headerMap
     * @param files      files
     * @param filesTypes filesTypes
     * @param shop       shop
     * @return created shop
     */
    @Transactional
    public Mono<Shop> createShop(@NonNull Map<String, Object> headerMap,
                                 @NonNull Flux<FilePart> files,
                                 @NonNull Flux<String> filesTypes,
                                 @NonNull Shop shop) {
        List<ShopImage> imageList = new ArrayList<>();
        AtomicReference<Shop> shopReference = new AtomicReference<>();
        return factory.withTransaction(session -> {
                    return shopRepository.save(shop, session)
                            .thenApply((createdShop) ->
                                    {
                                        shopReference.set(createdShop);
                                        return files.zipWith(filesTypes)
                                                .doOnNext(objects -> {
                                                    String type = objects.getT2();
                                                    ShopImage shopImage = new ShopImage();
                                                    Image image = new Image();
                                                    BdFile bdFile = BdUtil.uploadFile(objects.getT1(), type, BdConstants.DIRECTORY_NAME_IMAGES).block();

                                                    image.setDisplayOrder(1L);
                                                    image.setBdFile(bdFile);

                                                    shopImage.setShop(createdShop);
                                                    shopImage.setImage(image);
                                                    shopImage.setType(ShopFileType.valueOf(type));
                                                    imageList.add(shopImage);
                                                });
                                    }
                            )
                            .thenApply(unused -> {
                                Shop returnShop = shopReference.get();
                                returnShop.setImageList(imageList);
                                return Mono.just(returnShop);
                            });
                })
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }
}
