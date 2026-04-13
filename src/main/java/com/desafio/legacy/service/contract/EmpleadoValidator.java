package com.desafio.legacy.service.contract;

import java.util.List;

import com.desafio.legacy.dto.EmpleadoRequest;

/**
 * Contrato para validar reglas de negocio de empleados.
 */
public interface EmpleadoValidator {

    /**
     * Valida una solicitud de empleado y retorna los mensajes de error encontrados.
     */
    List<String> validate(EmpleadoRequest request);
}
