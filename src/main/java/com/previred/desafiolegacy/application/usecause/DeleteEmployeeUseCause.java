package com.previred.desafiolegacy.application.usecause;

import com.previred.desafiolegacy.application.dto.CommandResult;
import com.previred.desafiolegacy.application.dto.DeleteEmployeeCommand;

public interface DeleteEmployeeUseCause {

    public CommandResult execute(DeleteEmployeeCommand deleteEmployeeCommand) throws Exception;
}
