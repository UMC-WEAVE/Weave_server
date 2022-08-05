package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ArchiveResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class archiveListResponse{
        private Long archiveIdx;
        private CategoryResponse.categoryResponse category;
        private TeamResponse.teamWithDateListResponse team;
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
