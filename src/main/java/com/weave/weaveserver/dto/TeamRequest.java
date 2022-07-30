package com.weave.weaveserver.dto;

import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import lombok.*;

import java.time.LocalDate;


public class TeamRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    // 팀 생성
    public static class createReq {
        private Long leaderIdx;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imgUrl;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class addMemberReq {
        private Long leaderIdx;
        private String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getMemberReq {
        private Long leaderIdx;
        private Long teamIdx;
    }




}
