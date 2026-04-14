package com.prueba.desafio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "app.empleado")
public class EmpleadoProperties {

    private BigDecimal salarioMinimo;
    private BigDecimal bonoMaxPorcentaje;

    public BigDecimal getSalarioMinimo() {
        return salarioMinimo;
    }

    public void setSalarioMinimo(BigDecimal salarioMinimo) {
        this.salarioMinimo = salarioMinimo;
    }

    public BigDecimal getBonoMaxPorcentaje() {
        return bonoMaxPorcentaje;
    }

    public void setBonoMaxPorcentaje(BigDecimal bonoMaxPorcentaje) {
        this.bonoMaxPorcentaje = bonoMaxPorcentaje;
    }
}
