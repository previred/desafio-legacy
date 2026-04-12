package com.empleados.mapper;

import com.empleados.api.generated.model.EmpleadoResponse;
import com.empleados.model.Empleado;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Componente responsable del mapeo entre la entidad {@link Empleado}
 * y el DTO generado {@link EmpleadoResponse}.
 * <p>
 * Ademas de copiar campos, calcula el salario liquido
 * (salario + bono - descuentos) para incluirlo en la respuesta.
 * </p>
 */
@Component
public class EmpleadoMapper {

    /**
     * Convierte una entidad {@link Empleado} a su representacion DTO.
     *
     * @param empleado entidad a convertir (puede ser {@code null})
     * @return DTO con los datos del empleado y salario liquido calculado,
     *         o {@code null} si la entrada es {@code null}
     */
    public EmpleadoResponse toResponse(Empleado empleado) {
        if (empleado == null) return null;
        return new EmpleadoResponse()
            .id(empleado.getId())
            .nombre(empleado.getNombre())
            .apellido(empleado.getApellido())
            .rut(empleado.getRut())
            .cargo(empleado.getCargo())
            .salario(empleado.getSalario())
            .bono(empleado.getBono())
            .descuentos(empleado.getDescuentos())
            .salarioLiquido(calcularLiquido(empleado));
    }

    /**
     * Convierte una lista de entidades a su representacion DTO.
     *
     * @param empleados lista de entidades a convertir
     * @return lista de DTOs con salario liquido calculado
     */
    public List<EmpleadoResponse> toResponseList(List<Empleado> empleados) {
        return empleados.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * Calcula el salario liquido: {@code salario + bono - descuentos}.
     *
     * @param e empleado con los valores monetarios
     * @return salario liquido en pesos
     */
    private Long calcularLiquido(Empleado e) {
        long bono = e.getBono() != null ? e.getBono() : 0;
        long desc = e.getDescuentos() != null ? e.getDescuentos() : 0;
        return e.getSalario() + bono - desc;
    }
}
