package cl.previred.desafio_legacy.repocitory;

import java.util.List;

import cl.previred.desafio_legacy.dto.Employee;
import cl.previred.desafio_legacy.exception.BusinessExceptions;

/**
 * @author Christopher Gaete O.
 * @version 1.0.0, 01/05/2026
 */
public interface EmployeeRepository {
	/**
	 * Retorna lista de empledos desde la base datos.
	 * 
	 * @return {@link List}
	 * @throws BusinessExceptions
	 */
	public List<Employee> findAll() throws BusinessExceptions;

	/**
	 * Almacena un employee en base de datos.
	 * 
	 * @param employee {@link Employee}
	 * @throws BusinessExceptions
	 */
	public void save(Employee employee) throws BusinessExceptions;

	/**
	 * Elimina un employee en la base datos a partir de un 1 id.
	 * 
	 * @param id
	 * @throws BusinessExceptions
	 */

	public void delete(int id) throws BusinessExceptions;
}
