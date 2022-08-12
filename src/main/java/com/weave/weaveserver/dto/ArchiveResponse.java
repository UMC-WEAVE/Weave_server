package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ArchiveResponse {


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class archiveListResponseContainer{
        private TeamResponse.teamWithDateListResponse team; //TODO : 이걸 밖으로 꺼내서 archiveListResponse와 teamWithDateListResponse를 감싸는 DTO 필요
        private List<archiveListResponse> archiveList;
    }




    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class archiveListResponse{
        private Long archiveIdx;
        private CategoryResponse.categoryResponse category;
        private UserResponse.userResponse user;
        private String title;
        private String content;
        private ImageResponse.imageResponse image;
        private Boolean isPinned;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class archiveResponse{
        private Long archiveIdx;
        private CategoryResponse.categoryResponse category;
        private UserResponse.userResponse user;
        private String title;
        private String content;
        private String url;
        private List<ImageResponse.imageResponse> imageList;
        private Boolean is_pinned;

    }

}
