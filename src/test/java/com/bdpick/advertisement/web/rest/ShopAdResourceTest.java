package com.bdpick.advertisement.web.rest;

import com.bdpick.config.TestConfiguration;
import com.bdpick.advertisement.domain.Keyword;
import com.bdpick.advertisement.domain.AdKeyword;
import com.bdpick.advertisement.domain.ShopAd;
import com.bdpick.shop.adaptor.ShopProducerImpl;
import com.bdpick.shop.domain.Shop;
import com.bdpick.common.request.CommonResponse;
import com.bdpick.advertisement.repository.impl.AdImageRepositoryImpl;
import com.bdpick.advertisement.repository.impl.ShopAdRepositoryImpl;
import com.bdpick.advertisement.service.ShopAdServiceImpl;
import com.bdpick.shop.repository.impl.ShopRepositoryImpl;
import com.bdpick.shop.service.ShopServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * shop ad controller test class
 */
@WebFluxTest(ShopAdResource.class)
@Import(TestConfiguration.class)
public class ShopAdResourceTest {

    @SpyBean
    private ShopAdServiceImpl shopAdServiceImpl;
    @SpyBean
    private ShopServiceImpl shopServiceImpl;
    @SpyBean
    private ShopAdRepositoryImpl shopAdRepositoryImpl;
    @SpyBean
    private AdImageRepositoryImpl adImageRepositoryImpl;
    @SpyBean
    private ShopRepositoryImpl shopRepository;
    @SpyBean
    private ShopProducerImpl shopProducer;

    private Shop shop;
    private ShopAd shopAd;

    private WebTestClient webClient;
    Consumer<HttpHeaders> headers;

    @BeforeEach
    public void stub() {
        webClient = TestConfiguration.getWebTestClient();
        headers = TestConfiguration.getCommonClientHeaders();

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
    @WithMockUser
    public void createShopAdUrlApiTest() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("/META-INF/persistence.xml"));
        builder.part("fileTypes", "A1");
        builder.part("shopAd", shopAd);

        webClient.post().uri(PREFIX_API_URL + "/shop-ads")
                .headers(headers)
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
    @WithMockUser
    public void findShopAds() {
        webClient.get().uri(PREFIX_API_URL + "/shop-ads")
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
}
