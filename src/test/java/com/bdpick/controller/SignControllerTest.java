package com.bdpick.controller;

import com.bdpick.domain.UserType;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.service.SignService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * sign controller test
 */
@WebFluxTest(SignController.class)
public class SignControllerTest {
    @Autowired
    private WebTestClient client;
    private User user;
    @MockBean
    private SignService signService;

    @BeforeEach
    public void stub() {
        user = new User();
        user.setId("su240");
        user.setType(UserType.N);
        user.setEmail("yong2407@hanmail.net");
        user.setPassword("gs225201");

        BDDMockito.given(signService.up(user)).willReturn(Mono.just(user));
        BDDMockito.given(signService.isAvailableId(user.getId())).willReturn(Mono.just(true));

    }


    @Test
    @Transactional
    public void signUp() {
        client.post()
                .uri(PREFIX_API_URL + "/sign" + "/up")
                .body(Mono.just(user), User.class)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class)
                            .consumeWith(commonResponseEntityExchangeResult -> {
                                assert commonResponseEntityExchangeResult.getResponseBody().getData() != null;

                            });
                });
    }

    @Test
    @Transactional
    public void isAvailableId() {
        client.get()
                .uri(PREFIX_API_URL + "/sign" + "/check/" + user.getId())
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
