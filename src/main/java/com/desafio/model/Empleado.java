package com.desafio.model;

import lombok.Data;

@Data
public class Empleado {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String cargo;
    private double salario;
    private double bono;
    private double descuentos;
}
