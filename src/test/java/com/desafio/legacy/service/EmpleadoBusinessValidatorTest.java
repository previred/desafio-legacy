package com.desafio.legacy.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.desafio.legacy.dto.EmpleadoRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmpleadoBusinessValidatorTest {

    private final EmpleadoBusinessValidator validator = new EmpleadoBusinessValidator();

    @Test
    void shouldReturnNoErrorsWhenBusinessRulesAreValid() {
        EmpleadoRequest request = buildRequest(
            new BigDecimal("500000"),
            new BigDecimal("50000"),
            new BigDecimal("20000")
        );

        List<String> details = validator.validate(request);

        assertTrue(details.isEmpty());
    }

    @Test
    void shouldReturnAllErrorsWhenBusinessRulesAreInvalid() {
        EmpleadoRequest request = buildRequest(
            new BigDecimal("300000"),
            new BigDecimal("200000"),
            new BigDecimal("400000")
        );

        List<String> details = validator.validate(request);

        assertEquals(3, details.size());
        assertTrue(details.contains("El salario base debe ser mayor o igual a 400000"));
        assertTrue(details.contains("El bono no puede superar el 50% del salario base"));
        assertTrue(details.contains("El total de descuentos no puede ser mayor al salario base"));
    }

    @Test
    void shouldRejectNegativeBonoAndDescuentos() {
        EmpleadoRequest request = buildRequest(
            new BigDecimal("500000"),
            new BigDecimal("-1"),
            new BigDecimal("-1")
        );

        List<String> details = validator.validate(request);

        assertTrue(details.contains("El bono no puede ser negativo"));
        assertTrue(details.contains("Los descuentos no pueden ser negativos"));
    }

    @Test
    void shouldReturnErrorWhenRequestIsNull() {
        List<String> details = validator.validate(null);

        assertEquals(1, details.size());
        assertTrue(details.contains("El cuerpo de la solicitud es obligatorio"));
    }

    @Test
    void shouldReturnErrorWhenSalarioBaseIsNull() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Ana");
        request.setApellido("Perez");
        request.setRutDni("12345678-9");
        request.setCargo("Analista");
        request.setSalarioBase(null);

        List<String> details = validator.validate(request);

        assertEquals(1, details.size());
        assertTrue(details.contains("El salario base es obligatorio"));
    }

    private EmpleadoRequest buildRequest(BigDecimal salarioBase, BigDecimal bono, BigDecimal descuentos) {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Ana");
        request.setApellido("Perez");
        request.setRutDni("12345678-9");
        request.setCargo("Analista");
        request.setSalarioBase(salarioBase);
        request.setBono(bono);
        request.setDescuentos(descuentos);
        return request;
    }
}
