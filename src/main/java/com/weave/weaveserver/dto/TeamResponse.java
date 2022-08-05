package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    public static class getTeamListRes {
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


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class teamResponse {
        private Long teamIdx;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imgUrl;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class teamWithDateListResponse {
        private Long teamIdx;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<LocalDate> dateList;
        private String imgUrl;
    }

}
