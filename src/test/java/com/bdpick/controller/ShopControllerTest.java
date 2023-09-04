package com.bdpick.controller;

import com.bdpick.common.security.JwtService;
import com.bdpick.config.CommonTestConfiguration;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.entity.shop.Shop;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.ShopRepository;
import com.bdpick.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * shop ad controller test class
 */
@WebFluxTest(ShopController.class)
@Import(CommonTestConfiguration.class)
public class ShopControllerTest {
    @Autowired
    private WebTestClient webClient;

    @SpyBean
    private ShopService shopService;
    @SpyBean
    private ShopRepository shopRepository;
    @SpyBean
    private JwtService jwtService;

    @Autowired
    Map<String, Object> headerMap;

    Shop shop;
    ShopAd shopAd;

    @BeforeEach
    public void stub() {
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
    }

    /**
     * select my shop info
     */
    @Test
    public void selectMyShopApiTest() {
        webClient.get().uri(PREFIX_API_URL + "/shops/this")
                .headers(httpHeaders -> {
                    httpHeaders.add("authorization", "Bearer " + jwtService.createAccessToken("su2407"));
                })
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class).consumeWith(commonResponseEntityExchangeResult -> {
                        assert Objects.requireNonNull(commonResponseEntityExchangeResult.getResponseBody()).getData() != null;
                    });
                })
                .expectStatus().isOk();

    }

    /**
     * create shop ad api test
     */
    @Test
    public void createShopApiTest() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("oci_config"));
        builder.part("fileTypes", "S1");
        builder.part("shop", shop);

        webClient.post().uri(PREFIX_API_URL + "/shops")
                .headers(httpHeaders -> {
                    httpHeaders.add("authorization", "Bearer " + jwtService.createAccessToken("su2407"));
                })
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

    /**
     * check register number is available
     */
    @Test
    public void checkRegisterApiTest() {
        webClient.post().uri(PREFIX_API_URL + "/shops/check-register")
                .body(Mono.just(shop), Shop.class)
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
