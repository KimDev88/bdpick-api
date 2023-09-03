package com.bdpick.service;

import com.bdpick.common.BdConstants;
import com.bdpick.common.BdUtil;
import com.bdpick.domain.ShopFileType;
import com.bdpick.domain.entity.BdFile;
import com.bdpick.domain.entity.common.Image;
import com.bdpick.domain.entity.shop.Shop;
import com.bdpick.domain.entity.shop.ShopImage;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.ShopRepository;
import io.vertx.sqlclient.Tuple;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.IntStream;

import static com.bdpick.common.BdConstants.Exception.KEY_DUPLICATE_REGISTER;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final Stage.SessionFactory factory;
    private final ShopRepository shopRepository;
    private static final String MSG_EXIST_REGISTER_NUMBER = "이미 존재하는 사업자번호 입니다.";

    @Value("${openapi.token}")
    private String openApiToken;

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
                    return shopRepository.findShopByRegisterNumber(shop, session)
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

    /**
     * check register number is available
     *
     * @param shop shop
     * @return true : available, false : unavailable
     */
    public Mono<CommonResponse> checkRegisterNumber(@NonNull Shop shop) {
        CommonResponse response = new CommonResponse();

        String registerNumber = shop.getRegisterNumber();
        // request parameter 설정
        Map<String, Object> urlBodyMap = new HashMap<>();
        List<String> bnoList = new ArrayList<>();

        bnoList.add(registerNumber);
        urlBodyMap.put("b_no", bnoList);

        // 사업자번호 유효성 검증을 위한 공공 API 호출 설정
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("https://api.odcloud.kr/api/nts-businessman/v1/");
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        WebClient client = WebClient
                .builder()
                .uriBuilderFactory(uriBuilderFactory)
                .build();

        return factory.withSession(session ->
                        shopRepository.findShopByRegisterNumber(shop, session)
                )
                .thenApply(rtnShop -> {
                    // 해당 사업자번호 매장이 존재할 경우
                    if (rtnShop != null) {
                        response.setError(MSG_EXIST_REGISTER_NUMBER, false);
                        return Mono.just(response);
//                        return response;
                    } else {
                        return client
                                .post()
                                .uri("status?serviceKey=" + openApiToken)
                                .body(Mono.just(urlBodyMap), Map.class)
                                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Map.class))
                                .map(o -> {
                                    Optional<Map<String, Object>> result = Optional.ofNullable(o);
                                    Optional<List<Map<String, Object>>> dataList = result.map(stringObjectMap -> (List<Map<String, Object>>) stringObjectMap.get("data"));
                                    Optional<Map<String, Object>> resultMap = dataList.stream().findAny().orElseGet(ArrayList::new).stream().findFirst();
                                    Optional<String> bSttCd = resultMap.map(stringObjectMap -> (String) stringObjectMap.get("b_stt_cd"));
                                    if (bSttCd.isEmpty() || bSttCd.get().isEmpty()) {
                                        response.setError("사업자 번호가 유효하지 않습니다.", false);
                                    } else {
                                        switch (bSttCd.get()) {
                                            // 계속사업자
                                            case "01" -> response.setData(true);
                                            // 휴업사업자
                                            case "02" -> response.setError("휴업 사업자 입니다.", false);
                                            // 폐업자
                                            case "03" -> response.setError("폐업 사업자 입니다.", false);
                                        }
                                    }
                                    return Mono.just(response);
//                                    return  response;
                                }).block();
                    }
                })
                .exceptionally(Mono::error)
                .toCompletableFuture().join();
    }
}
