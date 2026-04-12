package com.previred.empleados.mapper;

import com.previred.empleados.entity.EmpleadoEntity;
import com.previred.empleados.model.EmpleadoModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmpleadoMapperTest {

    @Test
    void toModel_deberiaMapearTodosLosCampos() {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(1L);
        entity.setNombre("Juan");
        entity.setApellido("Pérez");
        entity.setRut("12345678-5");
        entity.setCargo("Desarrollador");
        entity.setSalarioBase(500000.0);
        entity.setBonos(100000.0);
        entity.setDescuentos(50000.0);

        EmpleadoModel model = EmpleadoMapper.toModel(entity);

        assertEquals(1L, model.getId());
        assertEquals("Juan", model.getNombre());
        assertEquals("Pérez", model.getApellido());
        assertEquals("12345678-5", model.getRut());
        assertEquals("Desarrollador", model.getCargo());
        assertEquals(500000.0, model.getSalarioBase());
        assertEquals(100000.0, model.getBonos());
        assertEquals(50000.0, model.getDescuentos());
    }

    @Test
    void toEntity_deberiaMapearTodosLosCampos() {
        EmpleadoModel model = new EmpleadoModel();
        model.setId(2L);
        model.setNombre("María");
        model.setApellido("López");
        model.setRut("87654321-K");
        model.setCargo("Analista");
        model.setSalarioBase(600000.0);
        model.setBonos(200000.0);
        model.setDescuentos(100000.0);

        EmpleadoEntity entity = EmpleadoMapper.toEntity(model);

        assertEquals(2L, entity.getId());
        assertEquals("María", entity.getNombre());
        assertEquals("López", entity.getApellido());
        assertEquals("87654321-K", entity.getRut());
        assertEquals("Analista", entity.getCargo());
        assertEquals(600000.0, entity.getSalarioBase());
        assertEquals(200000.0, entity.getBonos());
        assertEquals(100000.0, entity.getDescuentos());
    }

    @Test
    void toModel_conCamposNulos_deberiaMapearNulos() {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(1L);
        entity.setNombre("Test");

        EmpleadoModel model = EmpleadoMapper.toModel(entity);

        assertEquals(1L, model.getId());
        assertEquals("Test", model.getNombre());
        assertNull(model.getApellido());
        assertNull(model.getRut());
    }
}
