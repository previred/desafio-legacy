package com.desafio.legacy.repositories;

import com.desafio.legacy.entities.Empleado;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository {
    List<Empleado> listarEmpleados();
    void registrarEmpleado(Empleado empleado);

    void eliminarEmpleado(Long id) throws SQLException;

    Optional<Empleado> buscarPorRut(String rut);
}
