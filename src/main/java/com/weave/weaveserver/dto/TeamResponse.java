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
    public static class getMemberList {
        private Long userIdx;
        private String userName;
        private String userEmail;
        private String userImg;
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
    public static class getMyTeams {
        private Long teamIdx;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String imgUrl;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class showMyTeamList{
        private String userName;
        private List<getMyTeams> myTeams;
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
