package com.previred.empleados.exception;

public class DuplicateRutException extends RuntimeException {
    public DuplicateRutException(String rut) {
        super("El RUT " + rut + " ya existe en el sistema");
    }

    public DuplicateRutException(String message, Throwable cause) {
        super(message, cause);
    }
}
