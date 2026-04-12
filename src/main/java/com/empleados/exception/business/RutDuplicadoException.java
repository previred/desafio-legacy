package com.empleados.exception.business;

/**
 * Excepcion de negocio lanzada cuando se intenta registrar un empleado
 * con un RUT que ya existe en la base de datos.
 * <p>
 * Es una {@link RuntimeException} no verificada, capturada en
 * {@link com.empleados.controller.EmpleadoServlet} para retornar HTTP 400.
 * </p>
 */
public class RutDuplicadoException extends RuntimeException {

    /**
     * Crea la excepcion con un mensaje descriptivo.
     *
     * @param message detalle del RUT duplicado
     */
    public RutDuplicadoException(String message) {
        super(message);
    }

    /**
     * Crea la excepcion con mensaje y causa original.
     *
     * @param message detalle del RUT duplicado
     * @param cause   excepcion original que causo el error
     */
    public RutDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
