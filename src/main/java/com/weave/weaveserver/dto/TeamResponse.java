package com.weave.weaveserver.dto;

import com.weave.weaveserver.domain.Image;
import com.weave.weaveserver.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TeamResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class createRes {
        private Long teamIdx;
        private String title;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getListRes {
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        //private LocalDateTime date;
        //private int recentUserIdx;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class getMemberList {
        private Long userIdx;
        private String name;
        private String url;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class teamResponse {
        private Long teamIdx;
//        private User user; //팀의 리더인 유저. Archive 응답에서는 몰라도 돼서 주석 처리
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private Image img;
//        private boolean isEmpty;

    }

}
