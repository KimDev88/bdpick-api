package com.bdpick.controller;

import com.bdpick.common.MailService;
import com.bdpick.common.security.JwtService;
import com.bdpick.config.TestConfiguration;
import com.bdpick.domain.UserType;
import com.bdpick.domain.dto.Token;
import com.bdpick.domain.dto.UserDto;
import com.bdpick.domain.entity.Device;
import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.Verify;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.DeviceRepository;
import com.bdpick.repository.SignRepository;
import com.bdpick.repository.UserRepository;
import com.bdpick.repository.VerifyRepository;
import com.bdpick.service.DeviceService;
import com.bdpick.service.SignService;
import org.hibernate.reactive.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.bdpick.common.BdConstants.PREFIX_API_URL;

/**
 * sign controller test
 */
@WebFluxTest(SignController.class)
@Import(TestConfiguration.class)
public class SignControllerTest {
    private UserDto user;
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
    @SpyBean
    private JwtService jwtService;
    @SpyBean
    private DeviceService deviceService;
    @SpyBean
    private DeviceRepository deviceRepository;

    private final String URI = PREFIX_API_URL + "/sign";
    private Device device;

    private WebTestClient webClient;


    @BeforeEach
    public void stub() {
        webClient = TestConfiguration.getWebTestClient();

        String email = "yong2407@hanmail.net";
        user = new UserDto();
        user.setId("su2407");
        user.setType(UserType.N);
        user.setEmail(email);
        user.setPassword("gs225201");
        user.setUuid("TEST");

        device = new Device();
        device.setUser(user);
        device.setUuid("TEST");

    }

    /**
     * sign up user
     */
    @Test
    @WithMockUser
    public void signUp() {
        webClient.post()
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
     * sign in test
     */
    @Test
    @WithMockUser
    public void signIn() {
        webClient.post()
                .uri(URI + "/in")
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
     * sign in test
     */
    @Test
    @WithMockUser
    public void renewToken() {
        // 토큰 검증을 위해 실제 로그인 시 사용한 refresh token 조회
        Device rtnDevice = deviceService.findDeviceByUserAndUuid(device).block();
        Token token = new Token(JwtService.createAccessToken(user.getId()), Objects.requireNonNull(rtnDevice).getRefreshToken());
        webClient.post()
                .uri(URI + "/renew")
                .body(Mono.just(token), Token.class)
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
    @WithMockUser
    public void isAvailableId() {
        webClient.get()
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
    @WithMockUser
    public void sendMail() {
        webClient.post()
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
    @WithMockUser
    public void verifyEmail() {
        // 마지막 등록된 코드 조회
        sessionFactory.withSession(session ->
                session.createQuery("select v from Verify v where email = :email order by createdAt desc ", Verify.class)
                        .setParameter("email", user.getEmail())
                        .setMaxResults(1)
                        .getSingleResultOrNull()).thenAccept(rtnVerify -> {
            Assertions.assertNotNull(rtnVerify);
            verify = rtnVerify;
        }).toCompletableFuture().join();

        webClient.post()
                .uri(URI + "/verify-mail")
                .body(Mono.just(verify), Verify.class)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isOk();
                    responseSpec.expectBody(CommonResponse.class)
                            .consumeWith(commonResponseEntityExchangeResult -> {
                                Object data = Objects.requireNonNull(commonResponseEntityExchangeResult.getResponseBody())
                                        .getData();
                                // 데이터가 true 여야만 함
                                assert data instanceof Boolean && (Boolean) data;
                            });
                });
    }
}
