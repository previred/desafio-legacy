package com.mindgrid.empleados.adapters.inbound.web.request;

public class EmpleadoRequest {

    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private double salarioBase;
    private double bono;
    private double descuentos;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public double getSalarioBase() { return salarioBase; }
    public void setSalarioBase(double salarioBase) { this.salarioBase = salarioBase; }
    public double getBono() { return bono; }
    public void setBono(double bono) { this.bono = bono; }
    public double getDescuentos() { return descuentos; }
    public void setDescuentos(double descuentos) { this.descuentos = descuentos; }
}
