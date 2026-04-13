package com.desafio.legacy.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.desafio.legacy.dto.EmpleadoRequest;
import com.desafio.legacy.service.contract.EmpleadoValidator;

/**
 * Implementacion de validaciones de negocio para empleados.
 */
@Component
public class EmpleadoBusinessValidator implements EmpleadoValidator {

    private static final BigDecimal MIN_SALARIO_BASE = new BigDecimal("400000");
    private static final BigDecimal BONUS_FACTOR_MAX = new BigDecimal("0.5");
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Override
    public List<String> validate(EmpleadoRequest request) {
        List<String> details = new ArrayList<String>();
        if (request == null) {
            details.add("El cuerpo de la solicitud es obligatorio");
            return details;
        }

        BigDecimal salarioBase = request.getSalarioBase();
        if (salarioBase == null) {
            details.add("El salario base es obligatorio");
            return details;
        }

        BigDecimal bono = this.normalizeAmount(request.getBono());
        BigDecimal descuentos = this.normalizeAmount(request.getDescuentos());

        if (salarioBase.compareTo(MIN_SALARIO_BASE) < 0) {
            details.add("El salario base debe ser mayor o igual a 400000");
        }
        if (bono.compareTo(ZERO) < 0) {
            details.add("El bono no puede ser negativo");
        }
        if (descuentos.compareTo(ZERO) < 0) {
            details.add("Los descuentos no pueden ser negativos");
        }

        BigDecimal bonoMaximo = salarioBase.multiply(BONUS_FACTOR_MAX);
        if (bono.compareTo(bonoMaximo) > 0) {
            details.add("El bono no puede superar el 50% del salario base");
        }
        if (descuentos.compareTo(salarioBase) > 0) {
            details.add("El total de descuentos no puede ser mayor al salario base");
        }

        return details;
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? ZERO : amount;
    }
}
