package cl.previred.repository.impl;

import cl.previred.model.Empleado;
import cl.previred.repository.EmpleadoRepository;
import cl.previred.util.DatabaseUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoJdbcRepository implements EmpleadoRepository {

    @Override
    public List<Empleado> findAll() {
        List<Empleado> empleados = new ArrayList<Empleado>();
        String sql = "SELECT id, nombre, apellido, rut_dni, cargo, salario FROM empleados ORDER BY id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                empleados.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar empleados", e);
        }
        return empleados;
    }

    @Override
    public Empleado save(Empleado empleado) {
        String sql = "INSERT INTO empleados(nombre, apellido, rut_dni, cargo, salario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRutDni());
            ps.setString(4, empleado.getCargo());
            ps.setInt(5, empleado.getSalario());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) empleado.setId(rs.getLong(1));
            }
            return empleado;
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar empleado", e);
        }
    }

    @Override
    public Optional<Empleado> findById(Long id) {
        String sql = "SELECT id, nombre, apellido, rut_dni, cargo, salario FROM empleados WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar empleado", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Empleado> findByRutDni(String rutDni) {
        String sql = "SELECT id, nombre, apellido, rut_dni, cargo, salario FROM empleados WHERE rut_dni = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rutDni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar empleado por RUT/DNI", e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM empleados WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar empleado", e);
        }
    }

    private Empleado mapRow(ResultSet rs) throws SQLException {
        return new Empleado(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("rut_dni"),
                rs.getString("cargo"),
                rs.getInt("salario")
        );
    }
}
