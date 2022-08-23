package com.weave.weaveserver.dto;

import lombok.*;

import java.time.LocalDate;


public class TeamRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    // 팀 생성
    public static class createReq {
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imgUrl;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class addMemberReq {
        private String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getMemberReq {
        private Long teamIdx;
    }


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class deleteMemberReq {
        private Long userIdx;
    }

    @NoArgsConstructor
    @Getter
    // 팀 정보 수정
    public static class updateTeamReq {
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imgUrl;

        @Builder
        public updateTeamReq(String title, LocalDate startDate, LocalDate endDate, String imgUrl){
            this.title = title;
            this.startDate = startDate;
            this.endDate = endDate;
            this.imgUrl = imgUrl;
        }
    }
}
