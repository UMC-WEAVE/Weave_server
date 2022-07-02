package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class join{
        private String email;
        private String name;
        private String loginId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class login{
        private String email;
        private String name;
        private String loginId;
    }

}
