package com.prueba.desafio.dao;

import com.prueba.desafio.model.Empleado;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmpleadoDao {

    private final DataSource dataSource;

    public EmpleadoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Empleado> listar() throws SQLException {
        List<Empleado> lista = new ArrayList<>();

        String sql = "SELECT id, nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos " +
                "FROM empleados ORDER BY id ASC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }
        }

        return lista;
    }

    public Empleado guardar(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleados (nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRutDni());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalarioBase());
            ps.setBigDecimal(6, empleado.getBono());
            ps.setBigDecimal(7, empleado.getDescuentos());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    empleado.setId(rs.getLong(1));
                }
            }

            return empleado;
        }
    }

    public boolean existeRutDni(String rutDni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM empleados WHERE rut_dni = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rutDni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean eliminarPorId(Long id) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    private Empleado map(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getLong("id"));
        e.setNombre(rs.getString("nombre"));
        e.setApellido(rs.getString("apellido"));
        e.setRutDni(rs.getString("rut_dni"));
        e.setCargo(rs.getString("cargo"));
        e.setSalarioBase(rs.getBigDecimal("salario_base"));
        e.setBono(rs.getBigDecimal("bono"));
        e.setDescuentos(rs.getBigDecimal("descuentos"));
        return e;
    }
}