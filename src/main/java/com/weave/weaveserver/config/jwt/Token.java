package com.weave.weaveserver.config.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class Token {
    private String token;

    public Token(String token) {
        this.token = token;
    }
}