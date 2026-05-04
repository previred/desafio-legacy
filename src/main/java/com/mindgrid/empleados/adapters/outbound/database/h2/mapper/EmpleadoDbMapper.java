package com.mindgrid.empleados.adapters.outbound.database.h2.mapper;

import com.mindgrid.empleados.adapters.outbound.database.entity.EmpleadoEntity;
import com.mindgrid.empleados.domain.model.Empleado;

public class EmpleadoDbMapper {

    private EmpleadoDbMapper() {}

    public static EmpleadoEntity toEntity(Empleado empleado) {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(empleado.getId());
        entity.setNombre(empleado.getNombre());
        entity.setApellido(empleado.getApellido());
        entity.setRut(empleado.getRut());
        entity.setCargo(empleado.getCargo());
        entity.setSalarioBase(empleado.getSalarioBase());
        entity.setBono(empleado.getBono());
        entity.setDescuentos(empleado.getDescuentos());
        return entity;
    }

    public static Empleado toDomain(EmpleadoEntity entity) {
        return Empleado.builder()
            .id(entity.getId())
            .nombre(entity.getNombre())
            .apellido(entity.getApellido())
            .rut(entity.getRut())
            .cargo(entity.getCargo())
            .salarioBase(entity.getSalarioBase())
            .bono(entity.getBono())
            .descuentos(entity.getDescuentos())
            .build();
    }
}
