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
        private int teamIdx;
        private LocalDateTime date;
        private String title;
        private LocalDateTime start_time;
        private LocalDateTime end_time;
        private String location;
        private int userIdx;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class planListRes{

        private int planIdx;
        private int teamIdx;
        private int userIdx;
        private String title;
        private LocalDateTime date;
        private LocalDateTime start_time;
        private LocalDateTime end_time;
        private String location;
        private float latitude;
        private float longitude;
        private int cost;

        public planListRes(int planIdx, int teamIdx, int userIdx, String title, LocalDateTime date) {
            this.planIdx = planIdx;
            this.teamIdx = teamIdx;
            this.userIdx = userIdx;
            this.title = title;
            this.date = date;
        }
    }

}
