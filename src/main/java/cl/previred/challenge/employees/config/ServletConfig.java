package cl.previred.challenge.employees.config;

import javax.sql.DataSource;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.previred.challenge.employees.repository.EmployeeRepository;
import cl.previred.challenge.employees.service.EmployeeService;
import cl.previred.challenge.employees.servlet.EmployeeServlet;

@Configuration
public class ServletConfig {

	@Bean
	public EmployeeRepository employeeRepository(DataSource dataSource) {
		return new EmployeeRepository(dataSource);
	}

	@Bean
	public EmployeeService employeeService(EmployeeRepository employeeRepository) {
		return new EmployeeService(employeeRepository);
	}

	@Bean
	public ServletRegistrationBean<EmployeeServlet> employeeServlet(EmployeeService employeeService) {
		return new ServletRegistrationBean<>(new EmployeeServlet(employeeService), "/api/empleados");
	}
}