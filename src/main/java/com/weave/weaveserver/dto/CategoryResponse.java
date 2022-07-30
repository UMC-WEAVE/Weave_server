package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CategoryResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class categoryResponse{
        private Long categoryIdx;
        private String categoryName;
    }

}
