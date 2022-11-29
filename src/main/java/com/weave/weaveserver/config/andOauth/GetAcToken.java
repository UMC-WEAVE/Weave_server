package com.weave.weaveserver.config.andOauth;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetAcToken {
    private String access_token;
    private String token_type;
    private String expires_in;
}
