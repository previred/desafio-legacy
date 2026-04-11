package cl.previred.challenge.employees.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import cl.previred.challenge.employees.model.Compensation;
import cl.previred.challenge.employees.model.Employee;

public class EmployeeRepository {

	private final DataSource dataSource;

	public EmployeeRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<Employee> findAll() {
		List<Employee> employees = new ArrayList<>();

		String sql = """
				SELECT e.id,
				       e.name,
				       e.last_name,
				       e.document_number,
				       e.position,
				       c.base_salary,
				       c.bonus,
				       c.discounts
				FROM employee e
				INNER JOIN compensation c ON c.employee_id = e.id
				ORDER BY e.id
				""";

		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				Employee employee = new Employee(resultSet.getLong("id"), resultSet.getString("name"),
						resultSet.getString("last_name"), resultSet.getString("document_number"),
						resultSet.getString("position"), new Compensation(resultSet.getDouble("base_salary"),
								resultSet.getDouble("bonus"), resultSet.getDouble("discounts")));

				employees.add(employee);
			}

			return employees;

		} catch (SQLException e) {
			throw new RuntimeException("Error al obtener empleados", e);
		}
	}

	public void save(Employee employee) {
		String employeeSql = """
				INSERT INTO employee (name, last_name, document_number, position)
				VALUES (?, ?, ?, ?)
				""";

		String compensationSql = """
				INSERT INTO compensation (employee_id, base_salary, bonus, discounts)
				VALUES (?, ?, ?, ?)
				""";

		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			Long employeeId;

			try (PreparedStatement employeeStatement = connection.prepareStatement(employeeSql,
					Statement.RETURN_GENERATED_KEYS)) {

				employeeStatement.setString(1, employee.getName());
				employeeStatement.setString(2, employee.getLastName());
				employeeStatement.setString(3, employee.getDocumentNumber());
				employeeStatement.setString(4, employee.getPosition());

				employeeStatement.executeUpdate();

				try (ResultSet generatedKeys = employeeStatement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						employeeId = generatedKeys.getLong(1);
					} else {
						throw new RuntimeException("No se pudo obtener el ID generado del empleado");
					}
				}
			}

			try (PreparedStatement compensationStatement = connection.prepareStatement(compensationSql)) {
				compensationStatement.setLong(1, employeeId);
				compensationStatement.setDouble(2, employee.getCompensation().getBaseSalary());
				compensationStatement.setDouble(3, employee.getCompensation().getBonus());
				compensationStatement.setDouble(4, employee.getCompensation().getDiscounts());

				compensationStatement.executeUpdate();
			}

			connection.commit();

		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException rollbackException) {
					throw new RuntimeException("Error al hacer rollback", rollbackException);
				}
			}
			throw new RuntimeException("Error al guardar empleado y compensación", e);

		} finally {
			if (connection != null) {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException("Error al cerrar conexión", e);
				}
			}
		}
	}

	public boolean deleteById(Long id) {
		String sql = "DELETE FROM employee WHERE id = ?";

		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setLong(1, id);
			int rowsAffected = statement.executeUpdate();

			return rowsAffected > 0;

		} catch (SQLException e) {
			throw new RuntimeException("Error al eliminar empleado", e);
		}
	}

	public boolean existsByDocumentNumber(String documentNumber) {
		String sql = "SELECT COUNT(*) FROM employee WHERE document_number = ?";

		try (Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, documentNumber);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt(1) > 0;
				}

				return false;
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error al validar RUT/DNI duplicado", e);
		}
	}
}