package com.previred.desafio.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

public class EmpleadoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El RUT/DNI es obligatorio")
    @Pattern(
        regexp = "^[0-9]{7,8}-[0-9Kk]$",
        message = "El RUT/DNI debe tener el formato 12345678-9 o 12345678-K"
    )
    private String rut;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    @NotNull(message = "El salario es obligatorio")
    @DecimalMin(value = "400000", message = "El salario base no puede ser menor a $400.000")
    private BigDecimal salario;

    @NotNull(message = "El bono es obligatorio")
    @DecimalMin(value = "0", message = "El bono no puede ser negativo")
    private BigDecimal bono;

    @NotNull(message = "El descuento es obligatorio")
    @DecimalMin(value = "0", message = "El descuento no puede ser negativo")
    private BigDecimal descuento;

    public EmpleadoRequest() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }

    public BigDecimal getBono() { return bono; }
    public void setBono(BigDecimal bono) { this.bono = bono; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
}
