package com.previred.empleados.dao;

import com.previred.empleados.model.Empleado;

import java.util.List;
import java.util.Optional;

public interface EmpleadoDAO {
    List<Empleado> findAll();
    Optional<Empleado> findById(Long id);
    Empleado save(Empleado empleado);
    boolean existsByRut(String rut);
    void deleteById(Long id);
}
