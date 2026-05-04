package com.previred.desafiolegacy.domain.model;

import java.math.BigDecimal;

public class Employee {

    private Integer id;
    private String name;
    private String surname;
    private String fiscalId;
    private String role;
    private Salary salary;


    public Employee(Integer id,
                    String name,
                    String surname,
                    String fiscalId,
                    String role,
                    Salary salary) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.fiscalId = fiscalId;
        this.role = role;
        this.salary = salary;
    }

    public Integer getId() {
        return id;
    }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getFiscalId() { return fiscalId; }
    public String getRole() { return role; }

    public Salary getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "Empleado{" + "id=" + id + ", nombre='" + name + '\'' + ", rut='" + fiscalId + '\'' + '}';
    }

}
