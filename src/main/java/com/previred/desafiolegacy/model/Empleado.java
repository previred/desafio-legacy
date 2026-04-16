package com.previred.desafiolegacy.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.math.BigDecimal;

public class Empleado {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private BigDecimal salarioBase;
    private BigDecimal bono = BigDecimal.ZERO;
    private BigDecimal descuentos = BigDecimal.ZERO;

    public Empleado() {}

    public Empleado(Long id, String nombre, String apellido, String rut,
                    String cargo, BigDecimal salarioBase, BigDecimal bono, BigDecimal descuentos) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.bono = bono;
        this.descuentos = descuentos;
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

    public BigDecimal getSalarioBase() { return salarioBase; }
    public void setSalarioBase(BigDecimal salarioBase) { this.salarioBase = salarioBase; }

    public BigDecimal getBono() { return bono; }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setBono(BigDecimal bono) { this.bono = bono; }

    public BigDecimal getDescuentos() { return descuentos; }
    @JsonSetter(nulls = Nulls.SKIP)
    public void setDescuentos(BigDecimal descuentos) { this.descuentos = descuentos; }
}
