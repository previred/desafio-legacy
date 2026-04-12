package com.empleados.validation;

import com.empleados.model.Empleado;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validador personalizado que implementa las reglas de negocio
 * definidas por la anotacion {@link EmpleadoValido}.
 * <p>
 * Reglas validadas:
 * <ul>
 *   <li>Bono &le; 50% del salario base</li>
 *   <li>Descuentos &le; salario base</li>
 * </ul>
 * Cada violacion se agrega como error individual asociado al campo correspondiente.
 * </p>
 */
public class EmpleadoValidator implements ConstraintValidator<EmpleadoValido, Empleado> {

    /** {@inheritDoc} */
    @Override
    public void initialize(EmpleadoValido annotation) {
    }

    /**
     * Valida las reglas de negocio entre campos del empleado.
     *
     * @param empleado entidad a validar (puede ser {@code null}, en cuyo caso se considera valido)
     * @param context  contexto de validacion para agregar violaciones personalizadas
     * @return {@code true} si todas las reglas se cumplen
     */
    @Override
    public boolean isValid(Empleado empleado, ConstraintValidatorContext context) {
        if (empleado == null) {
            return true;
        }

        boolean valid = true;

        if (empleado.getBono() != null && empleado.getSalario() != null &&
            empleado.getBono() > empleado.getSalario() / 2) {
            addConstraintViolation(context, "El bono no puede superar el 50% del salario base", "bono");
            valid = false;
        }

        if (empleado.getDescuentos() != null && empleado.getSalario() != null &&
            empleado.getDescuentos() > empleado.getSalario()) {
            addConstraintViolation(context, "El total de descuentos no puede ser mayor al salario base", "descuentos");
            valid = false;
        }

        return valid;
    }

    /**
     * Agrega una violacion personalizada asociada a un campo especifico.
     *
     * @param context  contexto de validacion
     * @param message  mensaje de error descriptivo
     * @param property nombre del campo que tiene el error
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String message, String property) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode(property)
            .addConstraintViolation();
    }
}
