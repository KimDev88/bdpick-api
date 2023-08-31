package com.bdpick.service;

import com.bdpick.common.BdConstants;
import com.bdpick.common.BdUtil;
import com.bdpick.domain.ShopFileType;
import com.bdpick.domain.entity.BdFile;
import com.bdpick.domain.entity.common.Image;
import com.bdpick.domain.entity.shop.Shop;
import com.bdpick.domain.entity.shop.ShopImage;
import com.bdpick.repository.ShopRepository;
import io.vertx.sqlclient.Tuple;
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
import java.util.stream.IntStream;

import static com.bdpick.common.BdConstants.Exception.KEY_DUPLICATE_REGISTER;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final Stage.SessionFactory factory;
    private final ShopRepository shopRepository;

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
        return factory.withTransaction(session -> {
                    // 사업자등록번호로 조회
                    return shopRepository.findShopByRegistNumber(shop, session)
                            .thenCompose(foundShop -> {
                                // 해당 사업자 등록번호의 매장이 이미 존재할 경우
                                if (foundShop != null) {
                                    throw new RuntimeException(KEY_DUPLICATE_REGISTER);
                                }
                                // 매장 이미지 저장
                                List<FilePart> filePartList = files.toStream().toList();
                                List<String> fileTypeList = filesTypes.toStream().toList();
                                IntStream.range(0, Math.min(filePartList.size(), fileTypeList.size()))
                                        .mapToObj(i -> Tuple.of(filePartList.get(i), fileTypeList.get(i)))
                                        .forEach(tuple -> {
                                            String type = tuple.getString(1);
                                            ShopImage shopImage = new ShopImage();
                                            Image image = new Image();
                                            BdFile bdFile = BdUtil.uploadFile(tuple.get(FilePart.class, 0), type, BdConstants.DIRECTORY_NAME_IMAGES).block();

                                            image.setDisplayOrder(1L);
                                            image.setBdFile(bdFile);

                                            shopImage.setShop(shop);
                                            shopImage.setImage(image);
                                            shopImage.setType(ShopFileType.valueOf(type));
                                            imageList.add(shopImage);
                                        });
                                shop.setImageList(imageList);
                                return shopRepository.save(shop, session);
                            });
                })
                .thenApply(Mono::just)
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }
}
