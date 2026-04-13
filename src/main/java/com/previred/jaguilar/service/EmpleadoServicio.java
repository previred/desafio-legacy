package com.previred.jaguilar.service;

import com.previred.jaguilar.model.Empleado;
import com.previred.jaguilar.model.ErrorValidacion;

import java.util.List;

public interface  EmpleadoServicio {

    List<Empleado> listarEmpleados();

    void registrarEmpleado(Empleado empleado, List<ErrorValidacion> errores);

    void eliminarEmpleado(Long id, List<ErrorValidacion> errores);
}
