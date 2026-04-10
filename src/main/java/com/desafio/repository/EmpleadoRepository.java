package com.desafio.repository;

import com.desafio.model.Empleado;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para Empleado.
 * Implementado con JDBC puro (Connection, PreparedStatement, Try-with-resources).
 * Patrón DAO — NO usa Spring Data ni JPA.
 */
@Repository
public class EmpleadoRepository {

    private final DataSource dataSource;

    public EmpleadoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Obtiene todos los empleados de la base de datos.
     */
    public List<Empleado> findAll() {
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario_base, bonificaciones, descuentos FROM empleados";
        List<Empleado> empleados = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                empleados.add(mapRowToEmpleado(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener empleados: " + e.getMessage(), e);
        }

        return empleados;
    }

    /**
     * Busca un empleado por su ID.
     */
    public Optional<Empleado> findById(Long id) {
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario_base, bonificaciones, descuentos FROM empleados WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToEmpleado(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar empleado por ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Verifica si ya existe un empleado con el RUT dado.
     */
    public boolean existsByRut(String rut) {
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
            throw new RuntimeException("Error al verificar RUT: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Inserta un nuevo empleado y retorna el objeto con el ID generado.
     */
    public Empleado save(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido, rut, cargo, salario_base, bonificaciones, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setDouble(5, empleado.getSalarioBase());
            ps.setDouble(6, empleado.getBonificaciones());
            ps.setDouble(7, empleado.getDescuentos());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el empleado.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    empleado.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar empleado: " + e.getMessage(), e);
        }

        return empleado;
    }

    /**
     * Elimina un empleado por su ID. Retorna true si fue eliminado.
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar empleado: " + e.getMessage(), e);
        }
    }

    /**
     * Mapea una fila del ResultSet a un objeto Empleado.
     */
    private Empleado mapRowToEmpleado(ResultSet rs) throws SQLException {
        Empleado empleado = new Empleado();
        empleado.setId(rs.getLong("id"));
        empleado.setNombre(rs.getString("nombre"));
        empleado.setApellido(rs.getString("apellido"));
        empleado.setRut(rs.getString("rut"));
        empleado.setCargo(rs.getString("cargo"));
        empleado.setSalarioBase(rs.getDouble("salario_base"));
        empleado.setBonificaciones(rs.getDouble("bonificaciones"));
        empleado.setDescuentos(rs.getDouble("descuentos"));
        return empleado;
    }
}

