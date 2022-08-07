package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class join{
        private String email;
        private String name;
        private String loginId;
        private String image;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class kakao_access_token{
        private String accessToken;
    }
}
