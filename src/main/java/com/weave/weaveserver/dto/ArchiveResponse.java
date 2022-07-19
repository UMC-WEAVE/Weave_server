package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ArchiveResponse {

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class listResponse{
        private int categoryIdx; //임시

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class detailResponse{
        private int archiveIdx; //임시

    }

}
