package com.desafio.legacy.dto;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class EmpleadoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar 100 caracteres")
    private String apellido;

    @NotBlank(message = "El RUT/DNI es obligatorio")
    @Size(min = 5, max = 20, message = "El RUT/DNI debe tener entre 5 y 20 caracteres")
    @Pattern(regexp = "^[0-9A-Za-z.-]{5,20}$", message = "El formato de RUT/DNI es invalido")
    private String rutDni;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 100, message = "El cargo no puede superar 100 caracteres")
    private String cargo;

    @NotNull(message = "El salario base es obligatorio")
    @DecimalMin(value = "400000.00", message = "El salario base debe ser mayor o igual a 400000")
    @Digits(integer = 13, fraction = 2, message = "El salario base tiene formato invalido")
    private BigDecimal salarioBase;

    @DecimalMin(value = "0.00", message = "El bono no puede ser negativo")
    @Digits(integer = 13, fraction = 2, message = "El bono tiene formato invalido")
    private BigDecimal bono;

    @DecimalMin(value = "0.00", message = "Los descuentos no pueden ser negativos")
    @Digits(integer = 13, fraction = 2, message = "Los descuentos tienen formato invalido")
    private BigDecimal descuentos;

    public EmpleadoRequest() {
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
