package com.weave.weaveserver.dto;

import com.weave.weaveserver.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ArchiveRequest {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class createRequest{
        private Long userIdx;
        private Long teamIdx;
        private String title;
        private String content;
        private String url;
        private String imageUrl; //일단 한 장으로 진행
        private Long categoryIdx;
    }
}
