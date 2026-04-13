package cl.maraneda.previred.controller;

import cl.maraneda.previred.dto.OutputDTO;
import cl.maraneda.previred.dto.UsuarioDTO;
import cl.maraneda.previred.exceptions.PreviredRuntimeException;
import cl.maraneda.previred.mapper.MontoMapper;
import cl.maraneda.previred.mapper.UsuarioMapper;
import cl.maraneda.previred.service.UsuarioService;
import cl.maraneda.previred.utils.Util;
import cl.maraneda.previred.wrapper.CachedBodyHttpServletRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name="usuario", urlPatterns="/api/empleados")
public class UsuarioController extends HttpServlet {

    private final transient UsuarioService usuarioService;
    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException{
        try {
            log.info("Iniciando búsqueda de usuarios");
            OutputDTO outdto = new OutputDTO();
            List<UsuarioDTO> usuarios = usuarioService.buscarUsuarios();
            outdto.setUsuarios(usuarios);
            outdto.setMsg("OK");
            log.info("Encontrados {} usuarios", usuarios.size());
            Util.doResponse(resp, outdto, usuarios.isEmpty() ? HttpServletResponse.SC_NO_CONTENT : HttpServletResponse.SC_OK);
        }catch(SQLException | PreviredRuntimeException e){
            log.error("Error al buscar usuarios", e);
            Util.doErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        log.info("Creando nuevo usuario");
        String body = Util.readBodyFromRequest(req, resp);
        if(body.isBlank()) {
            log.error("Peticion sin cuerpo");
            Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "La petición no tiene cuerpo", resp);
            return;
        }
        try {
            log.info("Petición para crear usuario recibida correctamente");
            OutputDTO outdto = new OutputDTO();
            UsuarioDTO udto = UsuarioMapper.fromJsonString(body);
            usuarioService.insertarUsuario(udto);
            outdto.setMsg("Usuario creado exitosamente");
            Util.doResponse(resp, outdto);
            log.info("Usuario creado correctamente");
        }catch(IllegalArgumentException e){
            log.error("Petición invalida");
            Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e, resp);
        }catch(SQLException e){
            String sqlstate = e.getSQLState();
            if(sqlstate == null){
                log.error("Error SQL sin estado al crear usuario", e);
                Util.doErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, resp);
            }else{
                switch(sqlstate) {
                    case "23505":
                        log.error("Rut ya existente");
                        Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                                "Ya existe un usuario con el rut especificado", resp);
                        break;
                    case "23502":
                        log.error("Campos obligatorios faltantes");
                        Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                                "Todos los campos son obligatorios", resp);
                        break;
                    default:
                        log.error("Error SQL al crear usuario", e);
                        Util.doErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, resp);
                        break;
                }
            }
        }catch(PreviredRuntimeException e){
            log.error("Error al crear usuario", e);
            Util.doErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        log.info("Eliminando usuario");
        String id = req.getParameter("id");
        if(id == null || id.trim().isBlank()){
            log.error("ID de usuario no ingresado");
            Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "El id del usuario es obligatorio", resp);
            return;
        }
        try {
            log.info("ID recibida: {}", id);
            usuarioService.eliminarUsuario(Integer.parseInt(id));
            OutputDTO outdto = new OutputDTO();
            outdto.setMsg("Usuario eliminado exitosamente");
            Util.doResponse(resp, outdto);
            log.info("Usuario eliminado exitosamente");
        } catch (SQLException e) {
            log.error("Error al eliminar usuario: ", e);
            Util.doErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, resp);
        } catch (NumberFormatException _) {
            log.error("ID recibida no numerica");
            Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "El id del usuario debe ser numérico", resp);
        }catch (IllegalArgumentException e) {
            log.error("Peticion invalida");
            Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);
        }
    }

    protected void doPatch(String body, HttpServletResponse resp) throws IOException{
        log.info("Actualizando usuario");
        if(body.isBlank()) {
            log.error("Cuerpo de petición en blanco");
            Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "El monto y el usuario son obligatorios", resp);
            return;
        }
        try {
            log.info("Petición para actualizar monto recibida correctamente");
            usuarioService.actualizarMonto(MontoMapper.fromJsonString(body));
            OutputDTO outdto = new OutputDTO();
            outdto.setMsg("Monto actualizado exitosamente");
            Util.doResponse(resp, outdto);
            log.info("Usuario actualizado correctamente");
        } catch(IllegalArgumentException e){
            log.error("Peticion mal generada");
            Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e, resp);
        } catch (SQLException e) {
            String sqlstate = e.getSQLState();
            if(sqlstate!=null && sqlstate.equals("23502")){
                log.error("Campos obligatorios faltantes en monto");
                Util.doErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                        "Todos los campos son obligatorios", resp);
            }else {
                log.error("Error al actualizar montos de usuario", e);
                Util.doErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, resp);
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpServletRequest wrapped = new CachedBodyHttpServletRequest(req);

        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            String body = Util.readBodyFromRequest(wrapped, resp);
            doPatch(body, resp);
        } else {
            super.service(wrapped, resp);
        }
    }
}
