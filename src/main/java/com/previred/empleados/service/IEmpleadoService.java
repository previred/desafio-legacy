package com.previred.empleados.service;

import com.previred.empleados.model.EmpleadoModel;

import java.util.List;

public interface IEmpleadoService {

    List<EmpleadoModel> listarEmpleados();

    EmpleadoModel crearEmpleado(EmpleadoModel model);

    void eliminarEmpleado(Long id);
}
