package cl.hf.previred.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import cl.hf.previred.dao.EmpleadoDao;
import cl.hf.previred.model.EmpleadoDTO;

@Component
public class EmpleadoServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(EmpleadoServlet.class.getName());
    private final Gson gson = new Gson();

    @Autowired
    private EmpleadoDao empleadoDao;

    // GET: Retornar lista de empleados
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<EmpleadoDTO> empleados = empleadoDao.listar();
            String json = gson.toJson(empleados);

            enviarRespuesta(resp, HttpServletResponse.SC_OK, json);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar empleados", e);
            enviarError(resp, "Error interno al obtener empleados.");
        }
    }

    // POST: Agregar nuevo empleado con validaciones
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (BufferedReader reader = req.getReader()) {
            EmpleadoDTO nuevo = gson.fromJson(reader, EmpleadoDTO.class);

            // Usamos una lista para acumular todos los errores encontrados
            List<String> errores = new ArrayList<>();

            // 0. Validar campos obligatorios (nombre, apellido, rut, cargo, salario)
            if (nuevo == null || nuevo.getNombre() == null || nuevo.getNombre().trim().isEmpty()) {
                errores.add("El nombre es obligatorio.");
            }
            if (nuevo == null || nuevo.getApellido() == null || nuevo.getApellido().trim().isEmpty()) {
                errores.add("El apellido es obligatorio.");
            }
            if (nuevo == null || nuevo.getRut() == null || nuevo.getRut().trim().isEmpty()) {
                errores.add("El RUT/DNI es obligatorio.");
            }
            if (nuevo == null || nuevo.getCargo() == null || nuevo.getCargo().trim().isEmpty()) {
                errores.add("El cargo es obligatorio.");
            }

            // 1. Validar Salario Base Mínimo ($400.000)
            if (nuevo.getSalario() == null || nuevo.getSalario() < 400000) {
                errores.add("El salario base no puede ser menor a $400.000.");
            }

            // 2. Validar RUT Duplicado (Solo si el RUT no es nulo)
            if (nuevo.getRut() != null && !nuevo.getRut().trim().isEmpty() && empleadoDao.existeRut(nuevo.getRut())) {
                errores.add("El RUT/DNI ya se encuentra registrado.");
            }

            // 3. Validar Bonos (No pueden superar el 50% del salario base)
            double bono = (nuevo.getBono() != null) ? nuevo.getBono() : 0;
            if (nuevo.getSalario() != null && bono > (nuevo.getSalario() * 0.5)) {
                errores.add("Los bonos no pueden superar el 50% del salario base.");
            }

            // 4. Validar Descuentos (No pueden ser mayores al salario base)
            double descuento = (nuevo.getDescuento() != null) ? nuevo.getDescuento() : 0;
            if (nuevo.getSalario() != null && descuento > nuevo.getSalario()) {
                errores.add("El total de descuentos no puede ser mayor al salario base.");
            }

            // --- Verificación Final de Reglas ---
            if (!errores.isEmpty()) {
                // Retornamos HTTP 400 con el JSON de errores
                Map<String, Object> respuestaError = new HashMap<>();
                respuestaError.put("errores", errores);

                enviarRespuesta(resp, HttpServletResponse.SC_BAD_REQUEST, gson.toJson(respuestaError));
                return;
            }

            // Si pasa todas las validaciones, guardamos
            empleadoDao.guardar(nuevo);
            enviarRespuesta(resp, HttpServletResponse.SC_CREATED, gson.toJson(nuevo));

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al guardar", e);
            enviarError(resp, "Error interno al procesar el registro.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error de formato en petición POST", e);
            enviarError(resp, "Formato de datos inválido o campos faltantes.");
        }
    }

    // DELETE: Eliminar por ID
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("errores", Collections.singletonList("ID de empleado es requerido."));
            enviarRespuesta(resp, HttpServletResponse.SC_BAD_REQUEST, gson.toJson(error));
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            empleadoDao.eliminar(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errores", Collections.singletonList("ID inválido."));
            enviarRespuesta(resp, HttpServletResponse.SC_BAD_REQUEST, gson.toJson(error));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar empleado", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al eliminar el registro.");
            enviarRespuesta(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, gson.toJson(error));
        }
    }

    // Métodos utilitarios para manejo de respuestas
    private void enviarRespuesta(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    private void enviarError(HttpServletResponse resp, String mensaje) throws IOException {
        enviarRespuesta(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                gson.toJson(Collections.singletonMap("error", mensaje)));
    }
}