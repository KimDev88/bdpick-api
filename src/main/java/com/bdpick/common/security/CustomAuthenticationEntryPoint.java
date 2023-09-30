package com.bdpick.common.security;

import com.bdpick.common.request.CommonResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.bdpick.common.request.ResponseCode.CODE_TOKEN_EXPIRED;

/**
 * 토큰 만료 시 custom response handling class
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        CommonResponse resultResponse = new CommonResponse()
                .setError("token expired", null)
                .setCode(CODE_TOKEN_EXPIRED);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] resByte = objectMapper.writeValueAsBytes(resultResponse);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(resByte)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}