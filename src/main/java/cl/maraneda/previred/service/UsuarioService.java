package cl.maraneda.previred.service;

import cl.maraneda.previred.converter.Converter;
import cl.maraneda.previred.dto.MontoDTO;
import cl.maraneda.previred.dto.UsuarioDTO;
import cl.maraneda.previred.exceptions.MontoException;
import cl.maraneda.previred.exceptions.UsuarioException;
import cl.maraneda.previred.model.Usuario;
import cl.maraneda.previred.repository.UsuarioRepository;
import cl.maraneda.previred.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    private void validarCampos(Object dto) {
        List<String> excluir = dto instanceof UsuarioDTO ? List.of("id", "monto") : null;
        List<String> faltantes = Util.obtenerCamposFaltantes(dto, excluir);
        if(!faltantes.isEmpty()){
            throw new UsuarioException("Todos los campos son obligatorios. Faltan los siguientes campos: " + String.join(",", faltantes));
        }
    }

    public List<UsuarioDTO> buscarUsuarios() throws SQLException{
        return Converter.convert(UsuarioDTO.class, usuarioRepository.obtieneUsuarios());
    }

    public void insertarUsuario(UsuarioDTO udto) throws SQLException{
        validarCampos(udto);
        if(udto.getSalario() < 400000){
            throw new UsuarioException("El salario debe ser mayor o igual a 400000");
        }
        usuarioRepository.guardarUsuario(Converter.convert(Usuario.class, udto));
    }

    public void eliminarUsuario(int id) throws SQLException{
        if(!usuarioRepository.eliminarUsuario(id)){
            throw new UsuarioException("El usuario no existe");
        }
    }

    public void actualizarMonto(MontoDTO m) throws SQLException{
        validarCampos(m);
        int tipo = m.getTipo();
        int monto = m.getMonto();
        int id = m.getId();
        UsuarioDTO u = obtenerUsuario(id);
        if(monto < 1){
            throw new MontoException("El monto debe ser positivo");
        }
        if(tipo != MontoDTO.BONO && tipo != MontoDTO.DESCUENTO){
            throw new MontoException("Se debe especificar si el monto es actualizado para bono (1) o descuento (-1)");
        }
        if(tipo == MontoDTO.BONO && monto > u.getSalario() / 2) {
            throw new MontoException("El bono no debe superar la mitad del salario");
        }
        if(tipo == MontoDTO.DESCUENTO && monto > u.getSalario()) {
            throw new MontoException("El total de descuentos no debe ser mayor al salario");
        }
        int montoFinal = monto * tipo;
        if(!usuarioRepository.actualizarMonto(id, montoFinal)){
            throw new MontoException("No se pudo actualizar el monto del usuario");
        }
    }

    public UsuarioDTO obtenerUsuario(int id) throws SQLException{
        Usuario u =
            Optional.ofNullable(usuarioRepository.obtenerUsuario(id))
                    .orElseThrow(() ->
                        new UsuarioException("El usuario con la id especificada no existe"));
        return Converter.convert(UsuarioDTO.class, u);
    }
}
