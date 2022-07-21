package com.weave.weaveserver.dto;

import com.weave.weaveserver.domain.Team;
import com.weave.weaveserver.domain.User;
import lombok.*;

import java.time.LocalDate;

public class TeamRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class createReq {
        private Long userIdx;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imgUrl;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getReq {
        private Long userIdx;
        private Long teamIdx;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class addMemberReq {
        private Long teamIdx;
        private String email;
    }

}
