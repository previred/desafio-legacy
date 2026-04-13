package cl.maraneda.previred.dto;

import java.util.List;

public class OutputDTO {
    private String msg;
    private List<UsuarioDTO> usuarios;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<UsuarioDTO> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioDTO> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public String toString(){
        return String.format(
            "{\"msg\":\"%s\"%s}",
            msg,
            usuarios != null && !usuarios.isEmpty() ? ", \"usuarios\":" + UsuarioDTO.toString(usuarios) : "");
    }
}
