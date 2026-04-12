package cl.previred.desafio.dao;

import cl.previred.desafio.model.Empleado;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoDao {

    private static final String SQL_FIND_ALL =
            "SELECT id, nombre, apellido, rut, cargo, salario_base, bonos, descuentos FROM empleados ORDER BY id";

    private static final String SQL_INSERT =
            "INSERT INTO empleados (nombre, apellido, rut, cargo, salario_base, bonos, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_DELETE = "DELETE FROM empleados WHERE id = ?";

    private static final String SQL_EXISTS_BY_RUT = "SELECT 1 FROM empleados WHERE rut = ? LIMIT 1";

    private final DataSource dataSource;

    public EmpleadoDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Empleado> findAll() throws SQLException {
        List<Empleado> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }

    public Empleado insert(Empleado e) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellido());
            ps.setString(3, e.getRut());
            ps.setString(4, e.getCargo());
            ps.setBigDecimal(5, e.getSalarioBase());
            ps.setBigDecimal(6, e.getBonos());
            ps.setBigDecimal(7, e.getDescuentos());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getLong(1));
                }
            }
        }
        return e;
    }

    public int deleteById(long id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        }
    }

    public boolean existsByRut(String rut) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_RUT)) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getLong("id"));
        e.setNombre(rs.getString("nombre"));
        e.setApellido(rs.getString("apellido"));
        e.setRut(rs.getString("rut"));
        e.setCargo(rs.getString("cargo"));
        e.setSalarioBase(rs.getBigDecimal("salario_base"));
        e.setBonos(rs.getBigDecimal("bonos"));
        e.setDescuentos(rs.getBigDecimal("descuentos"));
        return e;
    }
}
