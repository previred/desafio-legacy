package cl.previred.desafio_legacy.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.previred.desafio_legacy.dto.Employee;
import cl.previred.desafio_legacy.exception.BusinessExceptions;
import cl.previred.desafio_legacy.services.EmployeesServices;
import cl.previred.desafio_legacy.services.impl.EmployeesServicesImpl;

/**
 * Controller que expone las apis para los empleados.
 *
 * @author Christopher Gaete O.
 * @version 1.0.0, 01-05-2026
 * 
 */
@WebServlet("/api/empleados/*")
public class Controller extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	private static final long serialVersionUID = 1L;
	private final ObjectMapper mapper = new ObjectMapper();
	private final EmployeesServices services = new EmployeesServicesImpl();

	/**
	 * @param req  {HttpServletRequest}
	 * @param resp {HttpServletRequest}
	 * @return list de Employees.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		List<Employee> lista;
		try {
			lista = services.getEmployees();
			mapper.writeValue(resp.getOutputStream(), lista);
		} catch (BusinessExceptions e) {
			logger.error(e.getMessage());
			mapper.writeValue(resp.getOutputStream(), e.getErrorCod());
			resp.setStatus(500);
		}

	}

	/**
	 * Agrega  nuevo Empleado.
	 * 
	 * @param req  {HttpServletRequest}
	 * @param resp {HttpServletRequest}
	 * 
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			Employee employee = mapper.readValue(req.getInputStream(), Employee.class);
			services.putEmployee(employee);
			resp.setStatus(201);
		} catch (BusinessExceptions e) {
			logger.error(e.getMessage());
			resp.setStatus(400);
			resp.setContentType("application/json");
			mapper.writeValue(resp.getOutputStream(), e.getErrorCod());

		}
	}

	/**
	 * 
	 * Borra un empleado a partir de su id.
	 * 
	 * @param req  {HttpServletRequest}
	 * @param resp {HttpServletRequest}
	 * 
	 */

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			String pathInfo = req.getPathInfo();
			if (pathInfo != null && pathInfo.startsWith("/")) {
				int id = Integer.parseInt(pathInfo.substring(1));
				services.delEmployee(id);
				resp.setStatus(204);
			} else {
				resp.setStatus(412);
			}
		} catch (BusinessExceptions e) {
			logger.error(e.getMessage());
			mapper.writeValue(resp.getOutputStream(), e.getErrorCod());
			resp.setStatus(500);
		}
	}

}
