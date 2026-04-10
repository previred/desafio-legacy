package dev.unicoast.desafio.service;

import dev.unicoast.desafio.model.dto.EmpleadoDTO;
import java.util.List;

public interface EmpleadoService {
    void registrarEmpleado(EmpleadoDTO dto);
    List<EmpleadoDTO> listarEmpleados();
    void eliminarEmpleado(String idParam);
}
