package com.previred.empleados.dao;

import com.previred.empleados.dao.impl.EmpleadoDAOImpl;
import com.previred.empleados.model.Empleado;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class EmpleadoDAOTest {

    private EmpleadoDAO empleadoDAO;
    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

        createSchema();

        empleadoDAO = new EmpleadoDAOImpl(dataSource);
    }

    private void createSchema() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS empleados");
            stmt.execute("CREATE TABLE empleados (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "apellido VARCHAR(100) NOT NULL, " +
                    "rut VARCHAR(12) UNIQUE NOT NULL, " +
                    "cargo VARCHAR(100) NOT NULL, " +
                    "salario_base DECIMAL(12,2) NOT NULL, " +
                    "bonos DECIMAL(12,2) DEFAULT 0, " +
                    "descuentos DECIMAL(12,2) DEFAULT 0, " +
                    "fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        }
    }

    @Test
    public void testSaveEmpleado() {
        Empleado empleado = createEmpleado("Juan", "Pérez", "12.345.678-5");

        Empleado saved = empleadoDAO.save(empleado);

        assertNotNull(saved.getId());
        assertEquals("Juan", saved.getNombre());
        assertEquals("12.345.678-5", saved.getRut());
    }

    @Test
    public void testFindAll() {
        empleadoDAO.save(createEmpleado("Juan", "Pérez", "12.345.678-5"));
        empleadoDAO.save(createEmpleado("María", "González", "23.456.789-6"));

        List<Empleado> empleados = empleadoDAO.findAll();

        assertEquals(2, empleados.size());
    }

    @Test
    public void testFindById() {
        Empleado saved = empleadoDAO.save(createEmpleado("Juan", "Pérez", "12.345.678-5"));

        Optional<Empleado> found = empleadoDAO.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Juan", found.get().getNombre());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    public void testFindByIdNoExiste() {
        Optional<Empleado> found = empleadoDAO.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    public void testExistsByRut() {
        empleadoDAO.save(createEmpleado("Juan", "Pérez", "12.345.678-5"));

        assertTrue(empleadoDAO.existsByRut("12.345.678-5"));
        assertFalse(empleadoDAO.existsByRut("99.999.999-9"));
    }

    @Test
    public void testDeleteById() {
        Empleado saved = empleadoDAO.save(createEmpleado("Juan", "Pérez", "12.345.678-5"));

        empleadoDAO.deleteById(saved.getId());

        Optional<Empleado> found = empleadoDAO.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    public void testSaveConBonosYDescuentos() {
        Empleado empleado = new Empleado("Juan", "Pérez", "12.345.678-5", "Desarrollador",
                new BigDecimal("1000000"), new BigDecimal("200000"), new BigDecimal("50000"));

        Empleado saved = empleadoDAO.save(empleado);

        Optional<Empleado> found = empleadoDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(new BigDecimal("1000000.00"), found.get().getSalarioBase());
        assertEquals(new BigDecimal("200000.00"), found.get().getBonos());
        assertEquals(new BigDecimal("50000.00"), found.get().getDescuentos());
    }

    @Test
    public void testCalcularSalarioTotal() {
        Empleado empleado = new Empleado("Juan", "Pérez", "12.345.678-5", "Desarrollador",
                new BigDecimal("1000000"), new BigDecimal("200000"), new BigDecimal("50000"));

        BigDecimal salarioTotal = empleado.calcularSalarioTotal();

        assertEquals(new BigDecimal("1150000"), salarioTotal);
    }

    private Empleado createEmpleado(String nombre, String apellido, String rut) {
        return new Empleado(nombre, apellido, rut, "Desarrollador",
                new BigDecimal("1000000"), new BigDecimal("200000"), new BigDecimal("50000"));
    }
}
