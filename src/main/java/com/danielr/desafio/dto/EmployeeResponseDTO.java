package com.danielr.desafio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record EmployeeResponseDTO(

        @JsonProperty("id")
        Long id,

        @JsonProperty("nombre")
        String name,

        @JsonProperty("apellido")
        String lastname,

        @JsonProperty("rut")
        String dni,

        @JsonProperty("cargo")
        String position,

        @JsonProperty("salario")
        BigDecimal salary,

        @JsonProperty("bono")
        BigDecimal bonus,

        @JsonProperty("descuentos")
        BigDecimal deductions,

        @JsonProperty("fecha_creacion")
        String created

) {
}
