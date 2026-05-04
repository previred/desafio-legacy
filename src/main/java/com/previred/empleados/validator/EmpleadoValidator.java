package com.previred.empleados.validator;

import com.previred.empleados.dto.EmpleadoRequestDTO;

import java.math.BigDecimal;

public class EmpleadoValidator {

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");
    private static final BigDecimal SALARIO_MAXIMO = new BigDecimal("100000000");
    private static final BigDecimal BONO_MAX_PERCENTAGE = new BigDecimal("0.5");

    public ValidationResult validate(EmpleadoRequestDTO request) {
        ValidationResult result = new ValidationResult();

        validateNombre(request.getNombre(), result);
        validateApellido(request.getApellido(), result);
        validateRut(request.getRut(), result);
        validateCargo(request.getCargo(), result);
        validateSalarioBase(request.getSalarioBase(), result);
        validateBonos(request.getSalarioBase(), request.getBonos(), result);
        validateDescuentos(request.getSalarioBase(), request.getDescuentos(), result);

        return result;
    }

    private void validateNombre(String nombre, ValidationResult result) {
        if (nombre == null || nombre.trim().isEmpty()) {
            result.addError("El nombre es obligatorio");
        } else if (nombre.trim().length() > 100) {
            result.addError("El nombre no puede exceder 100 caracteres");
        }
    }

    private void validateApellido(String apellido, ValidationResult result) {
        if (apellido == null || apellido.trim().isEmpty()) {
            result.addError("El apellido es obligatorio");
        } else if (apellido.trim().length() > 100) {
            result.addError("El apellido no puede exceder 100 caracteres");
        }
    }

    private void validateRut(String rut, ValidationResult result) {
        if (rut == null || rut.trim().isEmpty()) {
            result.addError("El RUT es obligatorio");
        } else if (!RutValidator.isValidRut(rut)) {
            result.addError("El RUT no es válido");
        }
    }

    private void validateCargo(String cargo, ValidationResult result) {
        if (cargo == null || cargo.trim().isEmpty()) {
            result.addError("El cargo es obligatorio");
        } else if (cargo.trim().length() > 100) {
            result.addError("El cargo no puede exceder 100 caracteres");
        }
    }

    private void validateSalarioBase(BigDecimal salarioBase, ValidationResult result) {
        if (salarioBase == null) {
            result.addError("El salario base es obligatorio");
        } else if (salarioBase.compareTo(BigDecimal.ZERO) <= 0) {
            result.addError("El salario base debe ser mayor a cero");
        } else if (salarioBase.compareTo(SALARIO_MINIMO) < 0) {
            result.addError("El salario base debe ser al menos $400.000");
        } else if (salarioBase.compareTo(SALARIO_MAXIMO) > 0) {
            result.addError("El salario base no puede superar $100.000.000");
        }
    }

    private void validateBonos(BigDecimal salarioBase, BigDecimal bonos, ValidationResult result) {
        if (bonos == null) {
            return;
        }

        if (bonos.compareTo(BigDecimal.ZERO) < 0) {
            result.addError("Los bonos no pueden ser negativos");
            return;
        }

        if (salarioBase != null) {
            BigDecimal maxBonos = salarioBase.multiply(BONO_MAX_PERCENTAGE);
            if (bonos.compareTo(maxBonos) > 0) {
                result.addError("Los bonos no pueden superar el 50% del salario base");
            }
        }
    }

    private void validateDescuentos(BigDecimal salarioBase, BigDecimal descuentos, ValidationResult result) {
        if (descuentos == null) {
            return;
        }

        if (descuentos.compareTo(BigDecimal.ZERO) < 0) {
            result.addError("Los descuentos no pueden ser negativos");
            return;
        }

        if (salarioBase != null && descuentos.compareTo(salarioBase) > 0) {
            result.addError("Los descuentos no pueden superar el salario base");
        }
    }
}
