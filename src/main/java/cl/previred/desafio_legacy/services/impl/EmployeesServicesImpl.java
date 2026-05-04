package cl.previred.desafio_legacy.services.impl;

import java.util.List;

import cl.previred.desafio_legacy.dto.Employee;
import cl.previred.desafio_legacy.exception.BusinessExceptions;
import cl.previred.desafio_legacy.exception.ErrorCod;
import cl.previred.desafio_legacy.repocitory.EmployeeRepository;
import cl.previred.desafio_legacy.repocitory.impl.EmployeesRepositoryImpl;
import cl.previred.desafio_legacy.services.EmployeesServices;

/**
 * @author Christopher Gaete O.
 * @version 1.0.0, 01/05/2026
 */
public class EmployeesServicesImpl implements EmployeesServices {

	private final EmployeeRepository repository = new EmployeesRepositoryImpl();
	private static final double MIN_SALARY = 400000D;

	@Override
	public List<Employee> getEmployees() throws BusinessExceptions {
		return repository.findAll();
	}

	@Override
	public void putEmployee(Employee employee) throws BusinessExceptions {
		if (employee.getSalary() < MIN_SALARY) {
			throw new BusinessExceptions(ErrorCod.SALARY, "El salario no es valido");
		}
		repository.save(employee);
	}


	@Override
	public void delEmployee(int id) throws BusinessExceptions {
		repository.delete(id);

	}

}
