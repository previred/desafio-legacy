package dev.unicoast.desafio.repository;

import dev.unicoast.desafio.model.entity.EmpleadoEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmpleadoRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmpleadoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<EmpleadoEntity> rowMapper = (rs, rowNum) -> {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(rs.getLong("id"));
        entity.setNombre(rs.getString("nombre"));
        entity.setApellido(rs.getString("apellido"));
        entity.setRut(rs.getString("rut"));
        entity.setCargo(rs.getString("cargo"));
        entity.setSalarioBase(rs.getBigDecimal("salario_base"));
        entity.setBonos(rs.getBigDecimal("bonos"));
        entity.setDescuentos(rs.getBigDecimal("descuentos"));
        return entity;
    };

    public boolean existeRut(String rut) {
        String sql = "SELECT COUNT(*) FROM empleados WHERE rut = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rut);
        return count != null && count > 0;
    }

    public void guardar(EmpleadoEntity empleado) {
        String sql = "INSERT INTO empleados (nombre, apellido, rut, cargo, salario_base, bonos, descuentos) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, 
                empleado.getNombre(), 
                empleado.getApellido(), 
                empleado.getRut(), 
                empleado.getCargo(), 
                empleado.getSalarioBase(), 
                empleado.getBonos(), 
                empleado.getDescuentos());
    }

    public List<EmpleadoEntity> obtenerTodos() {
        String sql = "SELECT * FROM empleados";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public int eliminar(Long id) {
        String sql = "DELETE FROM empleados WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
