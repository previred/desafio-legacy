package cl.previred.desafio.repository;

import cl.previred.desafio.exception.RepositoryException;
import cl.previred.desafio.model.Empleado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmpleadoRepository {

    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoRepository.class);

    private final DataSource dataSource;

    public EmpleadoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Empleado> findAll() {
        LOG.debug("Ejecutando query: SELECT todos los empleados");
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados";
        List<Empleado> empleados = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                empleados.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOG.error("Error al obtener todos los empleados", e);
            throw new RepositoryException("Error al obtener empleados", e);
        }
        return empleados;
    }

    public Empleado findById(Long id) {
        LOG.debug("Ejecutando query: SELECT empleado por id={}", id);
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOG.error("Error al obtener empleado por id={}", id, e);
            throw new RepositoryException("Error al obtener empleado", e);
        }
        return null;
    }

    public boolean existsByRut(String rut) {
        LOG.debug("Ejecutando query: COUNT empleado por rut={}", rut);
        String sql = "SELECT COUNT(*) FROM empleados WHERE rut = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOG.error("Error al verificar existencia de rut={}", rut, e);
            throw new RepositoryException("Error al verificar rut", e);
        }
        return false;
    }

    public Empleado save(Empleado empleado) {
        try (Connection conn = dataSource.getConnection()) {
            if (empleado.getId() == null) {
                LOG.debug("Ejecutando INSERT para empleado con rut={}", empleado.getRut());
                return insertEmpleado(conn, empleado);
            } else {
                LOG.debug("Ejecutando UPDATE para empleado con id={}", empleado.getId());
                return updateEmpleado(conn, empleado);
            }
        } catch (SQLException e) {
            LOG.error("Error al guardar empleado con rut={}", empleado.getRut(), e);
            throw new RepositoryException("Error al guardar empleado", e);
        }
    }

    private Empleado insertEmpleado(Connection conn, Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleados (nombre, apellido, rut, cargo, salario, bono, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalario());
            ps.setBigDecimal(6, empleado.getBono() != null ? empleado.getBono() : BigDecimal.ZERO);
            ps.setBigDecimal(7, empleado.getDescuentos() != null ? empleado.getDescuentos() : BigDecimal.ZERO);
            ps.executeUpdate();
            conn.commit();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    empleado.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
        return empleado;
    }

    private Empleado updateEmpleado(Connection conn, Empleado empleado) throws SQLException {
        String sql = "UPDATE empleados SET nombre = ?, apellido = ?, rut = ?, cargo = ?, salario = ?, bono = ?, descuentos = ? WHERE id = ?";
        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalario());
            ps.setBigDecimal(6, empleado.getBono());
            ps.setBigDecimal(7, empleado.getDescuentos());
            ps.setLong(8, empleado.getId());
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
        return empleado;
    }

    public boolean deleteById(Long id) {
        LOG.debug("Ejecutando DELETE para empleado con id={}", id);
        String sql = "DELETE FROM empleados WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.error("Error al eliminar empleado con id={}", id, e);
            throw new RepositoryException("Error al eliminar empleado", e);
        }
    }

    private Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado();
        emp.setId(rs.getLong("id"));
        emp.setNombre(rs.getString("nombre"));
        emp.setApellido(rs.getString("apellido"));
        emp.setRut(rs.getString("rut"));
        emp.setCargo(rs.getString("cargo"));
        emp.setSalario(rs.getBigDecimal("salario"));
        emp.setBono(rs.getBigDecimal("bono"));
        emp.setDescuentos(rs.getBigDecimal("descuentos"));
        return emp;
    }
}