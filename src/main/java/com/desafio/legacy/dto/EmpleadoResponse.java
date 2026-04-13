package com.desafio.legacy.dto;

import java.math.BigDecimal;

public class EmpleadoResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String rutDni;
    private String cargo;
    private BigDecimal salarioBase;
    private BigDecimal bono;
    private BigDecimal descuentos;

    public EmpleadoResponse() {
    }

    public EmpleadoResponse(
        Long id,
        String nombre,
        String apellido,
        String rutDni,
        String cargo,
        BigDecimal salarioBase,
        BigDecimal bono,
        BigDecimal descuentos
    ) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rutDni = rutDni;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.bono = bono;
        this.descuentos = descuentos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRutDni() {
        return rutDni;
    }

    public void setRutDni(String rutDni) {
        this.rutDni = rutDni;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public BigDecimal getBono() {
        return bono;
    }

    public void setBono(BigDecimal bono) {
        this.bono = bono;
    }

    public BigDecimal getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }
}
