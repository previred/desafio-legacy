package cl.previred.rsanchez.demoprevired.mapper;

import cl.previred.rsanchez.demoprevired.domain.EmployeeRequest;
import cl.previred.rsanchez.demoprevired.domain.EmployeeResponse;
import cl.previred.rsanchez.demoprevired.entity.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class EmployeeMapperTest {

    @Test
    void toDomainTest(){
        EmployeeResponse expectedResponse = EmployeeResponse.builder().build();
        Employee request = Employee.builder().build();
        var response = EmployeeMapper.toDomain(request);
        assertNotNull(response);
        assertEquals(response, expectedResponse);
    }

    @Test
    void toEntityTest(){
        EmployeeRequest request = EmployeeRequest.builder().build();
        Employee expectedResponse = Employee.builder().build();
        var response = EmployeeMapper.toEntity(request);
        assertNotNull(response);
        assertEquals(response, expectedResponse);
    }
}
