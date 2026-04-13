package com.danielr.desafio.repository;

import com.danielr.desafio.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(EmployeeRepository.class)
@ActiveProfiles("test")
@Sql(scripts = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee validEmployee;

    @BeforeEach
    void setUp() {
        validEmployee = new Employee();
        validEmployee.setFirstName("Juan");
        validEmployee.setLastName("Pérez");
        validEmployee.setTaxId("12345678-9");
        validEmployee.setPosition("Desarrollador");
        validEmployee.setBaseSalary(new BigDecimal("800000"));
        validEmployee.setBonus(new BigDecimal("100000"));
        validEmployee.setDeductions(new BigDecimal("50000"));
    }

    // ─────────────────────────────────────────────────────────────
    // findAll
    // ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("retorna lista con empleados no eliminados")
        void returnsActiveEmployees() {
            employeeRepository.save(validEmployee);

            List<Employee> result = employeeRepository.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFirstName()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("no retorna empleados eliminados logicamente")
        void doesNotReturnDeletedEmployees() {
            Employee saved = employeeRepository.save(validEmployee);
            employeeRepository.deleteById(saved.getId());

            List<Employee> result = employeeRepository.findAll();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("retorna lista vacia cuando no hay empleados")
        void returnsEmptyList() {
            List<Employee> result = employeeRepository.findAll();

            assertThat(result).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // findById
    // ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("retorna el empleado cuando existe")
        void returnsEmployeeWhenExists() {
            Employee saved = employeeRepository.save(validEmployee);

            Optional<Employee> result = employeeRepository.findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getTaxId()).isEqualTo("12345678-9");
        }

        @Test
        @DisplayName("retorna Optional vacio cuando no existe")
        void returnsEmptyWhenNotExists() {
            Optional<Employee> result = employeeRepository.findById(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("no retorna empleado eliminado logicamente")
        void doesNotReturnDeletedEmployee() {
            Employee saved = employeeRepository.save(validEmployee);
            employeeRepository.deleteById(saved.getId());
            Optional<Employee> result = employeeRepository.findById(saved.getId());
            assertThat(result).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // existsByDni
    // ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("existsByDni")
    class ExistsByDni {

        @Test
        @DisplayName("retorna true cuando el RUT existe")
        void returnsTrueWhenExists() {
            employeeRepository.save(validEmployee);

            boolean result = employeeRepository.existsByDni("12345678-9");

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("retorna false cuando el RUT no existe")
        void returnsFalseWhenNotExists() {
            boolean result = employeeRepository.existsByDni("99999999-9");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("retorna false cuando el empleado fue eliminado logicamente")
        void returnsFalseWhenDeleted() {
            Employee saved = employeeRepository.save(validEmployee);
            employeeRepository.deleteById(saved.getId());

            boolean result = employeeRepository.existsByDni("12345678-9");

            assertThat(result).isFalse();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // save
    // ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("guarda el empleado y retorna con ID generado")
        void savesEmployeeWithGeneratedId() {
            Employee saved = employeeRepository.save(validEmployee);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getId()).isGreaterThan(0L);
            assertThat(saved.getFirstName()).isEqualTo("Juan");
            assertThat(saved.getTaxId()).isEqualTo("12345678-9");
        }

        @Test
        @DisplayName("retorna con createdAt y updatedAt poblados")
        void savesEmployeeWithTimestamps() {
            Employee saved = employeeRepository.save(validEmployee);

            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // deleteById
    // ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteById")
    class DeleteById {

        @Test
        @DisplayName("retorna true cuando el empleado existe")
        void returnsTrueWhenExists() {
            Employee saved = employeeRepository.save(validEmployee);

            boolean result = employeeRepository.deleteById(saved.getId());

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("retorna false cuando el ID no existe")
        void returnsFalseWhenNotExists() {
            boolean result = employeeRepository.deleteById(999L);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("el empleado eliminado no aparece en findAll")
        void deletedEmployeeNotInFindAll() {
            Employee saved = employeeRepository.save(validEmployee);
            employeeRepository.deleteById(saved.getId());

            List<Employee> result = employeeRepository.findAll();

            assertThat(result).isEmpty();
        }
    }


}
