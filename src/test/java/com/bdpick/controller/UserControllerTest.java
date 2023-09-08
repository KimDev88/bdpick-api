package com.bdpick.controller;

import com.bdpick.domain.entity.User;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * user controller test class
 */
@WebFluxTest(UserController.class)
@ContextConfiguration
public class UserControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void stub() {
        BDDMockito.given(userService.findById(ArgumentMatchers.anyString())).willReturn(Mono.just(new User()));
    }

    @ParameterizedTest
//    @ValueSource(strings = {"1", "su240", ""})
    @ValueSource(strings = {"1", "su240"})
    public void findById(String id) {
        webTestClient.get()
                .uri(PREFIX_API_URL + "/users/" + id)
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
