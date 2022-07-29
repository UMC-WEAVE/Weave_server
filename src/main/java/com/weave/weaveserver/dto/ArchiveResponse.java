package com.weave.weaveserver.dto;

import com.weave.weaveserver.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ArchiveResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class archiveResponse{
        private Long archiveIdx;
        private Long categoryIdx;
        private String categoryName;
        private Long teamIdx;
        private Long userIdx;
        private String title;
        private String content;
        private String url;
        private List<ImageResponse.imageResponse> imageList;
        private Boolean is_pinned;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class archiveListResponse{
        private Long archiveIdx;
        private Long categoryIdx;
        private String categoryName;
        private Long teamIdx;
        private Long userIdx;
        private String title;
        private String content;
        private ImageResponse.imageResponse image;
        private Boolean isPinned;

    }

}
