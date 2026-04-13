package com.danielr.desafio.service;

import com.danielr.desafio.dto.EmployeeRequestDTO;
import com.danielr.desafio.dto.EmployeeResponseDTO;
import com.danielr.desafio.exception.BusinessException;
import com.danielr.desafio.model.Employee;
import com.danielr.desafio.repository.EmployeeRepository;
import com.danielr.desafio.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employeeSave;

    private EmployeeRequestDTO dto;

    @BeforeEach
    public void setUp() {
        dto = new EmployeeRequestDTO(
                "Juan",
                "Pérez",
                "12345678-9",
                "Desarrollador",
                new BigDecimal("800000"),
                new BigDecimal("100000"),
                new BigDecimal("50000")
        );

        employeeSave = new Employee();
        employeeSave.setId(1L);
        employeeSave.setFirstName("Juan");
        employeeSave.setLastName("Pérez");
        employeeSave.setTaxId("12345678-9");
        employeeSave.setPosition("Desarrollador");
        employeeSave.setBaseSalary(new BigDecimal("800000"));
        employeeSave.setBonus(new BigDecimal("100000"));
        employeeSave.setDeductions(new BigDecimal("50000"));
        employeeSave.setCreatedAt(LocalDateTime.now());
        employeeSave.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * findAll
     */
    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("Retorna lista empleados correctamente")
        void returnsEmpList() {
            when(employeeRepository.findAll()).thenReturn(List.of(employeeSave));

            List<EmployeeResponseDTO> result = employeeService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Juan");
            assertThat(result.get(0).lastname()).isEqualTo("Pérez");
            verify(employeeRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("retorna lista vacía sin errores")
        void returnsEmptyList() {
            when(employeeRepository.findAll()).thenReturn(List.of());

            List<EmployeeResponseDTO> result = employeeService.findAll();

            assertThat(result).isEmpty();
            verify(employeeRepository, times(1)).findAll();
        }

    }

    /**
     * Save
     */
    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("empleado válido se crea correctamente")
        void validEmployee_returnsDTO() {
            when(employeeRepository.existsByDni(any())).thenReturn(false);
            when(employeeRepository.save(any())).thenReturn(employeeSave);

            EmployeeResponseDTO result = employeeService.save(dto);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Juan");
            assertThat(result.lastname()).isEqualTo("Pérez");
            assertThat(result.dni()).isEqualTo("12345678-9");
            verify(employeeRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("salario menor a $400.000 lanza BusinessException")
        void lowSalary_throwsBusinessException() {
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Juan", "Pérez", "12345678-9", "Desarrollador",
                    new BigDecimal("300000"), null, null
            );

            assertThatThrownBy(() -> employeeService.save(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrors())
                                .anyMatch(e -> e.contains("400.000"));
                    });

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("bono exactamente 50% del salario es válido")
        void bonusExactly50Percent_isValid() {
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Juan", "Pérez", "12345678-9", "Desarrollador",
                    new BigDecimal("800000"),
                    new BigDecimal("400000"), // exactamente 50%
                    null
            );

            when(employeeRepository.existsByDni(any())).thenReturn(false);
            when(employeeRepository.save(any())).thenReturn(employeeSave);

            assertThatCode(() -> employeeService.save(request))
                    .doesNotThrowAnyException();

            verify(employeeRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("bono mayor al 50% del salario lanza BusinessException")
        void bonusOver50Percent_throwsBusinessException() {
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Juan", "Pérez", "12345678-9", "Desarrollador",
                    new BigDecimal("800000"),
                    new BigDecimal("500000"), // 500.000 > 50% de 800.000
                    new BigDecimal("0")
            );

            assertThatThrownBy(() -> employeeService.save(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrors())
                                .anyMatch(e -> e.contains("bono"));
                    });

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("bono en cero es válido")
        void zeroBonus_isValid() {
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Juan", "Pérez", "12345678-9", "Desarrollador",
                    new BigDecimal("800000"),
                    BigDecimal.ZERO,
                    null
            );

            when(employeeRepository.existsByDni(any())).thenReturn(false);
            when(employeeRepository.save(any())).thenReturn(employeeSave);

            assertThatCode(() -> employeeService.save(request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("descuentos igual al salario es válido")
        void deductionsEqualSalary_isValid() {
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Juan", "Pérez", "12345678-9", "Desarrollador",
                    new BigDecimal("800000"),
                    null,
                    new BigDecimal("800000") // igual al salario
            );

            when(employeeRepository.existsByDni(any())).thenReturn(false);
            when(employeeRepository.save(any())).thenReturn(employeeSave);

            assertThatCode(() -> employeeService.save(request))
                    .doesNotThrowAnyException();

            verify(employeeRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("descuentos mayores al salario lanzan BusinessException")
        void deductionsOverSalary_throwsBusinessException() {
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Juan", "Pérez", "12345678-9", "Desarrollador",
                    new BigDecimal("800000"),
                    null,
                    new BigDecimal("900000") // 900.000 > 800.000
            );

            assertThatThrownBy(() -> employeeService.save(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrors())
                                .anyMatch(e -> e.contains("descuentos"));
                    });

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("descuentos en cero son válidos")
        void zeroDeductions_isValid() {
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Juan", "Pérez", "12345678-9", "Desarrollador",
                    new BigDecimal("800000"),
                    null,
                    BigDecimal.ZERO
            );

            when(employeeRepository.existsByDni(any())).thenReturn(false);
            when(employeeRepository.save(any())).thenReturn(employeeSave);

            assertThatCode(() -> employeeService.save(request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("RUT duplicado lanza BusinessException")
        void duplicateDni_throwsBusinessException() {
            when(employeeRepository.existsByDni("12345678-9")).thenReturn(true);

            assertThatThrownBy(() -> employeeService.save(dto))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrors())
                                .anyMatch(e -> e.contains("12345678-9"));
                    });

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("múltiples errores se acumulan en una sola excepción")
        void multipleErrors_accumulatesAllErrors() {
            EmployeeRequestDTO request = new EmployeeRequestDTO(
                    "Juan", "Pérez", "12.345.678-9", "Desarrollador",
                    new BigDecimal("300000"),  // salario inválido
                    new BigDecimal("200000"),  // bono inválido
                    new BigDecimal("400000")   // descuentos inválidos
            );

            assertThatThrownBy(() -> employeeService.save(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrors()).hasSizeGreaterThan(1);
                    });

            verify(employeeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("elimina empleado existente y retorna true")
        void existingEmployee_returnsTrue() {
            when(employeeRepository.deleteById(1L)).thenReturn(true);

            boolean result = employeeService.delete(1L);

            assertThat(result).isTrue();
            verify(employeeRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("ID inexistente lanza BusinessException")
        void nonExistingEmployee_throwsBusinessException() {
            when(employeeRepository.deleteById(99L)).thenReturn(false);

            assertThatThrownBy(() -> employeeService.delete(99L))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrors())
                                .anyMatch(e -> e.contains("99"));
                    });
        }
    }

}
