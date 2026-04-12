package cl.previred.desafio.repository;

import cl.previred.desafio.model.Empleado;
import java.util.List;

/**
 * Puerto (interface) para el acceso a datos de empleados.
 *
 * <p>Esta interfaz define el contrato que debe cumplir cualquier
 * implementacion de repositorio de empleados. Permite separar
 * la logica de negocio de la logica de acceso a datos y facilita
 * el testing mediante implementaciones mock.</p>
 *
 * <p>La implementacion principal es {@link EmpleadoRepository}
 * que utiliza JDBC puro para el acceso a la base de datos.</p>
 *
 * @see EmpleadoRepository
 * @see Empleado
 * @since 1.0
 */
public interface EmpleadoRepositoryPort {

    /**
     * Recupera todos los empleados de la base de datos.
     *
     * @return lista de empleados, nunca null (puede estar vacia)
     */
    List<Empleado> findAll();

    /**
     * Busca un empleado por su identificador unico.
     *
     * @param id identificador del empleado
     * @return el empleado encontrado, o null si no existe
     */
    Empleado findById(Long id);

    /**
     * Verifica si existe un empleado con el RUT especificado.
     *
     * @param rut RUT del empleado a verificar
     * @return true si existe un empleado con ese RUT, false en caso contrario
     */
    boolean existsByRut(String rut);

    /**
     * Guarda o actualiza un empleado en la base de datos.
     *
     * <p>Si el empleado tiene id null, se realiza un INSERT.
     * Si el empleado tiene id no null, se realiza un UPDATE.</p>
     *
     * @param empleado empleado a guardar
     * @return el empleado con el id asignado (si fue insertado)
     */
    Empleado save(Empleado empleado);

    /**
     * Elimina un empleado por su identificador.
     *
     * @param id identificador del empleado a eliminar
     * @return true si se elimino el empleado, false si no se encontro
     */
    boolean deleteById(Long id);
}