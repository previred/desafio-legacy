package com.previred.empleados.model;

import com.google.gson.annotations.SerializedName;

public class EmpleadoModel {

    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("rut")
    private String rut;

    @SerializedName("cargo")
    private String cargo;

    @SerializedName("salario_base")
    private Double salarioBase;

    @SerializedName("bonos")
    private Double bonos;

    @SerializedName("descuentos")
    private Double descuentos;

    public EmpleadoModel() {
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

    public Double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(Double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public Double getBonos() {
        return bonos;
    }

    public void setBonos(Double bonos) {
        this.bonos = bonos;
    }

    public Double getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(Double descuentos) {
        this.descuentos = descuentos;
    }
}
