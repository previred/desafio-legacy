package com.previred.desafiolegacy.application.service;

import com.previred.desafiolegacy.application.dto.CommandResult;
import com.previred.desafiolegacy.application.dto.CommandStatus;
import com.previred.desafiolegacy.application.dto.CreateEmployeeCommand;
import com.previred.desafiolegacy.application.usecause.CreateEmployeeUseCause;
import com.previred.desafiolegacy.domain.builder.EmployeeBuilder;
import com.previred.desafiolegacy.domain.exception.DataDomainException;
import com.previred.desafiolegacy.domain.exception.PersistenceException;
import com.previred.desafiolegacy.domain.model.Employee;
import com.previred.desafiolegacy.domain.port.EmployeeRepository;

import java.io.Serializable;

public class CreateEmployeeService implements
        CreateEmployeeUseCause, Serializable {

    private final EmployeeRepository employeeRepository;

    public CreateEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public CommandResult execute(CreateEmployeeCommand createEmployeeCommand) {
        try{
            if(existEmployee(createEmployeeCommand.getFiscalId())){
                return CommandResult.fail(CommandStatus.INVALID_DATA, "fiscalId", "El numero fiscal ya se encuentra registrado.");
            }

            Employee employee = buildEmployee(createEmployeeCommand);

            employeeRepository.guardar(employee);

            return CommandResult.success();

        }catch(DataDomainException ex){
            return CommandResult.fail(CommandStatus.INVALID_DATA, ex.getField(), ex.getMessage());
        }catch(PersistenceException ex){
            return CommandResult.fail(CommandStatus.INTERNAL_ERROR,  ex.getMessage());
        }
    }

    private boolean existEmployee(String rut){
        return employeeRepository.existByRut(rut);
    }

    private Employee buildEmployee(CreateEmployeeCommand command) throws DataDomainException {

        return new EmployeeBuilder()
                .fiscalId(command.getFiscalId())
                .name(command.getName())
                .surname(command.getSurname())
                .role(command.getRole())
                .baseSalary(command.getSalary())
                .bonus(command.getBonus())
                .discounts(command.getDiscounts())
                .build();
    }
}
