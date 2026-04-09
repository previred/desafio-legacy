package com.previred.desafio.model;

import java.math.BigDecimal;

public class Empleado {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private BigDecimal salario;
    private BigDecimal bono;
    private BigDecimal descuento;

    public Empleado() {}

    public Empleado(Long id, String nombre, String apellido, String rut, String cargo,
                    BigDecimal salario, BigDecimal bono, BigDecimal descuento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.cargo = cargo;
        this.salario = salario;
        this.bono = bono;
        this.descuento = descuento;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public BigDecimal getBono() { return bono; }
    public void setBono(BigDecimal bono) { this.bono = bono; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
}
