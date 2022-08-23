package com.weave.weaveserver.config.exception;

public class UnAuthorizedException extends RuntimeException{
    public UnAuthorizedException(String s){
        super(s);
    }
}
