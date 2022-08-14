package com.weave.weaveserver.config.exception;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String s){
        super(s);
    }
}
