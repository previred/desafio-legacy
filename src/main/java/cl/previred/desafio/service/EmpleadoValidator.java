package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;

public interface EmpleadoValidator {
    void validate(EmpleadoRequest request);
}