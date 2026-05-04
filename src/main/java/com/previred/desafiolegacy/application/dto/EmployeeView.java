package com.previred.desafiolegacy.application.dto;

import java.math.BigDecimal;

public class EmployeeView {

    private Integer id;
    private String name;
    private String surname;
    private String fiscalId;
    private String role;
    private BigDecimal salary;
    private BigDecimal bonus;
    private BigDecimal discounts;

    private BigDecimal neto;

    public EmployeeView() {
    }

    public EmployeeView(Integer id, String name, String surname, String fiscalId, String role, BigDecimal salary, BigDecimal bonus, BigDecimal discounts, BigDecimal neto) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.fiscalId = fiscalId;
        this.role = role;
        this.salary = salary;
        this.bonus = bonus;
        this.discounts = discounts;
        this.neto = neto;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public void setBase(BigDecimal salary) {
        this.salary = salary;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public void setDiscounts(BigDecimal discounts) {
        this.discounts = discounts;
    }

    public Integer getId() {
        return id;
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

    public BigDecimal getNeto() {
        return neto;
    }
}
