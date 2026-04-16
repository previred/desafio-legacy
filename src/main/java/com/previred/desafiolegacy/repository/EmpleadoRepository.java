package com.previred.desafiolegacy.repository;

import com.previred.desafiolegacy.model.Empleado;
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

@Repository
public class EmpleadoRepository {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoRepository.class);

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
            rs.getBigDecimal("salario_base"),
            rs.getBigDecimal("bono"),
            rs.getBigDecimal("descuentos")
    );

    public List<Empleado> findAll() {
        logger.info("Buscando todos los empleados");
        String sql = "SELECT * FROM empleados";
        return jdbcTemplate.query(sql, empleadoRowMapper);
    }

    public boolean existsByRut(String rut) {
        logger.info("Verificando existencia de RUT");
        String sql = "SELECT COUNT(*) FROM empleados WHERE rut = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rut);
        return count != null && count > 0;
    }

    public Empleado save(Empleado empleado) {
        logger.info("Guardando nuevo empleado");
        String sql = "INSERT INTO empleados (nombre, apellido, rut, cargo, salario_base, bono, descuentos) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalarioBase());
            ps.setBigDecimal(6, empleado.getBono());
            ps.setBigDecimal(7, empleado.getDescuentos());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("No se pudo obtener el ID generado por la base de datos");
        }
        empleado.setId(key.longValue());
        return empleado;
    }

    public boolean deleteById(Long id) {
        logger.info("Eliminando empleado con id: {}", id);
        String sql = "DELETE FROM empleados WHERE id = ?";
        int filasAfectadas = jdbcTemplate.update(sql, id);
        return filasAfectadas > 0;
    }
}
