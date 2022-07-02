package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PlanRequest {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class createReq{
        private int teamIdx;
        private LocalDateTime date;
        private String title;
        private LocalDateTime start_time;
        private LocalDateTime end_time;
        private String location;
        private int userIdx;
        private int cost;
    }
}
