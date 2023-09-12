package com.bdpick.config;

import com.bdpick.common.ApplicationContextProvider;
import com.bdpick.common.BdUtil;
import com.bdpick.common.EntityFactory;
import com.bdpick.common.security.JwtService;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

/**
 * test configration class
 */
@org.springframework.boot.test.context.TestConfiguration
@Import({ApplicationContextProvider.class, EntityFactory.class, JwtService.class})
public class TestConfiguration {
    private static final String userIdForToken = "su2407";

    // 컨트롤러에서 공통으로 사용할 webTestClient 설정
    public static WebTestClient getWebTestClient() {
        WebTestClient webTestClient = BdUtil.getBean("webTestClient", WebTestClient.class);
        return webTestClient
                .mutateWith(SecurityMockServerConfigurers.csrf());

    }

    /**
     * get common client headers
     *
     * @return headers consumer
     */
    public static Consumer<HttpHeaders> getCommonClientHeaders() {
        return httpHeaders -> httpHeaders.add("authorization", "Bearer " + JwtService.createAccessToken(userIdForToken));
    }
}