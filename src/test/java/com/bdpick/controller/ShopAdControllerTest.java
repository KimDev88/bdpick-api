package com.bdpick.controller;

import com.bdpick.config.EntityConfiguration;
import com.bdpick.domain.entity.Keyword;
import com.bdpick.domain.entity.Shop;
import com.bdpick.domain.entity.advertisement.AdKeyword;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.AdImageRepository;
import com.bdpick.repository.ShopAdRepository;
import com.bdpick.service.ShopAdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * shop ad controller test class
 */
@WebFluxTest(ShopAdController.class)
@Import(EntityConfiguration.class)
public class ShopAdControllerTest {
    @Autowired
    private WebTestClient webClient;

    @SpyBean
    private ShopAdService shopAdService;
    @SpyBean
    private ShopAdRepository shopAdRepository;
    @SpyBean
    private AdImageRepository adImageRepository;

    Shop shop;
    ShopAd shopAd;
    List<FilePart> filePartList = new ArrayList<>();

    Flux<FilePart> partFlux;
    FilePart filePart;
    Flux<String> fileTypeFlux = Flux.just("A1", "A2", "A1", "A2", "A1", "A2", "A1", "A2", "A1", "A2");
    ;


    @BeforeEach
    public void stub() {
//        given(this.shopAdService.createShopAd(any(Map.class), any(Flux.class), any(Flux.class), any(ShopAd.class)))
//                .willReturn(Mono.just(shopAd));

        shop = new Shop();
        shop.setId(1L);

        shopAd = new ShopAd();
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
                        new ByteArrayResource("name" .getBytes(StandardCharsets.UTF_8)), new DefaultDataBufferFactory(), 1024);
            }
        };

        partFlux = Flux.just(filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart, filePart);
        fileTypeFlux = Flux.just("A1", "A2", "A1", "A2", "A1", "A2", "A1", "A2", "A1", "A2");
        IntStream.range(0, 10)
                .forEach(value -> {
                    filePartList.add(filePart);
                });
    }


    @Test
    public void CreateShopAdUrlApiTest() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        ByteArrayResource byteArrayResource = new ByteArrayResource("test" .getBytes(), "example.jpg");
        String header = String.format("form-data; name=%s; filename=%s", "file", "test.jpg");
        Flux<ByteArrayResource> testFlux = Flux.just(byteArrayResource, byteArrayResource, byteArrayResource, byteArrayResource);
        builder.part("files", filePart).header("Content-Disposition", header);
//        builder.asyncPart("files", partFlux, FilePart.class).header("Content-Disposition", header);
        builder.asyncPart("fileTypes", fileTypeFlux, String.class);
        builder.part("shop", shopAd);

        webClient.post().uri(PREFIX_API_URL + "/shop-ads")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class).consumeWith(commonResponseEntityExchangeResult -> {
                        assert Objects.requireNonNull(commonResponseEntityExchangeResult.getResponseBody()).getData() != null;
                    });
                })
                .expectStatus().isOk();

    }
}
