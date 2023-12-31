package com.github.davitavora.patch.exception;

public class UnprocessableEntityException extends RuntimeException {

    public UnprocessableEntityException(Exception exception) {
        super(exception);
    }

}
