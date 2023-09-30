package com.bdpick.user.web.rest;

import com.bdpick.config.TestConfiguration;
import com.bdpick.user.domain.User;
import com.bdpick.common.request.CommonResponse;
import com.bdpick.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * user controller test class
 */
@WebFluxTest(UserResource.class)
@Import(TestConfiguration.class)
@ContextConfiguration
public class UserResourceTest {
    @MockBean
    private UserServiceImpl userServiceImpl;

    private WebTestClient webClient;
    Consumer<HttpHeaders> headers;

    @BeforeEach
    public void stub() {
        webClient = TestConfiguration.getWebTestClient();
        headers = TestConfiguration.getCommonClientHeaders();

        BDDMockito.given(userServiceImpl.findById(ArgumentMatchers.anyString())).willReturn(Mono.just(new User()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "su240"})
    @WithMockUser
    public void findById(String id) {
        webClient.get()
                .uri(PREFIX_API_URL + "/users/" + id)
                .headers(headers)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class)
                            .consumeWith(commonResponseEntityExchangeResult -> {
                                assert commonResponseEntityExchangeResult.getResponseBody().getData() != null;
                            });
                });

    }
}
