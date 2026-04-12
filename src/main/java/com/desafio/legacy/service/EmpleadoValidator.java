package com.desafio.legacy.service;

import java.util.List;

import com.desafio.legacy.dto.EmpleadoRequest;

public interface EmpleadoValidator {

    List<String> validate(EmpleadoRequest request);
}
