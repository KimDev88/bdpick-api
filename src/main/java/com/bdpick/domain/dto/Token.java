package com.bdpick.domain.dto;


import lombok.Data;

/**
 * user token object class
 */
@Data
public class Token {
    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    private final String accessToken;
    private final String refreshToken;
}
