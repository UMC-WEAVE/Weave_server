package com.weave.weaveserver.config.andOauth;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetGoogleAcToken {
    private String access_token;
    private String expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
