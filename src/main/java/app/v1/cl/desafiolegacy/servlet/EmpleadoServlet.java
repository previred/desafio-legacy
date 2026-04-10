package app.v1.cl.desafiolegacy.servlet;

import app.v1.cl.desafiolegacy.repository.EmpleadoRepository;
import app.v1.cl.desafiolegacy.dto.EmpleadoDTO;
import app.v1.cl.desafiolegacy.mapper.EmpleadoMapper;
import app.v1.cl.desafiolegacy.model.Empleado;
import app.v1.cl.desafiolegacy.service.EmpleadoValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/api/empleados")
public class EmpleadoServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoServlet.class);

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoValidationService validationService;
    private final ObjectMapper objectMapper;

    public EmpleadoServlet(EmpleadoRepository empleadoRepository, EmpleadoValidationService validationService) {
        this.empleadoRepository = empleadoRepository;
        this.validationService = validationService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.info("GET /api/empleados - Obteniendo lista de empleados");
        List<Empleado> empleados = empleadoRepository.findAll();
        List<EmpleadoDTO> dtos = EmpleadoMapper.toDTOList(empleados);
        sendJsonResponse(resp, HttpServletResponse.SC_OK, dtos);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.info("POST /api/empleados - Creando nuevo empleado");
        try {
            String body = req.getReader().lines().collect(Collectors.joining());
            EmpleadoDTO dto = objectMapper.readValue(body, EmpleadoDTO.class);
            Empleado entity = EmpleadoMapper.toEntity(dto);

            List<String> errores = validationService.validar(entity);
            if (!errores.isEmpty()) {
                logger.warn("Validación fallida: {}", errores);
                sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                        Map.of("errores", errores));
                return;
            }

            Empleado saved = empleadoRepository.save(entity);
            EmpleadoDTO responseDto = EmpleadoMapper.toDTO(saved);
            logger.info("Empleado creado con ID: {}", saved.getId());
            sendJsonResponse(resp, HttpServletResponse.SC_CREATED, responseDto);

        } catch (Exception e) {
            logger.error("Error al crear empleado", e);
            sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    Map.of("errores", List.of("Error interno del servidor: " + e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        logger.info("DELETE /api/empleados?id={} - Eliminando empleado", idParam);

        if (idParam == null || idParam.isBlank()) {
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("errores", List.of("El parámetro 'id' es obligatorio.")));
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            boolean deleted = empleadoRepository.deleteById(id);

            if (deleted) {
                logger.info("Empleado con ID {} eliminado correctamente", id);
                sendJsonResponse(resp, HttpServletResponse.SC_OK,
                        Map.of("mensaje", "Empleado eliminado correctamente."));
            } else {
                logger.warn("Empleado con ID {} no encontrado", id);
                sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND,
                        Map.of("errores", List.of("Empleado no encontrado.")));
            }
        } catch (NumberFormatException e) {
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST,
                    Map.of("errores", List.of("El ID debe ser un número válido.")));
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(resp.getWriter(), data);
    }
}
