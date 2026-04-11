package com.previred.empleados.exception;

import java.util.List;

public class BusinessValidationException extends RuntimeException {

    private final List<String> errores;

    public BusinessValidationException(List<String> errores) {
        super("Error de validación de negocio");
        this.errores = errores;
    }

    public List<String> getErrores() {
        return errores;
    }
}
