package cl.previred.desafio_legacy.repocitory.configure;

import java.util.List;

import cl.previred.desafio_legacy.exception.BusinessExceptions;


/**
 * 
 *  Servicios de employees.
 * 
 * @author Christopher Gaete Oliveres.
 * @version 1.0.0, 02/05/2026
 * 
 */

public interface EmployeeRepocitory {

	public List<EmployeeRepocitory> getEmployees() throws BusinessExceptions;

	public void putEmployees() throws BusinessExceptions;

	public void delete(int id) throws BusinessExceptions;

}
