package cl.previred.challenge.employees.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.previred.challenge.employees.model.Employee;
import cl.previred.challenge.employees.repository.EmployeeRepository;
import cl.previred.challenge.employees.validation.EmployeeValidator;
import cl.previred.challenge.employees.validation.ValidationError;

public class EmployeeService {

	private static final Logger log = LoggerFactory.getLogger(EmployeeRepository.class);

	private final EmployeeRepository employeeRepository;
	private final EmployeeValidator validator = new EmployeeValidator();

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}

	public List<ValidationError> save(Employee employee) {

		log.info("Guardando empleado con documento {}", employee.getDocumentNumber());

		List<ValidationError> errors = validator.validate(employee);

		if (employeeRepository.existsByDocumentNumber(employee.getDocumentNumber())) {
			errors.add(new ValidationError("documentNumber", "El RUT/DNI ya existe"));
		}

		if (!errors.isEmpty()) {
			return errors;
		}

		try {
			employeeRepository.save(employee);
		} catch (RuntimeException e) {
			log.error("Error al guardar empleado", e);
			throw new RuntimeException("Error al guardar empleado", e);
		}
		return List.of();
	}

	public boolean deleteById(Long id) {
		return employeeRepository.deleteById(id);
	}
}