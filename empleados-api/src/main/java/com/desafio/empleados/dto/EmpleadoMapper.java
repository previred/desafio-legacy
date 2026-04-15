package com.desafio.empleados.dto;

import com.desafio.empleados.model.Empleado;

/**
 * Traduce el DTO de alta (contrato HTTP) al modelo persistible.
 */
public final class EmpleadoMapper {

    private EmpleadoMapper() {
    }

    public static Empleado toNuevaEntidad(EmpleadoAltaRequest req) {
        Empleado e = new Empleado();
        e.setNombre(req.getNombre());
        e.setApellido(req.getApellido());
        e.setRutDni(req.getRutDni());
        e.setCargo(req.getCargo());
        e.setSalarioBase(req.getSalarioBase());
        e.setBono(req.getBono());
        e.setDescuentos(req.getDescuentos());
        return e;
    }

    public static EmpleadoResponse toResponse(Empleado e) {
        EmpleadoResponse r = new EmpleadoResponse();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setApellido(e.getApellido());
        r.setRutDni(e.getRutDni());
        r.setCargo(e.getCargo());
        r.setSalarioBase(e.getSalarioBase());
        r.setBono(e.getBono());
        r.setDescuentos(e.getDescuentos());
        return r;
    }
}
