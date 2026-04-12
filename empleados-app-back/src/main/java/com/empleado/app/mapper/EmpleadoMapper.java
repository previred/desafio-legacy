package com.empleado.app.mapper;

import com.empleado.app.entity.EmpleadoEntity;
import com.empleado.app.model.EmpleadoRequest;
import com.empleado.app.model.EmpleadoResponse;

/**
 * Mapper encargado de convertir entre entidades y objetos DTO.
 * Separa la lógica de persistencia (Entity) de la lógica de exposición (Response).
 * Autor: Cristian Palacios
 */
public class EmpleadoMapper {

    /**
     * Convierte un objeto EmpleadoRequest a EmpleadoEntity.
     * Se utiliza al momento de crear o guardar un empleado en la base de datos.
     */
    public static EmpleadoEntity toEntity(EmpleadoRequest req) {

        return EmpleadoEntity.builder()
                .nombre(req.getNombre())
                .apellido(req.getApellido())
                .dni(req.getDni())
                .cargo(req.getCargo())
                .salario(req.getSalario())
                .bono(req.getBono())
                .descuentos(req.getDescuentos())
                .build();
    }

    /**
     * Convierte una entidad EmpleadoEntity a EmpleadoResponse.
     * Calcula el salario neto a partir de salario base, bono y descuentos.
     * Se utiliza para exponer datos al cliente.
     */
    public static EmpleadoResponse toResponse(EmpleadoEntity e) {

        double salarioBase = e.getSalario() != null ? e.getSalario() : 0;
        double bono = e.getBono() != null ? e.getBono() : 0;
        double descuentos = e.getDescuentos() != null ? e.getDescuentos() : 0;

        double salarioNeto = salarioBase + bono - descuentos;

        return EmpleadoResponse.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .dni(e.getDni())
                .cargo(e.getCargo())
                .salarioBase(salarioBase)
                .bono(bono)
                .descuentos(descuentos)
                .salarioNeto(salarioNeto)
                .build();
    }
}