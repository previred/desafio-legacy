package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.exception.ValidationExceptionList;

/**
 * Interfaz para validacion de datos de empleados.
 *
 * <p>Esta interfaz define el contrato para componentes que pueden
 * validar objetos {@link EmpleadoRequest}. Implementada principalmente
 * por {@link ValidationService}.</p>
 *
 * <p>El proposito es permitir diferentes estrategias de validacion
 * y facilitar el testing mediante mocks.</p>
 *
 * @see ValidationService
 * @see EmpleadoRequest
 * @since 1.0
 */
public interface EmpleadoValidator {

    /**
     * Valida los datos de un request de empleado.
     *
     * <p>Si la validacion falla, se debe lanzar una excepcion
     * (normalmente {@link ValidationExceptionList}).</p>
     *
     * @param request el DTO de empleado a validar
     * @throws ValidationExceptionList si los datos no son validos
     */
    void validate(EmpleadoRequest request);
}