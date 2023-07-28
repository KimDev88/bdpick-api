package com.bdpick.fluxstudy;

import com.bdpick.domain.entity.Keyword;
import com.bdpick.domain.entity.Shop;
import com.bdpick.domain.entity.advertisement.AdKeyword;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.KeywordRepository;
import com.bdpick.repository.ShopAdRepository;
import com.bdpick.repository.ShopRepository;
import com.bdpick.service.ShopAdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
//@WebFluxTest(ShopAdController.class)
@SpringBootTest
public class ShopAdTests {
    @Autowired
    private ShopAdService shopAdService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ShopAdRepository shopAdRepository;
    @Autowired
    private KeywordRepository keywordRepository;

    Shop shop = new Shop();
    ShopAd shopAd = new ShopAd();

    Flux<FilePart> partFlux;
    Flux<String> fileTypeFlux;

    @BeforeEach
    public void setData() {
        shop.setId(1L);


//        shop.setName("테스트 매장");
//        shop.setTel("01025562407");
//        shop.setAddressName("호계1동");
//        shop.setOwnerName("김용수");
//        shop.setRegistNumber("3788600265");
//        shop.setUserId("su240");
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
//            keyword = keywordRepository.findOrSave(keyword).block();
//            keyword.setKeyword(LocalDateTime.now().getNano() + "");
            adKeyword.setKeyword(keyword);
            adKeyword.setShopAd(shopAd);
            return adKeyword;
        }).limit(5).collect(Collectors.toList());
        shopAd.setKeywordList(keywordList);

        FilePart filePart = new FilePart() {
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
                        new ByteArrayResource("name" .getBytes(StandardCharsets.UTF_8)), new DefaultDataBufferFactory(), 1024);
            }
        };

        partFlux = Flux.just(filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart);
        fileTypeFlux = Flux.just("A1", "A2", "A1", "A2", "A1", "A2", "A1", "A2", "A1", "A2");
    }

    @Test
    public void createShopAdTest() {
        // 결과값 검증
        CommonResponse commonResponse = new CommonResponse();
        // 생성된 아이디는 +1 된 값이어야함
        AtomicReference<Long> createdId = new AtomicReference<>(0L);
        // 현재 등록된 마지막 shopAd 조회
        shopAdRepository.findLastShopAd()
                .doOnNext(shopAd1 -> {
                    createdId.set(shopAd1.getId() + 1);
                }).subscribe();

        Map<String, Object> headerMap = new HashMap<>();


        StepVerifier.create(shopAdService.createShopAd(headerMap, partFlux, fileTypeFlux, shopAd))
                .expectNext(commonResponse.setData(createdId))
                .verifyComplete();
    }
}
