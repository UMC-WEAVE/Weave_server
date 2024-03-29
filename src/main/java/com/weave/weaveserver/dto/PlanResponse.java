package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PlanResponse {
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class planDetailRes {
        private Long planIdx;
        private Long teamIdx;

        private LocalDate date;
        private String day;
        private String title;
        private String startTime;
        private String endTime;
        private String location;
        private double latitude;
        private double longitude;
        private int cost;

        private String recentUserName;
        private boolean isModified;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class planListRes {
        //Team Dto
        private TeamResponse.teamResponse teamDetailDto = new TeamResponse.teamResponse();

        //Team Member List Dto 필요함
        private List<TeamResponse.getMemberList> teamMemberListDto = new ArrayList<>();

        //PLAN
        private List<List> planDto = new ArrayList<>();

    }

}