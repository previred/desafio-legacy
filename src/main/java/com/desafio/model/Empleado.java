package com.desafio.model;

/**
 * Entidad de dominio que representa un Empleado en el sistema.
 * Mapea directamente a la tabla 'empleados' de la base de datos H2.
 */
public class Empleado {

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private double salarioBase;
    private double bonificaciones;
    private double descuentos;

    public Empleado() {
    }

    public Empleado(Long id, String nombre, String apellido, String rut,
                    String cargo, double salarioBase, double bonificaciones, double descuentos) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.bonificaciones = bonificaciones;
        this.descuentos = descuentos;
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

    /**
     * Calcula el salario líquido: salarioBase + bonificaciones - descuentos.
     */
    public double getSalarioLiquido() {
        return salarioBase + bonificaciones - descuentos;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", rut='" + rut + '\'' +
                ", cargo='" + cargo + '\'' +
                ", salarioBase=" + salarioBase +
                ", bonificaciones=" + bonificaciones +
                ", descuentos=" + descuentos +
                '}';
    }
}

