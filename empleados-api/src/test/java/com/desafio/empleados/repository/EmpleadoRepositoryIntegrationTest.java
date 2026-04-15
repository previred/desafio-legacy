package com.desafio.empleados.repository;

import com.desafio.empleados.EmpleadosApplication;
import com.desafio.empleados.model.Empleado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = EmpleadosApplication.class)
@DisplayName("EmpleadoRepository + H2")
class EmpleadoRepositoryIntegrationTest {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** Limpia filas de prueba para que los tests sean idempotentes incluso con contexto compartido. */
    @AfterEach
    void limpiarDatosDePrueba() {
        jdbcTemplate.update("DELETE FROM empleado WHERE rut_dni = ?", "12.345.678-5");
    }

    @Test
    void findByIdSinFilas() throws Exception {
        Optional<Empleado> r = empleadoRepository.findById(999_999L);
        assertThat(r).isEmpty();
    }

    @Test
    void insertYFindById() throws Exception {
        Empleado e = new Empleado();
        e.setNombre("Repo");
        e.setApellido("Test");
        e.setRutDni("12.345.678-5");
        e.setCargo("Dev");
        e.setSalarioBase(new BigDecimal("500000"));
        e.setBono(BigDecimal.ZERO);
        e.setDescuentos(BigDecimal.ZERO);

        Empleado persistido = empleadoRepository.insert(e);
        assertThat(persistido.getId()).isNotNull();
        // El argumento original no debe ser mutado
        assertThat(e.getId()).isNull();

        Optional<Empleado> found = empleadoRepository.findById(persistido.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getRutDni()).isEqualTo("12.345.678-5");
    }
}
