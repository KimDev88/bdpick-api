package com.bdpick.service;

import com.bdpick.domain.entity.User;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * shop service test class
 */
@SpringBootTest
public class ShopServiceTest {
    @Autowired
    private ShopService shopService;

    private final String registerNumber = "3788600266";
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
        // 신규
//        shop.setRegisterNumber("6990901684");
        // 폐업
        shop.setRegisterNumber("1141679791");
        shop.setUser(user);

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
}
