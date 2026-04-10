package com.desafio.dto;

/**
 * Data Transfer Object para transportar datos de empleado entre capas.
 * Se usa para la serialización/deserialización JSON con Jackson.
 */
public class EmpleadoDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private double salarioBase;
    private double bonificaciones;
    private double descuentos;
    private double salarioLiquido;

    public EmpleadoDTO() {
    }

    public EmpleadoDTO(Long id, String nombre, String apellido, String rut,
                       String cargo, double salarioBase, double bonificaciones,
                       double descuentos, double salarioLiquido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.bonificaciones = bonificaciones;
        this.descuentos = descuentos;
        this.salarioLiquido = salarioLiquido;
    }

    // --- Getters y Setters ---

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

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public double getBonificaciones() {
        return bonificaciones;
    }

    public void setBonificaciones(double bonificaciones) {
        this.bonificaciones = bonificaciones;
    }

    public double getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(double descuentos) {
        this.descuentos = descuentos;
    }

    public double getSalarioLiquido() {
        return salarioLiquido;
    }

    public void setSalarioLiquido(double salarioLiquido) {
        this.salarioLiquido = salarioLiquido;
    }
}

