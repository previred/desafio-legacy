package com.previred.empleados.exception;

public class EmpleadoNotFoundException extends RuntimeException {

    public EmpleadoNotFoundException(Long id) {
        super("Empleado no encontrado con ID: " + id);
    }
}
