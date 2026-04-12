package com.empleado.app.exception;

import java.util.List;

/**
 * Excepción personalizada para la API.
 * Se utiliza para manejar errores de negocio y validaciones.
 * Permite retornar una lista de errores detallados al cliente.
 * Autor: Cristian Palacios
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Lista de errores asociados a la excepción.
     * Puede contener múltiples mensajes de validación o negocio.
     */
    private List<String> errors;

    /**
     * Constructor principal de la excepción.
     * @param errors Lista de errores que describen el problema ocurrido.
     */
    public ApiException(List<String> errors) {
        this.errors = errors;
    }

    /**
     * Retorna la lista de errores asociados a la excepción.
     * @return Lista de mensajes de error.
     */
    public List<String> getErrors() {
        return errors;
    }
}