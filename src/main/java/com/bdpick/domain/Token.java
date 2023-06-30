package com.bdpick.domain;


import lombok.Data;

@Data
public class Token {
    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    private final String accessToken;
    private final String refreshToken;
}
