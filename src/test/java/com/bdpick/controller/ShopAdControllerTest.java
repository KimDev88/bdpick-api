package com.bdpick.controller;

import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.service.ShopAdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * shop ad controller test class
 */
@WebFluxTest(ShopAdController.class)
public class ShopAdControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ShopAdService shopAdService;

    ShopAd shopAd = new ShopAd();
    Flux<String> fileTypeFlux = Flux.just("A1", "A2", "A1", "A2", "A1", "A2", "A1", "A2", "A1", "A2");
    ;


    @BeforeEach
    public void stub() {
        given(this.shopAdService.createShopAd(any(Map.class), any(Flux.class), any(Flux.class), any(ShopAd.class)))
                .willReturn(Mono.just(shopAd));
    }

    @Test
    public void CreateShopAdUrlApiTest() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        ByteArrayResource byteArrayResource = new ByteArrayResource("test" .getBytes(), "example.jpg");
        Flux<ByteArrayResource> testFlux = Flux.just(byteArrayResource, byteArrayResource, byteArrayResource, byteArrayResource);
        builder.asyncPart("files", testFlux, ByteArrayResource.class);
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
                        assert commonResponseEntityExchangeResult.getResponseBody().getData() != null;
                    });
                })
                .expectStatus().isOk();

    }
}
