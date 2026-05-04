package com.previred.desafiolegacy.application.service;

import com.previred.desafiolegacy.application.dto.CommandResult;
import com.previred.desafiolegacy.application.dto.CommandStatus;
import com.previred.desafiolegacy.application.dto.DeleteEmployeeCommand;
import com.previred.desafiolegacy.application.usecause.DeleteEmployeeUseCause;
import com.previred.desafiolegacy.domain.exception.PersistenceException;
import com.previred.desafiolegacy.domain.port.EmployeeRepository;

import java.io.Serializable;

public class DeleteEmployeeService implements DeleteEmployeeUseCause, Serializable {

    private final EmployeeRepository employeeRepository;

    public DeleteEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    @Override
    public CommandResult execute(DeleteEmployeeCommand deleteEmployeeCommand) {

        if(deleteEmployeeCommand.getId() == null){
            return CommandResult.fail(CommandStatus.INVALID_DATA, "id", "Id del registro es nulo.");
        }

        try{
            this.employeeRepository.eliminar(deleteEmployeeCommand.getId());
        } catch (PersistenceException e) {
            return CommandResult.fail(CommandStatus.INTERNAL_ERROR, e.getMessage());
        }

        return CommandResult.success();
    }
}
