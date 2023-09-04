package com.bdpick.controller;

import com.bdpick.common.MailService;
import com.bdpick.config.CommonTestConfiguration;
import com.bdpick.domain.UserType;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.Verify;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.SignRepository;
import com.bdpick.repository.UserRepository;
import com.bdpick.repository.VerifyRepository;
import com.bdpick.service.SignService;
import jakarta.transaction.Transactional;
import org.hibernate.reactive.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * sign controller test
 */
@WebFluxTest(SignController.class)
@Import(CommonTestConfiguration.class)
public class SignControllerTest {
    @Autowired
    private WebTestClient client;
    private User user;
    private Verify verify;

    @SpyBean
    private SignService signService;
    @SpyBean
    private Stage.SessionFactory sessionFactory;
    @SpyBean
    private SignRepository signRepository;
    @SpyBean
    private UserRepository userRepository;
    @SpyBean
    private VerifyRepository verifyRepository;
    @SpyBean
    private MailService mailService;

    private final String URI = PREFIX_API_URL + "/sign";

    @BeforeEach
    public void stub() {
        String email = "yong2407@hanmail.net";
        user = new User();
        user.setId("su2407");
        user.setType(UserType.N);
        user.setEmail(email);
        user.setPassword("gs225201");

        verify = new Verify();
        verify.setEmail(email);
        verify.setCode("DM778W");

    }

    /**
     * sign up user
     */
    @Test
    @Transactional
    public void signUp() {
        client.post()
                .uri(URI + "/up")
                .body(Mono.just(user), User.class)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class)
                            .consumeWith(commonResponseEntityExchangeResult -> {
                                assert Objects.requireNonNull(commonResponseEntityExchangeResult.getResponseBody())
                                        .getData() != null;
                            });
                });
    }

    /**
     * check is available id
     */
    @Test
    @Transactional
    public void isAvailableId() {
        client.get()
                .uri(URI + "/check/" + user.getId())
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class)
                            .consumeWith(commonResponseEntityExchangeResult -> {
                                assert Objects.requireNonNull(commonResponseEntityExchangeResult.getResponseBody())
                                        .getData() != null;
                            });
                });
    }

    /**
     * send email test
     */
    @Test
    @Transactional
    public void sendMail() {
        client.post()
                .uri(URI + "/send-mail")
                .body(Mono.just(user), User.class)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class)
                            .consumeWith(commonResponseEntityExchangeResult -> {
                                assert Objects.requireNonNull(commonResponseEntityExchangeResult.getResponseBody())
                                        .getData() != null;
                            });
                });
    }

    /**
     * verify email and code
     */
    @Test
    public void verifyEmail() {
        client.post()
                .uri(URI + "/verify-mail")
                .body(Mono.just(verify), Verify.class)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class)
                            .consumeWith(commonResponseEntityExchangeResult -> {
                                Object data = Objects.requireNonNull(commonResponseEntityExchangeResult.getResponseBody())
                                        .getData();
                                assert data instanceof Boolean && (Boolean) data;
                            });
                });
    }


}
