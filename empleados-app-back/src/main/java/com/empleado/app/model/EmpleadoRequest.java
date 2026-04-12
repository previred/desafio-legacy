package com.empleado.app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoRequest {

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("dni")
    private String dni;

    @JsonProperty("cargo")
    private String cargo;

    @JsonProperty("salario")
    private Double salario;

    @JsonProperty("bono")
    private Double bono;

    @JsonProperty("descuentos")
    private Double descuentos;
}