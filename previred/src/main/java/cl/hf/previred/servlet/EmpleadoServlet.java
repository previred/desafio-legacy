package cl.hf.previred.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    
                List<String> errores = Stream.of(
                        validarCampoObligatorio(nuevo, EmpleadoDTO::getNombre, "El nombre es obligatorio."),
                        validarCampoObligatorio(nuevo, EmpleadoDTO::getApellido, "El apellido es obligatorio."),
                        validarCampoObligatorio(nuevo, EmpleadoDTO::getRut, "El RUT/DNI es obligatorio."),
                        validarCampoObligatorio(nuevo, EmpleadoDTO::getCargo, "El cargo es obligatorio."),
                        validarSalario(nuevo),
                        validarRutDuplicado(nuevo),
                        validarBono(nuevo),
                        validarDescuento(nuevo)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    
                if (!errores.isEmpty()) {
                    Map<String, Object> respuestaError = new HashMap<>();
                    respuestaError.put("errores", errores);
                    enviarRespuesta(resp, HttpServletResponse.SC_BAD_REQUEST, gson.toJson(respuestaError));
                    return;
                }
    
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
    
        private String validarCampoObligatorio(EmpleadoDTO empleado, Function<EmpleadoDTO, String> extractor, String mensaje) {
            if (empleado == null) {
                return mensaje;
            }
            String valor = extractor.apply(empleado);
            return valor == null || valor.trim().isEmpty() ? mensaje : null;
        }
    
        private String validarSalario(EmpleadoDTO empleado) {
            if (empleado == null || empleado.getSalario() == null || empleado.getSalario() < 400000) {
                return "El salario base no puede ser menor a $400.000.";
            }
            return null;
        }
    
        private String validarRutDuplicado(EmpleadoDTO empleado) throws SQLException {
            if (empleado == null || empleado.getRut() == null || empleado.getRut().trim().isEmpty()) {
                return null;
            }
            return empleadoDao.existeRut(empleado.getRut()) ? "El RUT/DNI ya se encuentra registrado." : null;
        }
    
        private String validarBono(EmpleadoDTO empleado) {
            if (empleado == null || empleado.getSalario() == null) {
                return null;
            }
            double bono = Optional.ofNullable(empleado.getBono()).orElse(0.0);
            return bono > empleado.getSalario() * 0.5 ? "Los bonos no pueden superar el 50% del salario base." : null;
        }
    
        private String validarDescuento(EmpleadoDTO empleado) {
            if (empleado == null || empleado.getSalario() == null) {
                return null;
            }
            double descuento = Optional.ofNullable(empleado.getDescuento()).orElse(0.0);
            return descuento > empleado.getSalario() ? "El total de descuentos no puede ser mayor al salario base." : null;
        }
    // ...existing code...

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