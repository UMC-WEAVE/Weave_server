package com.weave.weaveserver.config.exception.jwt;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//jwt Filter Exception 관리
//https://bcp0109.tistory.com/303?category=981824
@Getter
public enum ExceptionCode {

    //jwt token error
    UNKNOWN_ERROR(false,401,"UNAUTHORIZED"),
    WRONG_TYPE_TOKEN(false, 405, "WRONG_TYPE_TOKEN"),
    EXPIRED_TOKEN(false, 401, "EXPIRED_TOKEN"),
    UNSUPPORTED_TOKEN(false, 401, "UNSUPPORTED_TOKEN"),
    ACCESS_DENIED(false, 403, "ACCESS_DENIED"),
    PERMISSION_DENIED(false, 403, "PERMISSION_DENIED"),
    WRONG_TOKEN(false, 400, "WRONG_TOKEN")
    ;

    private final String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private final boolean isSuccess;
    private final int status;
    private final String message;

    ExceptionCode(final boolean isSuccess, final int status, final String message) {
        this.isSuccess = isSuccess;
        this.status = status;
        this.message = message;
    }
}
