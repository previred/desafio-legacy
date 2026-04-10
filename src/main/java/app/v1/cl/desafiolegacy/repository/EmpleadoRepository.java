package app.v1.cl.desafiolegacy.repository;

import app.v1.cl.desafiolegacy.model.Empleado;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmpleadoRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Empleado> ROW_MAPPER = (rs, rowNum) -> Empleado.builder()
            .id(rs.getLong("id"))
            .nombre(rs.getString("nombre"))
            .apellido(rs.getString("apellido"))
            .rut(rs.getString("rut"))
            .cargo(rs.getString("cargo"))
            .salario(rs.getBigDecimal("salario"))
            .bonos(rs.getBigDecimal("bonos"))
            .descuentos(rs.getBigDecimal("descuentos"))
            .build();

    public List<Empleado> findAll() {
        return jdbcTemplate.query("SELECT * FROM empleados ORDER BY id", ROW_MAPPER);
    }

    public Optional<Empleado> findById(Long id) {
        List<Empleado> results = jdbcTemplate.query(
                "SELECT * FROM empleados WHERE id = ?", ROW_MAPPER, id);
        return results.stream().findFirst();
    }

    public boolean existsByRut(String rut) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM empleados WHERE rut = ?", Integer.class, rut);
        return count != null && count > 0;
    }

    public Empleado save(Empleado empleado) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO empleados (nombre, apellido, rut, cargo, salario, bonos, descuentos) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalario());
            ps.setBigDecimal(6, empleado.getBonos());
            ps.setBigDecimal(7, empleado.getDescuentos());
            return ps;
        }, keyHolder);

        empleado.setId(keyHolder.getKey().longValue());
        return empleado;
    }

    public boolean deleteById(Long id) {
        int rows = jdbcTemplate.update("DELETE FROM empleados WHERE id = ?", id);
        return rows > 0;
    }
}
