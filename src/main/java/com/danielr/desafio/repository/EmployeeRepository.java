package com.danielr.desafio.repository;

import com.danielr.desafio.exception.BusinessException;
import com.danielr.desafio.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper reutilizable: convierte una fila SQL → objeto Employee
    private final RowMapper<Employee> rowMapper = (rs, rowNum) -> {
        Employee e = new Employee();
        e.setId(rs.getLong("id"));
        e.setFirstName(rs.getString("first_name"));
        e.setLastName(rs.getString("last_name"));
        e.setTaxId(rs.getString("tax_id"));
        e.setPosition(rs.getString("position"));
        e.setBaseSalary(rs.getBigDecimal("base_salary"));
        e.setBonus(rs.getBigDecimal("bonus"));
        e.setDeductions(rs.getBigDecimal("deductions"));
        e.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        e.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        Timestamp deletedAt = rs.getTimestamp("deleted_at");
        e.setDeletedAt(deletedAt != null ? deletedAt.toLocalDateTime() : null);

        return e;
    };

    /**
     * Se filtra por registros que no esten eliminados
     */
    public List<Employee> findAll() {
        logger.debug("Ejecutando Lista de empleados filtrada por eliminados");
        String sql = "SELECT * FROM employees WHERE deleted_at IS NULL";
        List<Employee> query = jdbcTemplate.query(sql, rowMapper);
        return query;
    }

    public Optional<Employee> findById(Long id) {
        logger.debug("Ejecutando findById: {}", id);
        String sql = "SELECT * FROM employees WHERE id = ? AND deleted_at IS NULL";
        List<Employee> result = jdbcTemplate.query(sql, rowMapper, id);
        return result.stream().findFirst();
    }

    /**
     * Find by Rut/Dni
     */
    public boolean existsByDni(String taxId) {
        logger.debug("Verificando DNI duplicado: {}", taxId);
        String sql = "SELECT COUNT(*) FROM employees WHERE tax_id = ? AND deleted_at IS NULL";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, taxId);
        return count != null && count > 0;
    }

    /**
     * Create Method
     */
    public Employee save(Employee employee) {

        logger.debug("Ejecutando creacion de empleado........");

        String sql = "INSERT INTO employees (first_name, last_name, tax_id, position, base_salary, bonus, deductions) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";


        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {

            int rows = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, employee.getFirstName());
                ps.setString(2, employee.getLastName());
                ps.setString(3, employee.getTaxId());
                ps.setString(4, employee.getPosition());
                ps.setBigDecimal(5, employee.getBaseSalary());
                ps.setBigDecimal(6, employee.getBonus() != null ? employee.getBonus() : BigDecimal.ZERO);
                ps.setBigDecimal(7, employee.getDeductions() != null ? employee.getDeductions() : BigDecimal.ZERO);

                return ps;
            }, keyHolder);

            if (rows == 0) {
                logger.error("No se pudo crear el empleado con DNI: {}", employee.getTaxId());
                throw new BusinessException("No se pudo crear el empleado");
            }

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null) {
                employee.setId(((Number) keys.get("ID")).longValue());
                employee.setCreatedAt(((Timestamp) keys.get("CREATED_AT")).toLocalDateTime());
                employee.setUpdatedAt(((Timestamp) keys.get("UPDATED_AT")).toLocalDateTime());
            }

            return employee;
        } catch (BusinessException e) {
            logger.error(e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            logger.error("Violacion de constraint al crear empleado con DNI: {}", employee.getTaxId());
            throw new BusinessException("Ya existe un empleado con el RUT: " + employee.getTaxId());
        } catch (RuntimeException e) {
            logger.error("Error de BD al crear empleado con DNI: {} - {}", employee.getTaxId(), e.getMessage());
            logger.error("Linea..... {}", e.getStackTrace());
            throw new BusinessException("Error al crear el empleado en la base de datos");
        }

    }

    /**
     * Debido a buenas practicas no se elimina el registro
     * y se procede a asignar fecha de eliminacion
     */
    public boolean deleteById(Long id) {
        logger.debug("Eliminando empleado con id: {} .....", id);
        String sql = "UPDATE employees " +
                "SET deleted_at = CURRENT_TIMESTAMP, " +
                "updated_at = CURRENT_TIMESTAMP, " +
                "tax_id = CONCAT(tax_id, '_deleted_', id) " +
                "WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return rows > 0;
    }

}
