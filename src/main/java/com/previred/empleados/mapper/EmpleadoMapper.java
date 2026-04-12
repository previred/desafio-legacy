package com.previred.empleados.mapper;

import com.previred.empleados.entity.EmpleadoEntity;
import com.previred.empleados.model.EmpleadoModel;

public class EmpleadoMapper {

    private EmpleadoMapper() {
    }

    public static EmpleadoModel toModel(EmpleadoEntity entity) {
        EmpleadoModel model = new EmpleadoModel();
        model.setId(entity.getId());
        model.setNombre(entity.getNombre());
        model.setApellido(entity.getApellido());
        model.setRut(entity.getRut());
        model.setCargo(entity.getCargo());
        model.setSalarioBase(entity.getSalarioBase());
        model.setBonos(entity.getBonos());
        model.setDescuentos(entity.getDescuentos());
        return model;
    }

    public static EmpleadoEntity toEntity(EmpleadoModel model) {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(model.getId());
        entity.setNombre(model.getNombre());
        entity.setApellido(model.getApellido());
        entity.setRut(model.getRut());
        entity.setCargo(model.getCargo());
        entity.setSalarioBase(model.getSalarioBase());
        entity.setBonos(model.getBonos());
        entity.setDescuentos(model.getDescuentos());
        return entity;
    }
}
