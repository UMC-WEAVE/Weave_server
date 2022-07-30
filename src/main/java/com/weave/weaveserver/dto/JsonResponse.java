package com.weave.weaveserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonResponse {
//    private final boolean success;
    private int status;
    private String message;
    private Object data;
}
