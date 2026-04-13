package com.desafio.legacy.repository.contract;

import java.util.List;

import com.desafio.legacy.model.Empleado;

/**
 * Puerto de persistencia para operaciones de empleados sobre base de datos.
 */
public interface EmpleadoRepository {

    /**
     * Lista todos los empleados.
     */
    List<Empleado> findAll();

    /**
     * Lista empleados aplicando filtros opcionales por texto y cargo.
     */
    List<Empleado> findByFilters(String term, String cargo);

    /**
     * Persiste un empleado y retorna el registro con ID generado.
     */
    Empleado save(Empleado empleado);

    /**
     * Elimina por ID.
     *
     * @return true si elimino al menos un registro
     */
    boolean deleteById(Long id);

    /**
     * Verifica si existe un empleado con el RUT/DNI indicado.
     */
    boolean existsByRutDni(String rutDni);
}
