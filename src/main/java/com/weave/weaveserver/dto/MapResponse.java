package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MapResponse {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class MapByDate{
        private LocalDate date;
        private List<Point> pointList = new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class Point {
        private LocalDate date;
        private String title;
        private double latitude;
        private double longitude;
    }
}
