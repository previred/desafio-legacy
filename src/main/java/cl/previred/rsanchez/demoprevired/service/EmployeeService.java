package cl.previred.rsanchez.demoprevired.service;

import cl.previred.rsanchez.demoprevired.domain.EmployeeRequest;
import cl.previred.rsanchez.demoprevired.domain.EmployeeResponse;
import cl.previred.rsanchez.demoprevired.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<EmployeeResponse> getAllEmployees();

    Employee saveEmployee(EmployeeRequest employee) throws Exception;

    void deleteEmployee(Long id);
}
