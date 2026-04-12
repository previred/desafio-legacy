package cl.previred.desafio.repository;

import cl.previred.desafio.exception.RepositoryException;
import cl.previred.desafio.model.Empleado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para acceso a datos de empleados usando JDBC puro.
 *
 * <p>Esta clase implementa el patron Repository para abstraer el acceso
 * a la base de datos. Utiliza:</p>
 * <ul>
 *   <li>DataSource de Spring para obtener conexiones</li>
 *   <li>PreparedStatement para prevencion de SQL Injection</li>
 *   <li>Try-with-resources para manejo correcto de recursos</li>
 *   <li>Transacciones manuales con commit/rollback</li>
 * </ul>
 *
 * <p>La tabla esperada en base de datos es {@code empleados} con las columnas:
 * id, nombre, apellido, rut, cargo, salario, bono, descuentos.</p>
 *
 * @see DataSource
 * @see Empleado
 * @since 1.0
 */
@Repository
public class EmpleadoRepository implements EmpleadoRepositoryPort {

    /** Logger para trazabilidad de operaciones SQL. */
    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoRepository.class);

    /** Fuente de datos para obtencion de conexiones JDBC. */
    private final DataSource dataSource;

    /**
     * Constructor con dependencia inyectada.
     *
     * @param dataSource fuente de datos configurada en Spring
     */
    public EmpleadoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Recupera todos los empleados de la base de datos.
     *
     * @return lista de empleados, nunca null (puede estar vacia)
     * @throws RepositoryException si ocurre un error al acceder a la base de datos
     */
    public List<Empleado> findAll() {
        LOG.debug("Ejecutando query: SELECT todos los empleados");
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados";
        List<Empleado> empleados = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                empleados.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener empleados", e);
        }
        return empleados;
    }

    /**
     * Busca un empleado por su identificador unico.
     *
     * @param id identificador del empleado
     * @return el empleado encontrado, o null si no existe
     * @throws RepositoryException si ocurre un error al acceder a la base de datos
     */
    public Empleado findById(Long id) {
        LOG.debug("Ejecutando query: SELECT empleado por id={}", id);
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario, bono, descuentos FROM empleados WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener empleado", e);
        }
        return null;
    }

    /**
     * Verifica si existe un empleado con el RUT especificado.
     *
     * @param rut RUT del empleado a verificar
     * @return true si existe un empleado con ese RUT, false en caso contrario
     * @throws RepositoryException si ocurre un error al acceder a la base de datos
     */
    public boolean existsByRut(String rut) {
        LOG.debug("Ejecutando query: COUNT empleado por rut={}", rut);
        String sql = "SELECT COUNT(*) FROM empleados WHERE rut = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al verificar rut", e);
        }
        return false;
    }

    /**
     * Guarda o actualiza un empleado en la base de datos.
     *
     * <p>Si el empleado tiene id null, se realiza un INSERT.
     * Si el empleado tiene id no null, se realiza un UPDATE.</p>
     *
     * @param empleado empleado a guardar
     * @return el empleado con el id asignado (si fue insertado)
     * @throws RepositoryException si ocurre un error al acceder a la base de datos
     */
    public Empleado save(Empleado empleado) {
        try (Connection conn = dataSource.getConnection()) {
            if (empleado.getId() == null) {
                LOG.debug("Ejecutando INSERT para empleado con rut={}", empleado.getRut());
                return insertEmpleado(conn, empleado);
            } else {
                LOG.debug("Ejecutando UPDATE para empleado con id={}", empleado.getId());
                return updateEmpleado(conn, empleado);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar empleado", e);
        }
    }

    /**
     * Inserta un nuevo empleado en la base de datos.
     *
     * @param conn     conexion activa a la base de datos
     * @param empleado empleado a insertar
     * @return el empleado con el id asignado
     * @throws SQLException si ocurre un error al ejecutar el INSERT
     */
    private Empleado insertEmpleado(Connection conn, Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleados (nombre, apellido, rut, cargo, salario, bono, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)";
        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalario());
            ps.setBigDecimal(6, empleado.getBono() != null ? empleado.getBono() : BigDecimal.ZERO);
            ps.setBigDecimal(7, empleado.getDescuentos() != null ? empleado.getDescuentos() : BigDecimal.ZERO);
            ps.executeUpdate();
            conn.commit();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    empleado.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
        return empleado;
    }

    /**
     * Actualiza un empleado existente en la base de datos.
     *
     * @param conn     conexion activa a la base de datos
     * @param empleado empleado con datos actualizados
     * @return el empleado actualizado
     * @throws SQLException si ocurre un error al ejecutar el UPDATE
     */
    private Empleado updateEmpleado(Connection conn, Empleado empleado) throws SQLException {
        String sql = "UPDATE empleados SET nombre = ?, apellido = ?, rut = ?, cargo = ?, salario = ?, bono = ?, descuentos = ? WHERE id = ?";
        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getApellido());
            ps.setString(3, empleado.getRut());
            ps.setString(4, empleado.getCargo());
            ps.setBigDecimal(5, empleado.getSalario());
            ps.setBigDecimal(6, empleado.getBono());
            ps.setBigDecimal(7, empleado.getDescuentos());
            ps.setLong(8, empleado.getId());
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
        return empleado;
    }

    /**
     * Elimina un empleado por su identificador.
     *
     * @param id identificador del empleado a eliminar
     * @return true si se elimino el empleado, false si no se encontro
     * @throws RepositoryException si ocurre un error al acceder a la base de datos
     */
    public boolean deleteById(Long id) {
        LOG.debug("Ejecutando DELETE para empleado con id={}", id);
        String sql = "DELETE FROM empleados WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar empleado", e);
        }
    }

    /**
     * Mapea una fila del ResultSet a un objeto Empleado.
     *
     * @param rs resultSet posicionado en una fila valida
     * @return objeto Empleado con datos de la fila actual
     * @throws SQLException si ocurre un error al leer los datos
     */
    private Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado();
        emp.setId(rs.getLong("id"));
        emp.setNombre(rs.getString("nombre"));
        emp.setApellido(rs.getString("apellido"));
        emp.setRut(rs.getString("rut"));
        emp.setCargo(rs.getString("cargo"));
        emp.setSalario(rs.getBigDecimal("salario"));
        emp.setBono(rs.getBigDecimal("bono"));
        emp.setDescuentos(rs.getBigDecimal("descuentos"));
        return emp;
    }
}
