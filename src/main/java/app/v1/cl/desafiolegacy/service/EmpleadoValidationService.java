package app.v1.cl.desafiolegacy.service;

import app.v1.cl.desafiolegacy.model.Empleado;

import java.util.List;

public interface EmpleadoValidationService {

    List<String> validar(Empleado empleado);
}
