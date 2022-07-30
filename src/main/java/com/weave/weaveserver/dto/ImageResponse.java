package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ImageResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class imageResponse{
        private Long imageIdx;
        private String url;
        private Long archiveIdx;
    }

}
