package com.previred.desafio.validation;

import com.previred.desafio.dto.ApiError;
import com.previred.desafio.dto.EmpleadoRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Aplica validaciones de formato y reglas de negocio sobre empleados.
 */
@Component
public class EmpleadoValidator {

    private static final Pattern RUT_DNI_PATTERN = Pattern.compile(
            "^(\\d{7,8}-?[\\dkK]|\\d{7,10}|[A-Za-z0-9]{7,12})$"
    );
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");
    private static final BigDecimal PORCENTAJE_MAXIMO_BONO = new BigDecimal("0.50");

    /**
     * Valida el request y devuelve los errores encontrados.
     *
     * @param request datos del empleado a validar
     * @return lista de errores tipados
     */
    public List<ApiError> validate(EmpleadoRequest request) {
        List<ApiError> errors = new ArrayList<>();

        if (request == null) {
            errors.add(ApiError.of("requestBody", "REQUIRED_FIELD", "El cuerpo de la solicitud es obligatorio"));
            return errors;
        }

        if (isBlank(request.getNombre())) {
            errors.add(ApiError.of("nombre", "REQUIRED_FIELD", "El nombre es obligatorio"));
        }
        if (isBlank(request.getApellido())) {
            errors.add(ApiError.of("apellido", "REQUIRED_FIELD", "El apellido es obligatorio"));
        }
        if (isBlank(request.getRutDni())) {
            errors.add(ApiError.of("rutDni", "REQUIRED_FIELD", "El RUT/DNI es obligatorio"));
        } else if (!RUT_DNI_PATTERN.matcher(request.getRutDni().trim()).matches()) {
            errors.add(ApiError.of("rutDni", "INVALID_FORMAT", "El formato del RUT/DNI no es valido", request.getRutDni()));
        }
        if (isBlank(request.getCargo())) {
            errors.add(ApiError.of("cargo", "REQUIRED_FIELD", "El cargo es obligatorio"));
        }
        if (request.getSalarioBase() == null) {
            errors.add(ApiError.of("salarioBase", "REQUIRED_FIELD", "El salario base es obligatorio"));
        } else if (request.getSalarioBase().compareTo(SALARIO_MINIMO) < 0) {
            errors.add(ApiError.of(
                    "salarioBase",
                    "MIN_SALARY",
                    "El salario base no puede ser menor a 400000",
                    request.getSalarioBase()
            ));
        }
        if (request.getBono() == null) {
            errors.add(ApiError.of("bono", "REQUIRED_FIELD", "El bono es obligatorio"));
        } else if (request.getBono().compareTo(BigDecimal.ZERO) < 0) {
            errors.add(ApiError.of("bono", "INVALID_VALUE", "El bono no puede ser negativo", request.getBono()));
        }
        if (request.getDescuentos() == null) {
            errors.add(ApiError.of("descuentos", "REQUIRED_FIELD", "Los descuentos son obligatorios"));
        } else if (request.getDescuentos().compareTo(BigDecimal.ZERO) < 0) {
            errors.add(ApiError.of(
                    "descuentos",
                    "INVALID_VALUE",
                    "Los descuentos no pueden ser negativos",
                    request.getDescuentos()
            ));
        }

        if (request.getSalarioBase() != null && request.getBono() != null) {
            BigDecimal bonoMaximo = request.getSalarioBase().multiply(PORCENTAJE_MAXIMO_BONO);
            if (request.getBono().compareTo(bonoMaximo) > 0) {
                errors.add(ApiError.of(
                        "bono",
                        "BONUS_EXCEEDS_LIMIT",
                        "El bono no puede superar el 50% del salario base",
                        request.getBono()
                ));
            }
        }

        if (request.getSalarioBase() != null && request.getDescuentos() != null
                && request.getDescuentos().compareTo(request.getSalarioBase()) > 0) {
            errors.add(ApiError.of(
                    "descuentos",
                    "DISCOUNTS_EXCEED_SALARY",
                    "El total de descuentos no puede ser mayor al salario base",
                    request.getDescuentos()
            ));
        }

        return errors;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
