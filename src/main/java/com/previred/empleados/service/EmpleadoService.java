package com.previred.empleados.service;

import com.previred.empleados.dto.EmpleadoDTO;
import com.previred.empleados.dto.EmpleadoRequestDTO;

import java.util.List;

public interface EmpleadoService {
    List<EmpleadoDTO> getAllEmpleados();
    EmpleadoDTO createEmpleado(EmpleadoRequestDTO request);
    void deleteEmpleado(Long id);
}
