package com.desafio.legacy.repository;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.desafio.legacy.model.Empleado;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JdbcEmpleadoRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM empleados");
    }

    @Test
    void shouldSaveAndFindAllEmployees() {
        Empleado empleado = buildEmpleado("11111111-1", "Dev");

        Empleado saved = empleadoRepository.save(empleado);
        List<Empleado> empleados = empleadoRepository.findAll();

        assertNotNull(saved.getId());
        assertEquals(1, empleados.size());
        assertEquals("11111111-1", empleados.get(0).getRutDni());
        assertEquals(0, new BigDecimal("500000").compareTo(empleados.get(0).getSalarioBase()));
    }

    @Test
    void shouldReturnTrueWhenRutDniAlreadyExists() {
        empleadoRepository.save(buildEmpleado("22222222-2", "QA"));

        boolean exists = empleadoRepository.existsByRutDni("22222222-2");
        boolean notExists = empleadoRepository.existsByRutDni("99999999-9");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldDeleteByIdWhenEmployeeExists() {
        Empleado saved = empleadoRepository.save(buildEmpleado("33333333-3", "Analista"));

        boolean deleted = empleadoRepository.deleteById(saved.getId());
        List<Empleado> empleados = empleadoRepository.findAll();

        assertTrue(deleted);
        assertEquals(0, empleados.size());
    }

    @Test
    void shouldReturnFalseWhenDeletingUnknownEmployee() {
        boolean deleted = empleadoRepository.deleteById(9999L);

        assertFalse(deleted);
    }

    private Empleado buildEmpleado(String rutDni, String cargo) {
        return new Empleado(
            null,
            "Ana",
            "Perez",
            rutDni,
            cargo,
            new BigDecimal("500000"),
            new BigDecimal("50000"),
            new BigDecimal("20000")
        );
    }
}
