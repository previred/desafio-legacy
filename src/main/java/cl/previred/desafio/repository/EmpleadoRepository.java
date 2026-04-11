package cl.previred.desafio.repository;

import cl.previred.desafio.model.Empleado;
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

    private final JdbcTemplate jdbcTemplate;

    public EmpleadoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Empleado> ROW_MAPPER = (rs, rowNum) -> {
        Empleado emp = new Empleado();
        emp.setId(rs.getLong("id"));
        emp.setNombre(rs.getString("nombre"));
        emp.setApellido(rs.getString("apellido"));
        emp.setRut(rs.getString("rut"));
        emp.setCargo(rs.getString("cargo"));
        emp.setSalario(rs.getDouble("salario"));
        emp.setBono(rs.getDouble("bono"));
        emp.setDescuentos(rs.getDouble("descuentos"));
        return emp;
    };

    public List<Empleado> findAll() {
        return jdbcTemplate.query("SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados", ROW_MAPPER);
    }

    public Empleado findById(Long id) {
        List<Empleado> results = jdbcTemplate.query(
                "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados WHERE id = ?",
                ROW_MAPPER,
                id
        );
        return results.isEmpty() ? null : results.get(0);
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
        if (empleado.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO empleados (nombre, apellido, rut, cargo, salario, bono, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, empleado.getNombre());
                ps.setString(2, empleado.getApellido());
                ps.setString(3, empleado.getRut());
                ps.setString(4, empleado.getCargo());
                ps.setDouble(5, empleado.getSalario());
                ps.setDouble(6, empleado.getBono() != null ? empleado.getBono() : 0.0);
                ps.setDouble(7, empleado.getDescuentos() != null ? empleado.getDescuentos() : 0.0);
                return ps;
            }, keyHolder);
            Number key = keyHolder.getKey();
            if (key != null) {
                empleado.setId(key.longValue());
            }
        } else {
            jdbcTemplate.update(
                    "UPDATE empleados SET nombre = ?, apellido = ?, rut = ?, cargo = ?, salario = ?, bono = ?, descuentos = ? WHERE id = ?",
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getRut(),
                    empleado.getCargo(),
                    empleado.getSalario(),
                    empleado.getBono(),
                    empleado.getDescuentos(),
                    empleado.getId()
            );
        }
        return empleado;
    }

    public boolean deleteById(Long id) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM empleados WHERE id = ?", id);
        return rowsAffected > 0;
    }
}