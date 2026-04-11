package cl.previred.challenge.employees.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Compensation {

    private double baseSalary;
    private double bonus;
    private double discounts;
}