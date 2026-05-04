package com.mindgrid.empleados.application.usecase;

import com.mindgrid.empleados.domain.model.Empleado;

import java.util.List;

public interface GestionEmpleadoUseCase {

    List<Empleado> listarEmpleados();

    Empleado agregarEmpleado(Empleado empleado);

    void eliminarEmpleado(Long id);
}
