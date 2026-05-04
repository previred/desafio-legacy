package com.mindgrid.empleados.adapters.inbound.web.mapper;

import com.mindgrid.empleados.adapters.inbound.web.request.EmpleadoRequest;
import com.mindgrid.empleados.domain.model.Empleado;

import java.util.LinkedHashMap;
import java.util.Map;

public class EmpleadoWebMapper {

    private EmpleadoWebMapper() {}

    public static Empleado toDomain(EmpleadoRequest request) {
        return Empleado.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .rut(request.getRut())
            .cargo(request.getCargo())
            .salarioBase(request.getSalarioBase())
            .bono(request.getBono())
            .descuentos(request.getDescuentos())
            .build();
    }

    public static Map<String, Object> toResponse(Empleado empleado) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", empleado.getId());
        map.put("nombre", empleado.getNombre());
        map.put("apellido", empleado.getApellido());
        map.put("rut", empleado.getRut());
        map.put("cargo", empleado.getCargo());
        map.put("salarioBase", empleado.getSalarioBase());
        map.put("bono", empleado.getBono());
        map.put("descuentos", empleado.getDescuentos());
        map.put("salarioNeto", empleado.getSalarioNeto());
        return map;
    }
}
