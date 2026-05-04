package com.previred.desafiolegacy.application.service;

import com.previred.desafiolegacy.application.dto.EmployeeView;
import com.previred.desafiolegacy.application.usecause.GetEmployeesUseCause;
import com.previred.desafiolegacy.domain.model.Employee;
import com.previred.desafiolegacy.domain.port.EmployeeRepository;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class GetEmployeesService implements GetEmployeesUseCause, Serializable {

    private final EmployeeRepository employeeRepository;

    public GetEmployeesService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeView> execute() {
        List<Employee> empleadosLista = employeeRepository.listarTodos();

        return empleadosLista.stream()
                .map(e -> new EmployeeView(
                        e.getId(),
                        e.getName(),
                        e.getSurname(),
                        e.getFiscalId(),
                        e.getRole(),
                        e.getSalary().getBase(),
                        e.getSalary().getBonus(),
                        e.getSalary().getDiscounts(),
                        e.getSalary().getNeto()
                ))
                .collect(Collectors.toList());
    }
}
