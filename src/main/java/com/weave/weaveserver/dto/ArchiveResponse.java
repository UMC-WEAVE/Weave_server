package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ArchiveResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class archiveResponse{
        private Long archiveIdx;
        private Long category_idx;
        private String category_name;
        private Long team_idx;
        private Long user_idx;
        private String title;
        private String content;
        private String url;
        private String image_url;
        private Boolean is_pinned;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class archiveListResponse{
        private Long archiveIdx;
        private Long category_idx;
        private String category_name;
        private Long team_idx;
        private Long user_idx;
        private String title;
        private String content;
        private String image_url;
        private Boolean is_pinned;

    }

}
