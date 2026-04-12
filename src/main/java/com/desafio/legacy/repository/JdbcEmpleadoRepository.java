package com.desafio.legacy.repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.desafio.legacy.model.Empleado;

@Repository
public class JdbcEmpleadoRepository implements EmpleadoRepository {

    private static final RowMapper<Empleado> EMPLEADO_ROW_MAPPER = (rs, rowNum) ->
        new Empleado(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("rut_dni"),
            rs.getString("cargo"),
            rs.getBigDecimal("salario_base"),
            rs.getBigDecimal("bono"),
            rs.getBigDecimal("descuentos")
        );

    private final JdbcTemplate jdbcTemplate;

    public JdbcEmpleadoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Empleado> findAll() {
        return findByFilters(null, null);
    }

    @Override
    public List<Empleado> findByFilters(String term, String cargo) {
        String sql = "SELECT id, nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos "
            + "FROM empleados WHERE 1=1";

        List<Object> params = new ArrayList<Object>();

        String normalizedTerm = term == null ? "" : term.trim();
        if (!normalizedTerm.isEmpty()) {
            sql = sql + " AND (LOWER(nombre) LIKE ? OR LOWER(apellido) LIKE ? OR LOWER(rut_dni) LIKE ? OR LOWER(cargo) LIKE ?)";
            String likeTerm = "%" + normalizedTerm.toLowerCase() + "%";
            params.add(likeTerm);
            params.add(likeTerm);
            params.add(likeTerm);
            params.add(likeTerm);
        }

        String normalizedCargo = cargo == null ? "" : cargo.trim();
        if (!normalizedCargo.isEmpty()) {
            sql = sql + " AND LOWER(cargo) LIKE ?";
            params.add("%" + normalizedCargo.toLowerCase() + "%");
        }

        sql = sql + " ORDER BY id ASC";

        return jdbcTemplate.query(sql, EMPLEADO_ROW_MAPPER, params.toArray());
    }

    @Override
    public Empleado save(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, empleado.getNombre());
            statement.setString(2, empleado.getApellido());
            statement.setString(3, empleado.getRutDni());
            statement.setString(4, empleado.getCargo());
            statement.setBigDecimal(5, empleado.getSalarioBase());
            statement.setBigDecimal(6, empleado.getBono());
            statement.setBigDecimal(7, empleado.getDescuentos());
            return statement;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();
        if (generatedId == null) {
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.get("ID") instanceof Number) {
                generatedId = (Number) keys.get("ID");
            } else if (keys != null && keys.get("id") instanceof Number) {
                generatedId = (Number) keys.get("id");
            }
        }
        if (generatedId != null) {
            empleado.setId(generatedId.longValue());
        }

        return empleado;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM empleados WHERE id = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    @Override
    public boolean existsByRutDni(String rutDni) {
        String sql = "SELECT COUNT(1) FROM empleados WHERE rut_dni = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rutDni);
        return count != null && count > 0;
    }
}
