package com.previred.desafiolegacy.application.dto;

import java.math.BigDecimal;

public class CreateEmployeeCommand {

    private String name;

    private String surname;

    private String fiscalId;

    private String role;

    private BigDecimal salary;

    private BigDecimal bonus;

    private BigDecimal discounts;

    public CreateEmployeeCommand() {
    }

    public CreateEmployeeCommand(String name,
                                 String surname,
                                 String fiscalId,
                                 String role,
                                 BigDecimal salary,
                                 BigDecimal bonus,
                                 BigDecimal discounts) {
        this.name = name;
        this.surname = surname;
        this.fiscalId = fiscalId;
        this.role = role;
        this.salary = salary;
        this.bonus = bonus;
        this.discounts = discounts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setFiscalId(String fiscalId) {
        this.fiscalId = fiscalId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public void setDiscounts(BigDecimal discounts) {
        this.discounts = discounts;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getFiscalId() {
        return fiscalId;
    }

    public String getRole() {
        return role;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public BigDecimal getDiscounts() {
        return discounts;
    }
}
