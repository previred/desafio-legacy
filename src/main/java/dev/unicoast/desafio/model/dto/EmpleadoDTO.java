package dev.unicoast.desafio.model.dto;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class EmpleadoDTO {

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

    @SerializedName("salarioBase")
    private BigDecimal salarioBase;

    @SerializedName("bonos")
    private BigDecimal bonos;

    @SerializedName("descuentos")
    private BigDecimal descuentos;

    public EmpleadoDTO() {
        // Constructor vacío requerido por Gson para la deserialización JSON
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

    public BigDecimal getSalarioBase() { 
        return salarioBase; 
    }
    
    public void setSalarioBase(BigDecimal salarioBase) { 
        this.salarioBase = salarioBase; 
    }

    public BigDecimal getBonos() { 
        return bonos; 
    }
    
    public void setBonos(BigDecimal bonos) { 
        this.bonos = bonos; 
    }

    public BigDecimal getDescuentos() { 
        return descuentos; 
    }
    
    public void setDescuentos(BigDecimal descuentos) { 
        this.descuentos = descuentos; 
    }
}
