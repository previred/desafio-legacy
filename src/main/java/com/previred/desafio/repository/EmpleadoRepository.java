package com.previred.desafio.repository;

import com.previred.desafio.model.Empleado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoRepository {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public EmpleadoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Empleado> empleadoRowMapper = (rs, rowNum) -> new Empleado(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("rut"),
            rs.getString("cargo"),
            rs.getBigDecimal("salario"),
            rs.getBigDecimal("bono"),
            rs.getBigDecimal("descuento")
    );

    public List<Empleado> findAll() {
        log.debug("Consultando todos los empleados");
        return jdbcTemplate.query("SELECT * FROM empleados ORDER BY id", empleadoRowMapper);
    }

    public Optional<Empleado> findById(Long id) {
        log.debug("Buscando empleado con id: {}", id);
        List<Empleado> results = jdbcTemplate.query(
                "SELECT * FROM empleados WHERE id = ?",
                empleadoRowMapper,
                id
        );
        return results.stream().findFirst();
    }

    public boolean existsByRut(String rut) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM empleados WHERE rut = ?",
                Integer.class,
                rut
        );
        return count != null && count > 0;
    }

    public Empleado save(Empleado empleado) {
        log.debug("Guardando nuevo empleado: {}", empleado.getRut());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO empleados (nombre, apellido, rut, cargo, salario, bono, descuento) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalario());
            ps.setBigDecimal(6, empleado.getBono());
            ps.setBigDecimal(7, empleado.getDescuento());
            return ps;
        }, keyHolder);

        empleado.setId(keyHolder.getKey().longValue());
        return empleado;
    }

    public boolean deleteById(Long id) {
        log.debug("Eliminando empleado con id: {}", id);
        int rows = jdbcTemplate.update("DELETE FROM empleados WHERE id = ?", id);
        return rows > 0;
    }
}
