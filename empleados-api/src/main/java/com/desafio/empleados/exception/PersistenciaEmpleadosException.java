package com.desafio.empleados.exception;

/**
 * Fallo al acceder o modificar datos (p. ej. {@link java.sql.SQLException} no tratada como regla de negocio).
 */
public class PersistenciaEmpleadosException extends RuntimeException {

    public PersistenciaEmpleadosException(String message, Throwable cause) {
        super(message, cause);
    }
}
