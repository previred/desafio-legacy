package com.danielr.desafio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record EmployeeRequestDTO(

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
    BigDecimal deductions

) {}
