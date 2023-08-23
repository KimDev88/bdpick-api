package com.bdpick.service;

import com.bdpick.domain.entity.Keyword;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.advertisement.AdKeyword;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.entity.shop.Shop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * shop service test class
 */
@SpringBootTest
public class ShopServiceTest {
    @Autowired
    private ShopService shopService;

    private final String registNumber = "3788600265";
    Shop shop;
    ShopAd shopAd = new ShopAd();
    List<FilePart> filePartList = new ArrayList<>();

    Flux<FilePart> partFlux;
    Flux<String> fileTypeFlux;
    FilePart filePart;

    @BeforeEach
    public void setData() {
        shop = new Shop();
        User user = new User();
        user.setId("su2407");

        shop.setName("테스트 매장");
        shop.setTel("01025562407");
        shop.setAddressName("호계1동");
        shop.setOwnerName("김용수");
        shop.setRegistNumber("3788600265");
        shop.setUser(user);
//        shopRepository.save(shop).subscribe();

        shopAd.setShop(shop);
        shopAd.setBranchName("지점명");
        shopAd.setStartedAt(LocalDateTime.now());
        shopAd.setEndedAt(LocalDateTime.now().plusDays(7L));
        shopAd.setContent("세일합니다.");
        AtomicInteger atomicInteger = new AtomicInteger(0);
        // 키워드 값 설정
        List<AdKeyword> keywordList = Stream.generate(() -> {
            AdKeyword adKeyword = new AdKeyword();
            Keyword keyword = new Keyword();
            keyword.setKeyword(atomicInteger.getAndIncrement() + "");
            adKeyword.setKeyword(keyword);
            adKeyword.setShopAd(shopAd);
            return adKeyword;
        }).limit(5).collect(Collectors.toList());
        shopAd.setKeywordList(keywordList);

        filePart = new FilePart() {
            @Override
            public String filename() {
                return "example.jpg";
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return Mono.empty();
            }

            @Override
            public String name() {
                return "example";
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.EMPTY;
            }

            @Override
            public Flux<DataBuffer> content() {
                return DataBufferUtils.read(
                        new ByteArrayResource("name".getBytes(StandardCharsets.UTF_8)), new DefaultDataBufferFactory(), 1024);
            }
        };

        partFlux = Flux.just(filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart);
        fileTypeFlux = Flux.just("S1", "S2", "S3", "S4", "S4", "S3", "S1", "S2", "S3", "S2");
        IntStream.range(0, 10)
                .forEach(value -> {
                    filePartList.add(filePart);
                });
    }

    /**
     * create shop test
     */
    @Test
    public void createShopTest() {
        Map<String, Object> headerMap = new HashMap<>();
        StepVerifier.create(shopService.createShop(headerMap, partFlux, fileTypeFlux, shop))
//        StepVerifier.create(shopService.createShop(null, partFlux, fileTypeFlux, shop))
                .expectNextMatches(createdShop -> (
                        createdShop.getId() != null &&
                                createdShop.getRegistNumber().equals(registNumber))
                )
                .verifyComplete();

    }

    /**
     * create shop ad test
     */
//    @Test
//    public void createShopAdTest() {
//        // 생성된 아이디는 +1 된 값이어야함
//        AtomicReference<Long> createdId = new AtomicReference<>(0L);
//        // 현재 등록된 마지막 shopAd 조회
//        shopAdService.findLastShopAd()
//                .doOnNext(shopAd1 -> {
//                    createdId.set(shopAd1.getId() + 1);
//                }).subscribe();
//
//        Map<String, Object> headerMap = new HashMap<>();
//        // 생성된 광고가 마지막 shopAd + 1의 아이디로 생성되었는지 검증
//        StepVerifier.create(shopAdService.createShopAd(headerMap, partFlux, fileTypeFlux, shopAd))
//                .expectNextMatches(shopAd1 -> shopAd1.getId().equals(createdId.get()))
//                .verifyComplete();
//    }
//
//    /**
//     * select shop ad list
//     */
//    @Test
//    public void selectShopAdList() {
//        StepVerifier.create(shopAdService.findShopAds(new Pageable(0, 100)))
//                .verifyComplete();
//
//    }
}
