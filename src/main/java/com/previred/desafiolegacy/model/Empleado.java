package com.previred.desafiolegacy.model;

public class Empleado {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private double salarioBase;
    private double bono;
    private double descuentos;

    public Empleado() {}

    public Empleado(Long id, String nombre, String apellido, String rut,
                    String cargo, double salarioBase, double bono, double descuentos) {
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

    public double getSalarioBase() { return salarioBase; }
    public void setSalarioBase(double salarioBase) { this.salarioBase = salarioBase; }

    public double getBono() { return bono; }
    public void setBono(double bono) { this.bono = bono; }

    public double getDescuentos() { return descuentos; }
    public void setDescuentos(double descuentos) { this.descuentos = descuentos; }
}
