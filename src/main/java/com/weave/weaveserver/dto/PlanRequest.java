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
        private int userIdx;
        private int teamIdx;

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
