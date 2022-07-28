package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlanResponse {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class planDetailRes {
        private Long planIdx;
        private Long teamIdx;

        private LocalDate date;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String location;
        private int cost;

        private Long recentUserIdx;
        private boolean isModified;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class planListRes_and{
        //PLAN
        private Long planIdx;
        private LocalDate date;
        private String title;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String location;
        private int cost;

        private Long recentUserIdx;
        private boolean isModified;
    }

}
