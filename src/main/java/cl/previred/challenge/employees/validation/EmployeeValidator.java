package cl.previred.challenge.employees.validation;

import cl.previred.challenge.employees.model.Compensation;
import cl.previred.challenge.employees.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeValidator {

    public List<ValidationError> validate(Employee employee) {

        List<ValidationError> errors = new ArrayList<>();

        if (employee == null) {
            errors.add(new ValidationError("employee", "El empleado es obligatorio"));
            return errors;
        }

        // -------- Datos básicos --------

        if (employee.getName() == null || employee.getName().isBlank()) {
            errors.add(new ValidationError("name", "El nombre es obligatorio"));
        }

        if (employee.getLastName() == null || employee.getLastName().isBlank()) {
            errors.add(new ValidationError("lastName", "El apellido es obligatorio"));
        }

        if (employee.getDocumentNumber() == null || employee.getDocumentNumber().isBlank()) {
            errors.add(new ValidationError("documentNumber", "El RUT/DNI es obligatorio"));
        }

        if (employee.getPosition() == null || employee.getPosition().isBlank()) {
            errors.add(new ValidationError("position", "El cargo es obligatorio"));
        }

        // -------- Compensation --------

        Compensation compensation = employee.getCompensation();

        if (compensation == null) {
            errors.add(new ValidationError("compensation", "La compensación es obligatoria"));
            return errors;
        }

        double baseSalary = compensation.getBaseSalary();
        double bonus = compensation.getBonus();
        double discounts = compensation.getDiscounts();

        // Salario base mínimo
        if (baseSalary < 400000) {
            errors.add(new ValidationError(
                    "compensation.baseSalary",
                    "El salario base no puede ser menor a 400000"
            ));
        }

        // Bonus <= 50% del salario base
        if (baseSalary > 0 && bonus > baseSalary * 0.5) {
            errors.add(new ValidationError(
                    "compensation.bonus",
                    "El bono no puede superar el 50% del salario base"
            ));
        }

        // Descuentos <= salario base
        if (baseSalary > 0 && discounts > baseSalary) {
            errors.add(new ValidationError(
                    "compensation.discounts",
                    "Los descuentos no pueden superar el salario base"
            ));
        }

        return errors;
    }
}