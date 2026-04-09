package com.previred.desafio.exception;

public class RutDuplicadoException extends RuntimeException {

    public RutDuplicadoException(String rut) {
        super("Ya existe un empleado con el RUT/DNI: " + rut);
    }
}
