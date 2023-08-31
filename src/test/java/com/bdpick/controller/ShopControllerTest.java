package com.bdpick.controller;

import com.bdpick.config.EntityConfiguration;
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

import java.util.Objects;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * shop ad controller test class
 */
@WebFluxTest(ShopController.class)
@Import(EntityConfiguration.class)
public class ShopControllerTest {
    @Autowired
    private WebTestClient webClient;

    @SpyBean
    private ShopService shopService;
    @SpyBean
    private ShopRepository shopRepository;

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
        shop.setRegisterNumber("3788600265");
        shop.setUser(user);
    }

    /**
     * create shop ad api test
     */
    @Test
    public void CreateShopApiTest() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("oci_config"));
        builder.part("fileTypes", "S1");
        builder.part("shop", shop);

        webClient.post().uri(PREFIX_API_URL + "/shops")
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
