package com.weave.weaveserver.config.andOauth;

import lombok.Data;

@Data
public class NaverProfile {
    private String resultcode;
    private String message;
    private Response response;

    @Data
    public static class Response {
        private String id;
        private String nickname;
        private String profile_image;
        private String email;
        private String name;
    }
}
