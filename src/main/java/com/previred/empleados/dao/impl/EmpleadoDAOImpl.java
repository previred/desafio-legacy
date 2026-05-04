package com.previred.empleados.dao.impl;

import com.previred.empleados.dao.EmpleadoDAO;
import com.previred.empleados.model.Empleado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoDAOImpl implements EmpleadoDAO {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoDAOImpl.class);

    private final DataSource dataSource;

    public EmpleadoDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Empleado> findAll() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario_base, bonos, descuentos, fecha_creacion FROM empleados ORDER BY id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                empleados.add(mapResultSetToEmpleado(rs));
            }

            logger.debug("Se encontraron {} empleados", empleados.size());

        } catch (SQLException e) {
            logger.error("Error al obtener empleados", e);
            throw new RuntimeException("Error al obtener empleados", e);
        }

        return empleados;
    }

    @Override
    public Optional<Empleado> findById(Long id) {
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario_base, bonos, descuentos, fecha_creacion FROM empleados WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Empleado empleado = mapResultSetToEmpleado(rs);
                    logger.debug("Empleado encontrado: {}", id);
                    return Optional.of(empleado);
                }
            }

        } catch (SQLException e) {
            logger.error("Error al buscar empleado por ID: {}", id, e);
            throw new RuntimeException("Error al buscar empleado", e);
        }

        logger.debug("Empleado no encontrado: {}", id);
        return Optional.empty();
    }

    @Override
    public Empleado save(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido, rut, cargo, salario_base, bonos, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getApellido());
            stmt.setString(3, empleado.getRut());
            stmt.setString(4, empleado.getCargo());
            stmt.setBigDecimal(5, empleado.getSalarioBase());
            stmt.setBigDecimal(6, empleado.getBonos());
            stmt.setBigDecimal(7, empleado.getDescuentos());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear empleado, no se afectaron filas");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    empleado.setId(generatedKeys.getLong(1));
                    logger.info("Empleado creado con ID: {}", empleado.getId());
                } else {
                    throw new SQLException("Error al crear empleado, no se obtuvo ID");
                }
            }

        } catch (SQLException e) {
            logger.error("Error al guardar empleado", e);
            throw new RuntimeException("Error al guardar empleado", e);
        }

        return empleado;
    }

    @Override
    public boolean existsByRut(String rut) {
        String sql = "SELECT COUNT(*) FROM empleados WHERE rut = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    logger.debug("RUT {} existe: {}", rut, exists);
                    return exists;
                }
            }

        } catch (SQLException e) {
            logger.error("Error al verificar RUT: {}", rut, e);
            throw new RuntimeException("Error al verificar RUT", e);
        }

        return false;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Empleado eliminado: {}", id);
            } else {
                logger.warn("No se encontró empleado para eliminar: {}", id);
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar empleado: {}", id, e);
            throw new RuntimeException("Error al eliminar empleado", e);
        }
    }

    private Empleado mapResultSetToEmpleado(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        empleado.setId(rs.getLong("id"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setApellido(rs.getString("apellido"));
        empleado.setRut(rs.getString("rut"));
        empleado.setCargo(rs.getString("cargo"));
        empleado.setSalarioBase(rs.getBigDecimal("salario_base"));
        empleado.setBonos(rs.getBigDecimal("bonos"));
        empleado.setDescuentos(rs.getBigDecimal("descuentos"));

        Timestamp timestamp = rs.getTimestamp("fecha_creacion");
        if (timestamp != null) {
            empleado.setFechaCreacion(timestamp.toLocalDateTime());
        }

        return empleado;
    }
}
