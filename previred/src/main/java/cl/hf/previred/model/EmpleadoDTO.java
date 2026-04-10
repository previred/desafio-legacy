package cl.hf.previred.model;

import java.io.Serializable;

public class EmpleadoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private String cargo;
    private Double salario;
    private Double bono;
    private Double descuento;

    public EmpleadoDTO() {
    }

    // Constructor actualizado para incluir todos los campos (útil para el DAO)
    public EmpleadoDTO(Long id, String nombre, String apellido, String rut, String cargo, 
                       Double salario, Double bono, Double descuento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.cargo = cargo;
        this.salario = salario;
        this.bono = bono;
        this.descuento = descuento;
    }

    // Getters y Setters existentes...
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

    public Double getSalario() { return salario; }
    public void setSalario(Double salario) { this.salario = salario; }

    // --- NUEVOS Getters y Setters ---
    public Double getBono() {
        return bono != null ? bono : 0.0;
    }

    public void setBono(Double bono) {
        this.bono = bono;
    }

    public Double getDescuento() {
        return descuento != null ? descuento : 0.0;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    @Override
    public String toString() {
        return "EmpleadoDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rut='" + rut + '\'' +
                ", cargo='" + cargo + '\'' +
                ", salario=" + salario +
                ", bono=" + bono +
                ", descuento=" + descuento +
                '}';
    }
}