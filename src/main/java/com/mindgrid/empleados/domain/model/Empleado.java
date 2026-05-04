package com.mindgrid.empleados.domain.model;

public class Empleado {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private double salarioBase;
    private double bono;
    private double descuentos;

    private Empleado() {}

    public double getSalarioNeto() {
        return salarioBase + bono - descuentos;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getRut() { return rut; }
    public String getCargo() { return cargo; }
    public double getSalarioBase() { return salarioBase; }
    public double getBono() { return bono; }
    public double getDescuentos() { return descuentos; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Empleado empleado = new Empleado();

        public Builder id(Long id) { empleado.id = id; return this; }
        public Builder nombre(String nombre) { empleado.nombre = nombre; return this; }
        public Builder apellido(String apellido) { empleado.apellido = apellido; return this; }
        public Builder rut(String rut) { empleado.rut = rut; return this; }
        public Builder cargo(String cargo) { empleado.cargo = cargo; return this; }
        public Builder salarioBase(double salarioBase) { empleado.salarioBase = salarioBase; return this; }
        public Builder bono(double bono) { empleado.bono = bono; return this; }
        public Builder descuentos(double descuentos) { empleado.descuentos = descuentos; return this; }

        public Empleado build() { return empleado; }
    }
}
