package cl.previred.servlet;

import cl.previred.dto.ApiResponse;
import cl.previred.dto.EmpleadoRequest;
import cl.previred.dto.EmpleadoResponse;
import cl.previred.service.EmpleadoService;
import cl.previred.util.AppExceptionHandler;
import cl.previred.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class EmpleadoServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7735369651125851366L;
	private static final Logger log = LoggerFactory.getLogger(EmpleadoServlet.class);
    private final EmpleadoService empleadoService;

    public EmpleadoServlet(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            log.info("GET /api/empleados");
            List<EmpleadoResponse> data = empleadoService.listar();
            JsonUtils.writeJson(resp.getOutputStream(), ApiResponse.ok("Listado obtenido correctamente", data));
        } catch (Exception ex) {
            AppExceptionHandler.handle(ex, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            EmpleadoRequest request = JsonUtils.fromJson(req.getInputStream(), EmpleadoRequest.class);
            log.info("POST /api/empleados rutDni={}", request.getRutDni());
            EmpleadoResponse created = empleadoService.crear(request);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtils.writeJson(resp.getOutputStream(), ApiResponse.ok("Empleado creado correctamente", created));
        } catch (Exception ex) {
            AppExceptionHandler.handle(ex, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new IllegalArgumentException("Debe indicar el id del empleado");
            }
            Long id = Long.valueOf(pathInfo.substring(1));
            log.info("DELETE /api/empleados/{}", id);
            empleadoService.eliminar(id);
            JsonUtils.writeJson(resp.getOutputStream(), ApiResponse.ok("Empleado eliminado correctamente", null));
        } catch (Exception ex) {
            AppExceptionHandler.handle(ex, resp);
        }
    }
}
