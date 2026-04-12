package com.empleado.app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String cargo;
    private Double salarioBase;
    
    private Double bono;
    private Double descuentos;
    private Double salarioNeto;
}