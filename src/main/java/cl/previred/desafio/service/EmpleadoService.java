package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.repository.EmpleadoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio que encapsula la logica de negocio relacionada con empleados.
 *
 * <p>Esta clase actua como intermediario entre la capa de presentacion
 * (Servlets) y la capa de acceso a datos (Repository). Se encarga de:</p>
 * <ul>
 *   <li>Coordinar la creacion de empleados con validaciones previas</li>
 *   <li>Recuperar empleados por ID o listar todos</li>
 *   <li>Eliminar empleados por ID</li>
 * </ul>
 *
 * <p>Las validaciones de negocio son delegadas a {@link ValidationService},
 * manteniendo el principio de responsabilidad unica.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * EmpleadoService service = new EmpleadoService(repository, validationService);
 * List<Empleado> empleados = service.getAllEmpleados();
 * }</pre>
 *
 * @see ValidationService
 * @see cl.previred.desafio.repository.EmpleadoRepository
 * @since 1.0
 */
@Service
public class EmpleadoService {

    /** Logger para trazabilidad de operaciones. */
    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoService.class);

    /** Repositorio para acceso a datos de empleados. */
    private final EmpleadoRepositoryPort empleadoRepository;

    /** Servicio de validacion de datos de entrada. */
    private final EmpleadoValidator empleadoValidator;

    /**
     * Constructor con dependencias inyectadas.
     *
     * @param empleadoRepository  repositorio de empleados
     * @param validationService   servicio de validacion
     */
    public EmpleadoService(EmpleadoRepositoryPort empleadoRepository, EmpleadoValidator validationService) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoValidator = validationService;
    }

    /**
     * Obtiene todos los empleados registrados en el sistema.
     *
     * @return lista de todos los empleados, nunca null
     */
    public List<Empleado> getAllEmpleados() {
        LOG.debug("Obteniendo todos los empleados");
        return empleadoRepository.findAll();
    }

    /**
     * Obtiene un empleado por su identificador unico.
     *
     * @param id identificador del empleado
     * @return el empleado encontrado, o null si no existe
     */
    public Empleado getEmpleadoById(Long id) {
        LOG.debug("Obteniendo empleado por id: {}", id);
        return empleadoRepository.findById(id);
    }

    /**
     * Crea un nuevo empleado en el sistema.
     *
     * <p>Este metodo valida los datos del request antes de persistir.
     * Si la validacion falla, se lanza {@link cl.previred.desafio.exception.ValidationExceptionList}.</p>
     *
     * <p>Los campos bono y descuentos se inicializan a BigDecimal.ZERO si son null.</p>
     *
     * @param request DTO con los datos del empleado a crear
     * @return el empleado creado con su id asignado
     * @throws cl.previred.desafio.exception.ValidationExceptionList si los datos no son validos
     * @see ValidationService#validate(EmpleadoRequest)
     */
    public Empleado crearEmpleado(EmpleadoRequest request) {
        LOG.debug("Creando empleado con RUT: {}", request.getRut());
        empleadoValidator.validate(request);

        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre());
        empleado.setApellido(request.getApellido());
        empleado.setRut(request.getRut());
        empleado.setCargo(request.getCargo());
        empleado.setSalario(request.getSalario());
        empleado.setBono(request.getBono() != null ? request.getBono() : BigDecimal.ZERO);
        empleado.setDescuentos(request.getDescuentos() != null ? 
            request.getDescuentos() : BigDecimal.ZERO);

        Empleado empleadoCreado = empleadoRepository.save(empleado);
        LOG.info("Empleado creado exitosamente con RUT: {}", request.getRut());
        return empleadoCreado;
    }

    /**
     * Elimina un empleado por su identificador.
     *
     * @param id identificador del empleado a eliminar
     * @return true si el empleado fue eliminado, false si no se encontro
     */
    public boolean eliminarEmpleado(Long id) {
        LOG.debug("Eliminando empleado con id: {}", id);
        return empleadoRepository.deleteById(id);
    }
}
