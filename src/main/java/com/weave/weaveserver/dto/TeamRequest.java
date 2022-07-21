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
        private int userIdx;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imgUrl;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getReq {
        private int userIdx;
        private int teamIdx;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class addMemberReq {
        private int teamIdx;
        private String email;
    }

}
