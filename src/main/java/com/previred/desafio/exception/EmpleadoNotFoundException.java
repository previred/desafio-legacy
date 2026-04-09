package com.previred.desafio.exception;

public class EmpleadoNotFoundException extends RuntimeException {

    public EmpleadoNotFoundException(Long id) {
        super("No se encontró el empleado con ID: " + id);
    }
}
