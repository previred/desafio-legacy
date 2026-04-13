package com.desafio.legacy.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El campo nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El campo apellido es obligatorio")
    private String apellido;
    @NotBlank(message = "El campo rut_dni es obligatorio")
    private String rut_dni;
    @NotBlank(message = "El campo cargo es obligatorio")
    private String cargo;
    @Min(value = 400000, message = "El salario mínimo es $400.000")
    @NotNull(message = "El campo salario es obligatorio")
    private Double salario;
    private Double bono;
    private Double descuento;
    private boolean activo = true;

    public Empleado() {
    }

    public Empleado(Long id, String nombre, String apellido, String rut_dni, String cargo, Double salario) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut_dni = rut_dni;
        this.cargo = cargo;
        this.salario = salario;
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
    public String getRut_dni() {
        return rut_dni;
    }
    public void setRut_dni(String rut_dni) {
        this.rut_dni = rut_dni;
    }
    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    public Double getSalario() {
        return salario;
    }
    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public Double getBono() {
        return bono;
    }
    public void setBono(Double bono) {
        this.bono = bono;
    }
    public Double getDescuento() {
        return descuento;
    }
    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
