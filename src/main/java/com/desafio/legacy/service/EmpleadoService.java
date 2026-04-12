package com.desafio.legacy.service;

import java.util.List;

import com.desafio.legacy.dto.EmpleadoRequest;
import com.desafio.legacy.dto.EmpleadoResponse;

public interface EmpleadoService {

    List<EmpleadoResponse> obtenerEmpleados();

    List<EmpleadoResponse> obtenerEmpleados(String term, String cargo);

    EmpleadoResponse crearEmpleado(EmpleadoRequest request);

    void eliminarEmpleado(Long id);
}
