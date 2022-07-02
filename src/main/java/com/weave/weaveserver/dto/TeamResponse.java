package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TeamResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class createRes {
        private int teamIdx;
        private String title;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getListRes {
        private String title;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private LocalDateTime date;
        private String location;
        private int recentUserIdx;

    }

}
