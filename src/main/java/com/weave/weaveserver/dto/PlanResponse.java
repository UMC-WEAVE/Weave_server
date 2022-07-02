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
    public static class planRes{
        private int planIdx;
        private int teamIdx;
        private LocalDateTime date;
        private String title;
        private LocalDateTime start_time;
        private LocalDateTime end_time;
        private String location;
        private int cost;
        private int userIdx;
    }

}
