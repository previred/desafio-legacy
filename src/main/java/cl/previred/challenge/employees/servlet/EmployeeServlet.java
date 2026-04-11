package cl.previred.challenge.employees.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.previred.challenge.employees.model.Employee;
import cl.previred.challenge.employees.service.EmployeeService;
import cl.previred.challenge.employees.validation.ValidationError;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EmployeeServlet extends HttpServlet {

	private final EmployeeService employeeService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public EmployeeServlet(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String json = objectMapper.writeValueAsString(employeeService.findAll());
		response.getWriter().write(json);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Employee employee = objectMapper.readValue(request.getInputStream(), Employee.class);

		List<ValidationError> errors = employeeService.save(employee);

		if (!errors.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");

			String json = objectMapper.writeValueAsString(Map.of("errors", errors));
			response.getWriter().write(json);
			return;
		}

		response.setStatus(HttpServletResponse.SC_CREATED);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String idParam = request.getParameter("id");

		if (idParam == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write(objectMapper
					.writeValueAsString(Map.of("errors", List.of(new ValidationError("id", "El id es obligatorio")))));
			return;
		}

		Long id;
		try {
			id = Long.parseLong(idParam);
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write(objectMapper.writeValueAsString(
					Map.of("errors", List.of(new ValidationError("id", "El id debe ser numérico")))));
			return;
		}

		boolean deleted = employeeService.deleteById(id);

		if (!deleted) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write(objectMapper.writeValueAsString(
					Map.of("errors", List.of(new ValidationError("id", "Empleado no encontrado")))));
			return;
		}

		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}