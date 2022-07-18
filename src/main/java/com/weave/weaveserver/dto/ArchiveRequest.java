package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ArchiveRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class createRequest{
        private int userIdx;
        private int teamIdx;
        private String title;
        private String content;
        private String url;
        private String imgUrl;
        private int categoryIdx;
    }
}
