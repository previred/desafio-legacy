package com.empleados.model;

import com.empleados.validation.EmpleadoValido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Modelo de dominio que representa un empleado.
 * <p>
 * Contiene validaciones de Bean Validation a nivel de campo
 * y una validacion personalizada {@link EmpleadoValido} a nivel de clase
 * para reglas de negocio que involucran multiples campos.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EmpleadoValido
public class Empleado {

    /** Identificador unico autogenerado por la base de datos. */
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    /** RUT/DNI chileno en formato XX.XXX.XXX-X. */
    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$",
             message = "Formato RUT inválido. Use XX.XXX.XXX-X")
    private String rut;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    /** Salario base en pesos. Minimo $400,000. */
    @Min(value = 400000, message = "El salario base no puede ser menor a $400,000")
    private Long salario;

    /** Bono adicional. No puede superar el 50% del salario base. */
    @Min(value = 0, message = "El bono no puede ser negativo")
    private Long bono;

    /** Descuentos aplicados. No puede ser mayor al salario base. */
    @Min(value = 0, message = "Los descuentos no pueden ser negativos")
    private Long descuentos;
}
