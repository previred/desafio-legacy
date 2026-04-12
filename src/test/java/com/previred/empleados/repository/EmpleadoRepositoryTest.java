package com.previred.empleados.repository;

import com.previred.empleados.entity.EmpleadoEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql(statements = {
    "CREATE TABLE IF NOT EXISTS empleados (" +
    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
    "nombre VARCHAR(100) NOT NULL, " +
    "apellido VARCHAR(100) NOT NULL, " +
    "rut VARCHAR(12) NOT NULL UNIQUE, " +
    "cargo VARCHAR(100) NOT NULL, " +
    "salario_base DOUBLE NOT NULL, " +
    "bonos DOUBLE DEFAULT 0, " +
    "descuentos DOUBLE DEFAULT 0)"
})
class EmpleadoRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private EmpleadoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new EmpleadoRepository(jdbcTemplate);
        jdbcTemplate.update("DELETE FROM empleados");
    }

    private EmpleadoEntity crearEntityBase() {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setNombre("Juan");
        entity.setApellido("Pérez");
        entity.setRut("12345678-5");
        entity.setCargo("Desarrollador");
        entity.setSalarioBase(500000.0);
        entity.setBonos(100000.0);
        entity.setDescuentos(50000.0);
        return entity;
    }

    @Test
    void save_deberiaGuardarYRetornarConId() {
        EmpleadoEntity entity = crearEntityBase();

        EmpleadoEntity saved = repository.save(entity);

        assertNotNull(saved.getId());
        assertEquals("Juan", saved.getNombre());
    }

    @Test
    void findAll_deberiaRetornarTodos() {
        repository.save(crearEntityBase());
        EmpleadoEntity otro = crearEntityBase();
        otro.setRut("87654321-K");
        otro.setNombre("María");
        repository.save(otro);

        List<EmpleadoEntity> resultado = repository.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    void findAll_sinDatos_deberiaRetornarVacio() {
        List<EmpleadoEntity> resultado = repository.findAll();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void findById_existente_deberiaRetornar() {
        EmpleadoEntity saved = repository.save(crearEntityBase());

        Optional<EmpleadoEntity> resultado = repository.findById(saved.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    void findById_noExistente_deberiaRetornarVacio() {
        Optional<EmpleadoEntity> resultado = repository.findById(999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void existsByRut_existente_deberiaRetornarTrue() {
        repository.save(crearEntityBase());

        assertTrue(repository.existsByRut("12345678-5"));
    }

    @Test
    void existsByRut_noExistente_deberiaRetornarFalse() {
        assertFalse(repository.existsByRut("99999999-9"));
    }

    @Test
    void deleteById_existente_deberiaRetornarTrue() {
        EmpleadoEntity saved = repository.save(crearEntityBase());

        assertTrue(repository.deleteById(saved.getId()));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void deleteById_noExistente_deberiaRetornarFalse() {
        assertFalse(repository.deleteById(999L));
    }

    @Test
    void save_conBonosNulos_deberiaGuardarConCero() {
        EmpleadoEntity entity = crearEntityBase();
        entity.setBonos(null);
        entity.setDescuentos(null);

        EmpleadoEntity saved = repository.save(entity);
        Optional<EmpleadoEntity> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(0.0, found.get().getBonos());
        assertEquals(0.0, found.get().getDescuentos());
    }
}
