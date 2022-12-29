package com.weave.weaveserver.config.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class Token {
    private String loginId;
    private String token;

    public Token(String token) {
        this.token = token;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}