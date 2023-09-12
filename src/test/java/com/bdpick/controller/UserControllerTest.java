package com.bdpick.controller;

import com.bdpick.config.TestConfiguration;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.service.UserService;
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
@WebFluxTest(UserController.class)
@Import(TestConfiguration.class)
@ContextConfiguration
public class UserControllerTest {
    @MockBean
    private UserService userService;

    private WebTestClient webClient;
    Consumer<HttpHeaders> headers;

    @BeforeEach
    public void stub() {
        webClient = TestConfiguration.getWebTestClient();
        headers = TestConfiguration.getCommonClientHeaders();

        BDDMockito.given(userService.findById(ArgumentMatchers.anyString())).willReturn(Mono.just(new User()));
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
