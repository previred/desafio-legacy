package cl.previred.desafio_legacy.repocitory.impl;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.previred.desafio_legacy.dto.Employee;
import cl.previred.desafio_legacy.exception.BusinessExceptions;
import cl.previred.desafio_legacy.exception.ErrorCod;
import cl.previred.desafio_legacy.repocitory.EmployeeRepository;
import cl.previred.desafio_legacy.repocitory.configure.DbConfig;

/**
 * 
 *  Servicios de employees.
 * 
 * @author Christopher Gaete Oliveres.
 * @version 1.0.0, 02/05/2026
 * 
 */
public class EmployeesRepositoryImpl implements EmployeeRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeesRepositoryImpl.class);
	
	
	@Override
	public List<Employee> findAll() throws BusinessExceptions {

		List<Employee> empleados = new ArrayList<Employee>();
		String sql = "SELECT * FROM empleados";
		try (Connection conn = DbConfig.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Employee e = new Employee();
				e.setId(rs.getInt("id"));
				e.setName(rs.getString("name"));
				e.setLastName(rs.getString("last_name"));
				e.setRut(rs.getString("rut"));
				e.setCharget(rs.getString("charget"));
				e.setSalary(rs.getDouble("salary"));
				empleados.add(e);
			}
			return empleados;
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessExceptions(ErrorCod.GENERIC_ERROR, e.getMessage(),e);
		}catch (Exception e) {
			throw new BusinessExceptions(ErrorCod.GENERIC_ERROR, e.getMessage(),e);
		}
	}

	@Override
	public void save(Employee emp) throws BusinessExceptions {
		String sql = "INSERT INTO empleados (name, last_name, rut, charget, salary) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DbConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, emp.getName());
			pstmt.setString(2, emp.getLastName());
			pstmt.setString(3, emp.getRut());
			pstmt.setString(4, emp.getCharget());
			pstmt.setDouble(5, emp.getSalary());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			if (e.getErrorCode() == 23505) {
				throw new BusinessExceptions(ErrorCod.DUPLICATE_RECORD, e.getMessage(),e);
	        } else {
	        	throw new BusinessExceptions(ErrorCod.GENERIC_ERROR, e.getMessage(),e);
	        }
		}catch (Exception e) {
			throw new BusinessExceptions(ErrorCod.GENERIC_ERROR, e.getMessage(),e);
		}
		
	}

	@Override
	public void delete(int id) throws BusinessExceptions  {
		String sql = "DELETE FROM empleados WHERE id = ?";
		try (Connection conn = DbConfig.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new BusinessExceptions(ErrorCod.GENERIC_ERROR, e.getMessage(),e);
		}catch (Exception e) {
			throw new BusinessExceptions(ErrorCod.GENERIC_ERROR, e.getMessage(),e);
		}
	}
}
