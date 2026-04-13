package com.desafio.legacy.repositories.impl;

import com.desafio.legacy.entities.Empleado;
import com.desafio.legacy.repositories.EmpleadoRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoRepositoryImpl implements EmpleadoRepository {
    private final DataSource dataSource;

    public EmpleadoRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public List<Empleado> listarEmpleados() {
        List<Empleado> empleados = new ArrayList<>();
        String sql = "SELECT * FROM empleados WHERE activo = TRUE";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Empleado e = new Empleado();
                e.setId(rs.getLong("id"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setRut_dni(rs.getString("rut_dni"));
                e.setCargo(rs.getString("cargo"));
                e.setSalario(rs.getDouble("salario"));
                e.setBono(rs.getDouble("bono"));
                e.setDescuento(rs.getDouble("descuento"));
                empleados.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return empleados;
    }

    @Override
    public void registrarEmpleado(Empleado empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido, rut_dni, cargo, salario, bono, descuento, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empleado.getNombre());
            stmt.setString(2, empleado.getApellido());
            stmt.setString(3, empleado.getRut_dni());
            stmt.setString(4, empleado.getCargo());
            stmt.setDouble(5, empleado.getSalario());
            stmt.setDouble(6, empleado.getBono());
            stmt.setDouble(7, empleado.getDescuento());
            stmt.setBoolean(8, empleado.isActivo());

            stmt.executeUpdate();
            System.out.println("Empleado guardado exitósamente");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar en la base de datos", e);
        }
    }

    @Override
    public void eliminarEmpleado(Long id) throws SQLException {
        String sql = "UPDATE empleados SET activo = FALSE WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el empleado con ID: " + id);
            }
        }

    }

    @Override
    public Optional<Empleado> buscarPorRut(String rut_dni) {
        String sql = "SELECT * FROM empleados WHERE rut_dni = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut_dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Empleado emp = new Empleado();
                    emp.setRut_dni(rs.getString("rut_dni"));
                    return Optional.of(emp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
