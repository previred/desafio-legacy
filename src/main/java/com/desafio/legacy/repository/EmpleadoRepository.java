package com.desafio.legacy.repository;

import java.util.List;

import com.desafio.legacy.model.Empleado;

public interface EmpleadoRepository {

    List<Empleado> findAll();

    List<Empleado> findByFilters(String term, String cargo);

    Empleado save(Empleado empleado);

    boolean deleteById(Long id);

    boolean existsByRutDni(String rutDni);
}
