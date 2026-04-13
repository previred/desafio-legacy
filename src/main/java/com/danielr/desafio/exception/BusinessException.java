package com.danielr.desafio.exception;

import java.util.List;

public class BusinessException extends RuntimeException {

    private final List<String> errors;

    public BusinessException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public BusinessException(List<String> errors) {
        super(String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
