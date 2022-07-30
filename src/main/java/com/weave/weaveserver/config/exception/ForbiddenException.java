package com.weave.weaveserver.config.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String s){
        super(s);
    }
}
