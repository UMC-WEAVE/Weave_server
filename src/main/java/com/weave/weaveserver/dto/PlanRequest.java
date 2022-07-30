package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PlanRequest {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class createReq{
        private Long userIdx;
        private Long teamIdx;

        private String title;
        private LocalDate date;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String location;
        private double latitude;
        private double longitude;
        private int cost;

        private Long archiveIdx;
        private int isArchive;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class updateReq{
        private Long userIdx;

        private String title;
        private LocalDate date;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String location;
        private double latitude;
        private double longitude;
        private int cost;
    }
}
