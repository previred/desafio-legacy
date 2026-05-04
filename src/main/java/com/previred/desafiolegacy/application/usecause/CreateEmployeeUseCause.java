package com.previred.desafiolegacy.application.usecause;

import com.previred.desafiolegacy.application.dto.CommandResult;
import com.previred.desafiolegacy.application.dto.CreateEmployeeCommand;

public interface CreateEmployeeUseCause {

    CommandResult execute(CreateEmployeeCommand createEmployeeCommand);

}
