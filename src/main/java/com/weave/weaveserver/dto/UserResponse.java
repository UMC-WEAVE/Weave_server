package com.weave.weaveserver.dto;


import lombok.*;

public class UserResponse {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class userResponse{
        private String name;
        private String email;
    }

    @Builder
    @Data
    public static class myPage{
        private String name;
        private String email;
        private String image;
        private String loginType;
        private int countTeam;
    }

}
