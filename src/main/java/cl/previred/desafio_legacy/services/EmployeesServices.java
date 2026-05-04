package cl.previred.desafio_legacy.services;

import java.util.List;

import cl.previred.desafio_legacy.dto.Employee;
import cl.previred.desafio_legacy.exception.BusinessExceptions;


/**
 * @author Christopher Gaete O.
 * @version 1.0.0, 01/05/2026
 */
public interface EmployeesServices {

	/**
	 * 
	 * Retonar lista de  employees. En caso de error retonar una BusinessExceptions
	 * @return {@link List} Lista de employees
	 * @throws BusinessExceptions
	 */
	public  List<Employee> getEmployees() throws BusinessExceptions;
	
	/**
	 * Agrega un nuevo empleado.
	 * @param employee {@link Employee}
	 * @throws BusinessExceptions
	 */
	public  void putEmployee(Employee employee) throws BusinessExceptions;
	
	/**
	 * Elimina un employee a partir de un id.
	 * @param id {@link Integer}
	 * @throws BusinessExceptions
	 * 
	 */
	public void delEmployee(int id) throws BusinessExceptions;
	
}
