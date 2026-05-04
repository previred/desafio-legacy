package com.previred.empleados.dto;

import java.math.BigDecimal;

public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private BigDecimal salarioBase;
    private BigDecimal bonos;
    private BigDecimal descuentos;
    private BigDecimal salarioTotal;

    public EmpleadoDTO() {
    }

    public EmpleadoDTO(Long id, String nombre, String apellido, String rut, String cargo,
                       BigDecimal salarioBase, BigDecimal bonos, BigDecimal descuentos, BigDecimal salarioTotal) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.bonos = bonos;
        this.descuentos = descuentos;
        this.salarioTotal = salarioTotal;
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

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
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

    public BigDecimal getBonos() {
        return bonos;
    }

    public void setBonos(BigDecimal bonos) {
        this.bonos = bonos;
    }

    public BigDecimal getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }

    public BigDecimal getSalarioTotal() {
        return salarioTotal;
    }

    public void setSalarioTotal(BigDecimal salarioTotal) {
        this.salarioTotal = salarioTotal;
    }
}
