package com.desafio.legacy.services;

import com.desafio.legacy.entities.Empleado;
import com.desafio.legacy.models.EmpleadoDTO;

import java.sql.SQLException;
import java.util.List;

public interface EmpleadoService {
    List<EmpleadoDTO> listar();
    void guardar(Empleado empleado) throws Exception;

    void eliminar(Long id) throws SQLException;
}
