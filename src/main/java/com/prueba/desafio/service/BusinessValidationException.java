package com.prueba.desafio.service;

import com.prueba.desafio.dto.ValidationError;

import java.util.List;

public class BusinessValidationException extends RuntimeException {

    private final List<ValidationError> errors;

    public BusinessValidationException(String message, List<ValidationError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}