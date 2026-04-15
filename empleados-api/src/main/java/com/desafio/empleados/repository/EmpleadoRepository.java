package com.desafio.empleados.repository;

import com.desafio.empleados.model.Empleado;
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
import java.util.Optional;

@Repository
public class EmpleadoRepository {

    private final DataSource dataSource;

    public EmpleadoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Empleado> findAll() throws SQLException {
        final String sql = "SELECT id, nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos "
                + "FROM empleado ORDER BY id";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Empleado> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        }
    }

    public Optional<Empleado> findById(long id) throws SQLException {
        final String sql = "SELECT id, nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos "
                + "FROM empleado WHERE id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        }
    }

    public boolean existsByRutDni(String rutDniNormalizado) throws SQLException {
        final String sql = "SELECT COUNT(*) FROM empleado WHERE rut_dni = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, rutDniNormalizado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    public Empleado insert(Empleado e) throws SQLException {
        final String sql = "INSERT INTO empleado (nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getRutDni());
            ps.setString(4, e.getCargo());
            ps.setBigDecimal(5, e.getSalarioBase());
            ps.setBigDecimal(6, e.getBono());
            ps.setBigDecimal(7, e.getDescuentos());
            ps.executeUpdate();
            long generatedId = e.getId() != null ? e.getId() : 0L;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    generatedId = keys.getLong(1);
                }
            }
            Empleado insertado = copiarCon(e, generatedId);
            return insertado;
        }
    }

    private static Empleado copiarCon(Empleado origen, long nuevoId) {
        Empleado copia = new Empleado();
        copia.setId(nuevoId);
        copia.setNombre(origen.getNombre());
        copia.setApellido(origen.getApellido());
        copia.setRutDni(origen.getRutDni());
        copia.setCargo(origen.getCargo());
        copia.setSalarioBase(origen.getSalarioBase());
        copia.setBono(origen.getBono());
        copia.setDescuentos(origen.getDescuentos());
        return copia;
    }

    public boolean deleteById(long id) throws SQLException {
        final String sql = "DELETE FROM empleado WHERE id = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getLong("id"));
        e.setNombre(rs.getString("nombre"));
        e.setApellido(rs.getString("apellido"));
        e.setRutDni(rs.getString("rut_dni"));
        e.setCargo(rs.getString("cargo"));
        e.setSalarioBase(rs.getBigDecimal("salario_base"));
        BigDecimal bono = rs.getBigDecimal("bono");
        e.setBono(bono != null ? bono : BigDecimal.ZERO);
        BigDecimal desc = rs.getBigDecimal("descuentos");
        e.setDescuentos(desc != null ? desc : BigDecimal.ZERO);
        return e;
    }
}
