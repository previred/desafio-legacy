package com.previred.empleados.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(List<String> errors) {
        super("Errores de validación");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
