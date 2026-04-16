package cl.previred.rsanchez.demoprevired.service.impl;

import cl.previred.rsanchez.demoprevired.domain.EmployeeRequest;
import cl.previred.rsanchez.demoprevired.domain.EmployeeResponse;
import cl.previred.rsanchez.demoprevired.entity.Employee;
import cl.previred.rsanchez.demoprevired.mapper.EmployeeMapper;
import cl.previred.rsanchez.demoprevired.repository.EmployeeRepository;
import cl.previred.rsanchez.demoprevired.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository repository;

    @Value("${app.mensajes.salario.minimo}")
    private int SALARIO_MINIMO;
    @Value("${app.mensajes.porcentaje.bonos}")
    private double CALCULO_BONOS;

    public EmployeeServiceImpl() {

    }


    @Override
    public List<EmployeeResponse> getAllEmployees() {
        log.info("[getAllEmployees][Inicio del metodo para obtener todos los empleados]");
        return repository.findAll().stream().map(EmployeeMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Employee saveEmployee(EmployeeRequest employee) throws Exception {
        log.info("[saveEmployee][Inicio del metodo para guardar un nuevo empleado]");
        List<String> erroress = validateSaveEmployee(employee);
        if(erroress.isEmpty()){
            log.info("[saveEmployee][Antes de guardar en la bd]");
            return repository.save(EmployeeMapper.toEntity(employee));
        } else {
            throw new cl.previred.rsanchez.demoprevired.exception.BadRequestException(erroress);
        }
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("[deleteEmployee][Inicio del metodo para eliminar a empleado con id {}",id+"]");
        repository.deleteById(id);
        log.info("[deleteEmployee][Empleado eliminado de la bd]");
    }

    private boolean validateExist(String rut){
        Employee employee = repository.findByRut(rut);
        log.info("Valor employee (validateExist) {}", employee);
        return employee != null;
    }

    private List<String> validateSaveEmployee(EmployeeRequest employee) throws BadRequestException {
        List<String> errores = new ArrayList<>();

        if((employee.getSalario() + employee.getBonos()) < employee.getDescuentos()) {
            log.error("Descuento excedeen el salario mas los bonos");
            errores.add("Descuentos no pueden superar el salario con los bonos");
        }
        if(employee.getSalario() < SALARIO_MINIMO){
            log.error("Salario no puede ser menor a {}", SALARIO_MINIMO);
            errores.add("Salario no puede ser menor a "+SALARIO_MINIMO);
        }
        if(validateExist(employee.getRut())){
            log.error("Ya existe usuario con esos datos, no es posible agregar un nuevo usuario");
            errores.add("Ya existe un usuario con los datos ingresados");
        }
        if(employee.getSalario() * CALCULO_BONOS < employee.getBonos()) {
            log.error("Bonos no pueden superar el 50% del salario");
            errores.add("Bonos no pueden superar el 50% del salario");
        }
        return errores;
    }
}
