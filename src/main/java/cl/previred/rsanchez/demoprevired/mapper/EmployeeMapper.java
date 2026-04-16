package cl.previred.rsanchez.demoprevired.mapper;

import cl.previred.rsanchez.demoprevired.domain.EmployeeRequest;
import cl.previred.rsanchez.demoprevired.domain.EmployeeResponse;
import cl.previred.rsanchez.demoprevired.entity.Employee;

public class EmployeeMapper {

    public static EmployeeResponse toDomain(Employee employee){
        return EmployeeResponse.builder()
                .id(employee.getId())
                .nombre(employee.getNombre())
                .apellido(employee.getApellido())
                .rut(employee.getRut())
                .cargo(employee.getCargo())
                .salario(employee.getSalario())
                .build();
    }

    public static Employee toEntity(EmployeeRequest employee){
        return Employee.builder()
                .cargo(employee.getCargo())
                .salario(employee.getSalario() + employee.getBonos() - employee.getDescuentos())
                .nombre(employee.getNombre())
                .apellido(employee.getApellido())
                .rut(employee.getRut())
                .build();
    }
}
