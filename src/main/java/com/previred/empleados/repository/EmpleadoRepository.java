package com.previred.empleados.repository;

import com.previred.empleados.entity.EmpleadoEntity;
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
public class EmpleadoRepository implements IEmpleadoRepository {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoRepository.class);

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<EmpleadoEntity> rowMapper = (rs, rowNum) -> {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(rs.getLong("id"));
        entity.setNombre(rs.getString("nombre"));
        entity.setApellido(rs.getString("apellido"));
        entity.setRut(rs.getString("rut"));
        entity.setCargo(rs.getString("cargo"));
        entity.setSalarioBase(rs.getDouble("salario_base"));
        entity.setBonos(rs.getDouble("bonos"));
        entity.setDescuentos(rs.getDouble("descuentos"));
        return entity;
    };

    public EmpleadoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EmpleadoEntity> findAll() {
        log.debug("Consultando todos los empleados");
        return jdbcTemplate.query("SELECT * FROM empleados", rowMapper);
    }

    public Optional<EmpleadoEntity> findById(Long id) {
        log.debug("Consultando empleado con ID: {}", id);
        List<EmpleadoEntity> results = jdbcTemplate.query(
                "SELECT * FROM empleados WHERE id = ?", rowMapper, id);
        return results.stream().findFirst();
    }

    public boolean existsByRut(String rut) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM empleados WHERE rut = ?", Integer.class, rut);
        return count != null && count > 0;
    }

    public EmpleadoEntity save(EmpleadoEntity entity) {
        log.debug("Guardando empleado: {} {}", entity.getNombre(), entity.getApellido());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO empleados (nombre, apellido, rut, cargo, salario_base, bonos, descuentos) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getApellido());
            ps.setString(3, entity.getRut());
            ps.setString(4, entity.getCargo());
            ps.setDouble(5, entity.getSalarioBase());
            ps.setDouble(6, entity.getBonos() != null ? entity.getBonos() : 0.0);
            ps.setDouble(7, entity.getDescuentos() != null ? entity.getDescuentos() : 0.0);
            return ps;
        }, keyHolder);
        entity.setId(keyHolder.getKey().longValue());
        return entity;
    }

    public boolean deleteById(Long id) {
        log.debug("Eliminando empleado con ID: {}", id);
        int rows = jdbcTemplate.update("DELETE FROM empleados WHERE id = ?", id);
        return rows > 0;
    }
}
