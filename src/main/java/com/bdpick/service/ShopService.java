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

//    public Mono<Boolean> checkRegisterNumber(@NonNull Shop shop) {
//
//        String registerNumber = shop.getRegisterNumber();
//
//        // request parameter 설정
//        Map<String, Object> bodyMap = new HashMap<>();
//        List<String> bnoList = new ArrayList<>();
//
//        bnoList.add(registerNumber);
//        bodyMap.put("b_no", bnoList);
//
//        // 공곰 API 호출 설정
//        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("https://api.odcloud.kr/api/nts-businessman/v1/");
//        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
//        WebClient client = WebClient
//                .builder()
//                .uriBuilderFactory(factory)
//                .build();
//
//        return shopRepository.findShopByRegistNumber(registerNumber)
//                .hasElement()
//                .flatMap(aBoolean -> {
//                    // 해당 사업자번호가 존재할 경우
//                    if (aBoolean) {
//                        response.setError("이미 존재하는 사업자번호 입니다.", false);
//                    } else {
//                        return client
//                                .post()
//                                .uri("status?serviceKey=" + openApiToken)
//                                .body(Mono.just(bodyMap), Map.class)
//                                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Map.class))
//                                .map(o -> {
//                                    Optional<Map<String, Object>> result = Optional.ofNullable(o);
//                                    Optional<List<Map<String, Object>>> dataList = result.map(stringObjectMap -> (List<Map<String, Object>>) stringObjectMap.get("data"));
//                                    Optional<Map<String, Object>> resultMap = dataList.stream().findAny().orElseGet(ArrayList::new).stream().findFirst();
//                                    Optional<String> bSttCd = resultMap.map(stringObjectMap -> (String) stringObjectMap.get("b_stt_cd"));
//                                    if (bSttCd.isEmpty() || bSttCd.get().equals("")) {
//                                        response.setError("사업자 번호가 유효하지 않습니다.", false);
//                                    } else {
//                                        switch (bSttCd.get()) {
//                                            // 계속사업자
//                                            case "01":
//                                                response.setData(true);
//                                                break;
//                                            // 휴업사업자
//                                            case "02":
//                                                response.setError("휴업 사업자 입니다.", false);
//                                                break;
//                                            // 폐업자
//                                            case "03":
//                                                response.setError("폐업 사업자 입니다.", false);
//                                                break;
//                                        }
//                                    }
//                                    return response;
//                                });
//                    }
//                    return Mono.just(response);
//    }

}
