package cl.previred.rsanchez.demoprevired.servlet;

import cl.previred.rsanchez.demoprevired.domain.EmployeeRequest;
import cl.previred.rsanchez.demoprevired.domain.EmployeeResponse;
import cl.previred.rsanchez.demoprevired.domain.ErrorResponse;
import cl.previred.rsanchez.demoprevired.entity.Employee;
import cl.previred.rsanchez.demoprevired.exception.BadRequestException;
import cl.previred.rsanchez.demoprevired.service.EmployeeService;
import cl.previred.rsanchez.demoprevired.service.impl.EmployeeServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;


@Slf4j
@WebServlet("/api/empleados/*")
public class EmployeeServlet extends HttpServlet {

    private transient EmployeeService service;
    private transient Gson gson;

    @Override
    public void init() throws ServletException {
        log.info("[init][Inicio deconfiguracion inicial]");
        super.init();
        WebApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext());
        this.service = context.getBean(EmployeeServiceImpl.class);
        this.gson = new Gson();
        log.info("[init][EmployeeServlet inicializado]");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("[doPost][Inicio del metodo]");
        String body = readBody(request);
        EmployeeRequest req = gson.fromJson(body, EmployeeRequest.class);
        log.info("[doPost][Se realizo correctamente la conversion desde json a objeto]");
        try {
            Employee resp = service.saveEmployee(req);
            log.info("[doPost][Se realizo correctamente la insercion en la bd]");
            writeJsonResponse(response, HttpServletResponse.SC_CREATED, resp);
            log.info("[doPost][Termino del metodo]");
        } catch (BadRequestException e) {
            ErrorResponse error = ErrorResponse.builder().mensaje("Error al guardar empleado").errores(e.getErrores()).build();
            writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, error);
        } catch (Exception ex) {
            ErrorResponse error = ErrorResponse.builder().mensaje(ex.getMessage()).build();
            writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, error);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("[doGet][Inicio del metodo]");
        List<EmployeeResponse> employees = service.getAllEmployees();
        log.info("[doGet][Consumio la informacion desde la bd tamaño: {}", employees.size()+"]");
        writeJsonResponse(response, HttpServletResponse.SC_OK, employees);
        log.info("[doGet][Termino el metodo]");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        log.info("[doDelete][Inicio del metodo]");
        Long id = Long.valueOf(request.getPathInfo().substring(1));
        try {
            service.deleteEmployee(id);
            log.info("[doDelete][Termino del metodo]");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeJsonResponse(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(data));
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
