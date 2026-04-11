package cl.previred.desafio.repository;

import cl.previred.desafio.exception.RepositoryException;
import cl.previred.desafio.model.Empleado;
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

    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoRepository.class);

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
        LOG.debug("Ejecutando query: SELECT todos los empleados");
        try {
            return jdbcTemplate.query("SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados", ROW_MAPPER);
        } catch (Exception e) {
            LOG.error("Error al obtener todos los empleados", e);
            throw new RepositoryException("Error al obtener empleados", e);
        }
    }

    public Empleado findById(Long id) {
        LOG.debug("Ejecutando query: SELECT empleado por id={}", id);
        try {
            List<Empleado> results = jdbcTemplate.query(
                    "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados WHERE id = ?",
                    ROW_MAPPER,
                    id
            );
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            LOG.error("Error al obtener empleado por id={}", id, e);
            throw new RepositoryException("Error al obtener empleado", e);
        }
    }

    public boolean existsByRut(String rut) {
        LOG.debug("Ejecutando query: COUNT empleado por rut={}", rut);
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM empleados WHERE rut = ?",
                    Integer.class,
                    rut
            );
            return count != null && count > 0;
        } catch (Exception e) {
            LOG.error("Error al verificar existencia de rut={}", rut, e);
            throw new RepositoryException("Error al verificar rut", e);
        }
    }

    public Empleado save(Empleado empleado) {
        try {
            if (empleado.getId() == null) {
                LOG.debug("Ejecutando INSERT para empleado con rut={}", empleado.getRut());
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
                LOG.debug("Ejecutando UPDATE para empleado con id={}", empleado.getId());
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
        } catch (Exception e) {
            LOG.error("Error al guardar empleado con rut={}", empleado.getRut(), e);
            throw new RepositoryException("Error al guardar empleado", e);
        }
    }

    public boolean deleteById(Long id) {
        LOG.debug("Ejecutando DELETE para empleado con id={}", id);
        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM empleados WHERE id = ?", id);
            return rowsAffected > 0;
        } catch (Exception e) {
            LOG.error("Error al eliminar empleado con id={}", id, e);
            throw new RepositoryException("Error al eliminar empleado", e);
        }
    }
}