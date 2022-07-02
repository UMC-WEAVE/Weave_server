package com.weave.weaveserver.config.oauth.provider;

import lombok.Data;

@Data
public class OAuthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;
    private Long refresh_token_expires_in;
}