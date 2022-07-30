package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TeamResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class createRes {
        private Long teamIdx;
        private String title;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getListRes {
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        //private LocalDateTime date;
        //private int recentUserIdx;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getMemberList {
        private Long userIdx;
        private String name;
        private String url;

    }

}
