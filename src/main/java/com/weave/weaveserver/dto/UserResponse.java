package com.weave.weaveserver.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserResponse {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class userRes{
        private Long userIdx;
        private String name;
    }

}
