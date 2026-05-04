package com.previred.desafiolegacy.domain.model;

import com.previred.desafiolegacy.domain.exception.DataDomainException;

import java.math.BigDecimal;

public class Salary {

    private final BigDecimal base;
    private final BigDecimal bonus;
    private final BigDecimal discounts;

    public Salary(BigDecimal base, BigDecimal bonus, BigDecimal discounts) throws DataDomainException {

        validateLegalMinimum(base);
        validateBonusLimit(base, bonus);
        validateDiscountLimit(base, discounts);

        this.base = base;
        this.bonus = bonus;
        this.discounts = discounts;
    }

    private void validateLegalMinimum(BigDecimal base) throws DataDomainException {
        if (base == null) {
            throw new DataDomainException("base", "EL salario base no puede ser nulo.");
        } else if (base.compareTo(BigDecimal.valueOf(400000)) < 0) {
            throw new DataDomainException("base", "El salario base no cumple con el mínimo legal.");
        }
    }

    private void validateBonusLimit(BigDecimal base, BigDecimal bonus) throws DataDomainException {

        BigDecimal limite = base.multiply(new BigDecimal("0.50"));
        if (bonus == null) {
            throw new DataDomainException("bonus", "Los bonos no pueden ser nulo.");
        } else if (bonus.compareTo(limite) > 0) {
            throw new DataDomainException("bonos", "Los bonos exceden el 50% del sueldo.");
        }
    }

    private void validateDiscountLimit(BigDecimal base, BigDecimal discounts) throws DataDomainException {
        if (discounts == null) {
            throw new DataDomainException("discounts", "Los descuentos no pueden ser nulo.");
        } else if (discounts.compareTo(base) > 0) {
            throw new DataDomainException("discounts", "Los descuentos no pueden ser superiores al salario base.");
        }
    }

    public BigDecimal getNeto() {
        return base.add(bonus).subtract(discounts);
    }

    public BigDecimal getBase() {
        return base;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public BigDecimal getDiscounts() {
        return discounts;
    }
}
