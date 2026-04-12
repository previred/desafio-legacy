package com.empleados.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Anotacion de validacion personalizada a nivel de clase para {@link com.empleados.model.Empleado}.
 * <p>
 * Ejecuta reglas de negocio que involucran multiples campos:
 * <ul>
 *   <li>El bono no puede superar el 50% del salario base</li>
 *   <li>Los descuentos no pueden ser mayores al salario base</li>
 * </ul>
 * Delegada al validador {@link EmpleadoValidator}.
 * </p>
 *
 * @see EmpleadoValidator
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmpleadoValidator.class)
@Documented
public @interface EmpleadoValido {
    String message() default "Empleado inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
