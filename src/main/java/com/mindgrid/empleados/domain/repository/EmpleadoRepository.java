package com.mindgrid.empleados.domain.repository;

import com.mindgrid.empleados.domain.model.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository {

    List<Empleado> findAll();

    Empleado save(Empleado empleado);

    boolean deleteById(Long id);

    boolean existsByRut(String rut);

    Optional<Empleado> findById(Long id);
}
