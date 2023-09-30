package com.bdpick.shop.web.rest;

import com.bdpick.common.security.JwtService;
import com.bdpick.config.TestConfiguration;
import com.bdpick.common.request.CommonResponse;
import com.bdpick.shop.adaptor.ShopProducerImpl;
import com.bdpick.shop.domain.Shop;
import com.bdpick.shop.repository.impl.ShopRepositoryImpl;
import com.bdpick.shop.service.ShopServiceImpl;
import com.bdpick.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * Shop Resource Test Class
 */
@WebFluxTest(ShopResource.class)
@Import(TestConfiguration.class)
class ShopResourceTest {
    @SpyBean
    private ShopServiceImpl shopService;
    @SpyBean
    private ShopRepositoryImpl shopRepositoryImpl;
    @SpyBean
    private JwtService jwtService;
    @SpyBean
    private ShopProducerImpl shopProducer;
//    @SpyBean
//    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    Map<String, Object> headerMap;

    private WebTestClient webClient;
    private Consumer<HttpHeaders> headers;
    private Shop shop;


    @BeforeEach
    void setUp() {
        webClient = TestConfiguration.getWebTestClient();
        headers = TestConfiguration.getCommonClientHeaders();

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

    @AfterEach
    void tearDown() {
    }

    /**
     * select my shop info
     */
    @Test
    @WithMockUser
    void selectMyShop() {
        webClient.get().uri(PREFIX_API_URL + "/shops/this")
                .headers(headers)
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
    @WithMockUser
    void checkRegisterNumber() {
        webClient.post().uri(PREFIX_API_URL + "/shops/check-register")
                .headers(headers)
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

    /**
     * create shop ad api test
     */
    @Test
    @WithMockUser
    void createShop() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("/META-INF/persistence.xml"));
        builder.part("fileTypes", "S1");
        builder.part("shop", shop);

        // 공통헤더 설정
        webClient
                .post().uri(PREFIX_API_URL + "/shops")
                .headers(headers)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .headers(TestConfiguration.getCommonClientHeaders())
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