package com.previred.desafiolegacy.infrastructure.config;

import com.previred.desafiolegacy.application.service.CreateEmployeeService;
import com.previred.desafiolegacy.application.service.DeleteEmployeeService;
import com.previred.desafiolegacy.application.service.GetEmployeesService;
import com.previred.desafiolegacy.application.usecause.CreateEmployeeUseCause;
import com.previred.desafiolegacy.application.usecause.DeleteEmployeeUseCause;
import com.previred.desafiolegacy.application.usecause.GetEmployeesUseCause;
import com.previred.desafiolegacy.domain.port.EmployeeRepository;
import com.previred.desafiolegacy.infrastructure.adapter.out.JdbcEmployeeRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletContextConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        CreateEmployeeService employeeService = new CreateEmployeeService(empleadoRepository());
        DeleteEmployeeService deleteEmployeeService = new DeleteEmployeeService(empleadoRepository());
        GetEmployeesService getEmployeesService = new GetEmployeesService(empleadoRepository());

        sce.getServletContext().setAttribute("createEmployeeUseCause", createEmployeeUseCause(employeeService));
        sce.getServletContext().setAttribute("deleteEmployeeUseCause", deleteEmployeeUseCause(deleteEmployeeService));
        sce.getServletContext().setAttribute("getEmployeesUseCause", getEmployeesUseCause(getEmployeesService));

        ServletContextListener.super.contextInitialized(sce);
    }

    private EmployeeRepository empleadoRepository() {
        return new JdbcEmployeeRepository(DatabaseConfig.getInstancia());
    }

    private CreateEmployeeUseCause  createEmployeeUseCause(CreateEmployeeService empleadoService) {
        return empleadoService;
    }
    private DeleteEmployeeUseCause deleteEmployeeUseCause(DeleteEmployeeService empleadoService) {
        return empleadoService;
    }

    private GetEmployeesUseCause getEmployeesUseCause(GetEmployeesService empleadoService) {
        return empleadoService;
    }




}
