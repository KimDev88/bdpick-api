package com.bdpick.service;

import com.bdpick.common.security.JwtService;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.entity.shop.Shop;
import org.junit.jupiter.api.Assertions;
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
import java.util.*;
import java.util.stream.IntStream;

/**
 * shop service test class
 */
@SpringBootTest
public class ShopServiceTest {
    @Autowired
    private ShopService shopService;

    @Autowired
    private JwtService jwtService;

    private  String registerNumber;
    private Shop shop;
    private ShopAd shopAd = new ShopAd();
    private List<FilePart> filePartList = new ArrayList<>();

    private Flux<FilePart> partFlux;
    private Flux<String> fileTypeFlux;
    private FilePart filePart;

    private Map<String, Object> headerMap = new HashMap<>();

    private String userId;



    @BeforeEach
    public void setData() {
        userId = "su2407";
        shop = new Shop();
        User user = new User();
        user.setId(userId);

        shop.setName("테스트 매장");
        shop.setTel("01025562407");
        shop.setAddressName("호계1동");
        shop.setOwnerName("김용수");
        // 신규
        registerNumber = "6990901684";
        // 폐업
        registerNumber = "1141679791";
        shop.setRegisterNumber(registerNumber);
        shop.setUser(user);

        // headermap
        headerMap.put("authorization", "Bearer " + jwtService.createAccessToken(userId));

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
     * select my shop info
     */
    @Test
    public void selectMyShop() {
        StepVerifier.create(shopService.selectMyShop(headerMap))
                .expectNextMatches(Objects::nonNull)
                .verifyComplete();
    }

    /**
     * create shop test
     */
    @Test
    public void createShopTest() {
//        Map<String, Object> headerMap = new HashMap<>();
        StepVerifier.create(shopService.createShop(headerMap, partFlux, fileTypeFlux, shop))
                .expectNextMatches(createdShop -> (
                        createdShop.getId() != null &&
                                createdShop.getRegisterNumber().equals(registerNumber))
                )
                .verifyComplete();

    }

    /**
     * check register number is available
     */
    @Test
    public void checkRegisterNumberTest() {
        StepVerifier.create(shopService.checkRegisterNumber(shop))
                .expectNextMatches(response -> (
                        response.getData() != null)
                )
                .verifyComplete();
    }

    @Test
    public void getShopIdByUserId() {
        Assertions.assertNotNull(shopService.getShopIdByUserId(userId));
    }

}
