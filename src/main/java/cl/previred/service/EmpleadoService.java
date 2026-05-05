package cl.previred.service;

import cl.previred.dto.EmpleadoRequest;
import cl.previred.dto.EmpleadoResponse;

import java.util.List;

public interface EmpleadoService {
    List<EmpleadoResponse> listar();
    EmpleadoResponse crear(EmpleadoRequest request);
    void eliminar(Long id);
}
