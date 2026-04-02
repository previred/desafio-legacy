package com.previred.empleados.exception;

public class EmpleadoNotFoundException extends RuntimeException {
    public EmpleadoNotFoundException(Long id) {
        super("Empleado con ID " + id + " no encontrado");
    }

    public EmpleadoNotFoundException(String message) {
        super(message);
    }
}
