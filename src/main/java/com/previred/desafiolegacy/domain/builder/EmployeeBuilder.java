package com.previred.desafiolegacy.domain.builder;

import com.previred.desafiolegacy.domain.exception.DataDomainException;
import com.previred.desafiolegacy.domain.model.Employee;
import com.previred.desafiolegacy.domain.model.Salary;

import java.math.BigDecimal;

public class EmployeeBuilder {

    private Integer id;
    private String name;
    private String surname;
    private String fiscalId;
    private String role;
    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal discounts;

    public EmployeeBuilder id(Integer id) { this.id = id; return this; }
    public EmployeeBuilder name(String name) { this.name = name; return this; }
    public EmployeeBuilder surname(String surname) { this.surname = surname; return this; }
    public EmployeeBuilder fiscalId(String fiscalId) { this.fiscalId = fiscalId; return this; }
    public EmployeeBuilder role(String cargo) { this.role = cargo; return this; }
    public EmployeeBuilder baseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; return this; }
    public EmployeeBuilder bonus(BigDecimal bonus) { this.bonus = bonus; return this; }
    public EmployeeBuilder discounts(BigDecimal discounts) { this.discounts = discounts; return this; }

    public Employee build() throws DataDomainException {

        if (this.name == null || this.name.trim().isEmpty()) {
            throw new DataDomainException("name", "El nombre es obligatorio.");
        }
        if (this.fiscalId == null) {
            throw new DataDomainException("fiscalId", "El identificador fiscal es obligatorio.");
        }
        Salary salary = new Salary(baseSalary, bonus, discounts);

        return new Employee(id, name, surname, fiscalId, role, salary);
    }

}
