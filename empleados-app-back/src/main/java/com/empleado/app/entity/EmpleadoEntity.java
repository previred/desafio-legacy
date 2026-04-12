package com.empleado.app.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmpleadoEntity {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String cargo;
    private Double salario;
    
    private Double bono;
    private Double descuentos;

}