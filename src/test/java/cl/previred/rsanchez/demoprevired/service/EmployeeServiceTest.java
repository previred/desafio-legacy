package cl.previred.rsanchez.demoprevired.service;

import cl.previred.rsanchez.demoprevired.domain.EmployeeResponse;
import cl.previred.rsanchez.demoprevired.entity.Employee;
import cl.previred.rsanchez.demoprevired.repository.EmployeeRepository;
import cl.previred.rsanchez.demoprevired.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeServiceImpl service;

    @Mock
    private EmployeeRepository repository;

    @Test
    void getAllEmployeesOk(){
        EmployeeResponse responseEmployee = EmployeeResponse.builder().build();
        Employee responseEntity = Employee.builder().build();
        var expectResponse = List.of(responseEmployee);
        var returnResponse = List.of(responseEntity);
        when(repository.findAll()).thenReturn(returnResponse);
        var response = service.getAllEmployees();
        assertNotNull(response);
        assertEquals(response, expectResponse);
        verify(repository, times(1)).findAll();
    }

    @Test
    void deleteEmployeeOk(){
        Long id = 1L;
        service.deleteEmployee(id);
        verify(repository, times(1)).deleteById(id);
    }
}
