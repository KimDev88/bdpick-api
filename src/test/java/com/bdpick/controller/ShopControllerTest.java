package com.bdpick.controller;

import com.bdpick.common.security.JwtService;
import com.bdpick.config.TestConfiguration;
import com.bdpick.domain.entity.User;
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
 * shop ad controller test class
 */
@WebFluxTest(ShopController.class)
@Import(TestConfiguration.class)
public class ShopControllerTest {


    @SpyBean
    private ShopService shopService;
    @SpyBean
    private ShopRepository shopRepository;
    @SpyBean
    private JwtService jwtService;

    @Autowired
    Map<String, Object> headerMap;

    private WebTestClient webClient;
    Consumer<HttpHeaders> headers;

    Shop shop;

    @BeforeEach
    public void stub() {
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

    /**
     * select my shop info
     */
    @Test
    @WithMockUser
    public void selectMyShopApiTest() {
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
     * create shop ad api test
     */
    @Test
    @WithMockUser
    public void createShopApiTest() {
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

    /**
     * check register number is available
     */
    @Test
    @WithMockUser
    public void checkRegisterApiTest() {
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

}
