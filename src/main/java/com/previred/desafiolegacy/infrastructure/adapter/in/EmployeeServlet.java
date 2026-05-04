package com.previred.desafiolegacy.infrastructure.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.previred.desafiolegacy.application.dto.CommandResult;
import com.previred.desafiolegacy.application.dto.CreateEmployeeCommand;
import com.previred.desafiolegacy.application.dto.DeleteEmployeeCommand;
import com.previred.desafiolegacy.application.dto.EmployeeView;
import com.previred.desafiolegacy.application.usecause.CreateEmployeeUseCause;
import com.previred.desafiolegacy.application.usecause.DeleteEmployeeUseCause;
import com.previred.desafiolegacy.application.usecause.GetEmployeesUseCause;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/api/empleados/*")
public class EmployeeServlet extends HttpServlet {

    private CreateEmployeeUseCause createEmployeeUseCause;

    private DeleteEmployeeUseCause deleteEmployeeUseCause;

    private GetEmployeesUseCause getEmployeesUseCause;

    @Override
    public void init() throws ServletException {

        this.createEmployeeUseCause = (CreateEmployeeUseCause) getServletContext().getAttribute("createEmployeeUseCause") ;
        this.deleteEmployeeUseCause = (DeleteEmployeeUseCause) getServletContext().getAttribute("deleteEmployeeUseCause") ;
        this.getEmployeesUseCause = (GetEmployeesUseCause) getServletContext().getAttribute("getEmployeesUseCause") ;
        super.init();
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {

            ObjectMapper mapper = new ObjectMapper();
            CreateEmployeeCommand requestBody =
                    mapper.readValue(req.getInputStream(),
                            CreateEmployeeCommand.class);

            CommandResult commandResult = createEmployeeUseCause.execute(requestBody);
            resp.setContentType("application/json");
            setStatusResponse(resp, commandResult);
            mapper.writeValue(resp.getWriter(), commandResult);

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");

        }
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"ID requerido\"}");
                return;
            }

            Long id = Long.parseLong(pathInfo.substring(1)); // /123 -> 123
            DeleteEmployeeCommand command = new DeleteEmployeeCommand(id);

            CommandResult commandResult = deleteEmployeeUseCause.execute(command);
            resp.setContentType("application/json");
            setStatusResponse(resp, commandResult);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getWriter(), commandResult);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID inválido\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<EmployeeView> employees = getEmployeesUseCause.execute();

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getWriter(), employees);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void setStatusResponse(HttpServletResponse resp, CommandResult commandResult) throws IOException {
        switch (commandResult.getStatus()) {
            case SUCCESS:
                resp.setStatus(HttpServletResponse.SC_CREATED);
                break;
            case INVALID_DATA:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
