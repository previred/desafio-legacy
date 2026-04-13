package com.desafio.legacy.service.contract;

import java.util.List;

import com.desafio.legacy.dto.EmpleadoRequest;
import com.desafio.legacy.dto.EmpleadoResponse;

/**
 * Contrato de casos de uso para gestion de empleados.
 */
public interface EmpleadoService {

    /**
     * Obtiene todos los empleados sin filtros.
     */
    List<EmpleadoResponse> obtenerEmpleados();

    /**
     * Obtiene empleados aplicando filtros opcionales.
     */
    List<EmpleadoResponse> obtenerEmpleados(String term, String cargo);

    /**
     * Crea un nuevo empleado.
     */
    EmpleadoResponse crearEmpleado(EmpleadoRequest request);

    /**
     * Elimina un empleado por ID.
     */
    void eliminarEmpleado(Long id);
}
