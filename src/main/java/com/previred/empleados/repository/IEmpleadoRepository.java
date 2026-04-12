package com.previred.empleados.repository;

import com.previred.empleados.entity.EmpleadoEntity;

import java.util.List;
import java.util.Optional;

public interface IEmpleadoRepository {

    List<EmpleadoEntity> findAll();

    Optional<EmpleadoEntity> findById(Long id);

    boolean existsByRut(String rut);

    EmpleadoEntity save(EmpleadoEntity entity);

    boolean deleteById(Long id);
}
