package com.previred.empleados.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Empleado {
    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private BigDecimal salarioBase;
    private BigDecimal bonos;
    private BigDecimal descuentos;
    private LocalDateTime fechaCreacion;

    public Empleado() {
        this.bonos = BigDecimal.ZERO;
        this.descuentos = BigDecimal.ZERO;
    }

    public Empleado(String nombre, String apellido, String rut, String cargo,
                    BigDecimal salarioBase, BigDecimal bonos, BigDecimal descuentos) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.bonos = bonos != null ? bonos : BigDecimal.ZERO;
        this.descuentos = descuentos != null ? descuentos : BigDecimal.ZERO;
    }

    public BigDecimal calcularSalarioTotal() {
        return salarioBase.add(bonos).subtract(descuentos);
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
