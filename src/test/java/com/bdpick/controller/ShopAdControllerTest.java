package com.bdpick.controller;

import com.bdpick.config.EntityConfiguration;
import com.bdpick.domain.entity.Keyword;
import com.bdpick.domain.entity.shop.Shop;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

    @BeforeEach
    public void stub() {
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
    }

    /**
     * create shop ad api test
     */
    @Test
    public void CreateShopAdUrlApiTest() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("oci_config"));
        builder.part("fileTypes", "A1");
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

    /**
     * find shop ads test
     */
    @Test
    public void findShopAds() {
        webClient.get().uri(PREFIX_API_URL + "/shop-ads")
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
