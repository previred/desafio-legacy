package com.previred.desafiolegacy.application.usecause;

import com.previred.desafiolegacy.application.dto.EmployeeView;
import com.previred.desafiolegacy.domain.model.Employee;

import java.util.List;

public interface GetEmployeesUseCause {

    public List<EmployeeView> execute();


}
