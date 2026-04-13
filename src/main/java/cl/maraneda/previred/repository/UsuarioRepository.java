package cl.maraneda.previred.repository;

import cl.maraneda.previred.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioRepository {
    private final DataSource dataSource;

    @Autowired
    public UsuarioRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public List<Usuario> obtieneUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario, monto FROM usuario";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setRut(rs.getString("rut"));
                u.setCargo(rs.getString("cargo"));
                u.setSalario(rs.getInt("salario"));
                u.setMonto(rs.getInt("monto"));
                usuarios.add(u);
            }
        }
        return usuarios;
    }

    public void guardarUsuario(Usuario u) throws SQLException{
        String sql = """
            INSERT INTO usuario (nombre, apellido, rut, cargo, salario, monto) \s
            VALUES(?, ?, ?, ?, ?, ?)
        """;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, u.getNombre());
            stmt.setString(2, u.getApellido());
            stmt.setString(3, u.getRut());
            stmt.setString(4, u.getCargo());
            stmt.setInt(5, u.getSalario());
            stmt.setInt(6, u.getSalario());
            stmt.executeUpdate();
        }
    }

    public boolean eliminarUsuario(int id) throws SQLException{
        String sql = "DELETE FROM usuario WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            return stmt.executeUpdate() != 0;
        }
    }

    public boolean actualizarMonto(int id, int monto) throws SQLException{
        String sql = "UPDATE usuario SET monto = monto + ? WHERE id = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, monto);
            stmt.setInt(2, id);
            return stmt.executeUpdate() != 0;
        }
    }

    public Usuario obtenerUsuario(int id) throws SQLException{
        String sql = "SELECT id, nombre, apellido, rut, cargo, salario, monto FROM usuario WHERE id = ?";
        Usuario u = null;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setApellido(rs.getString("apellido"));
                    u.setRut(rs.getString("rut"));
                    u.setCargo(rs.getString("cargo"));
                    u.setSalario(rs.getInt("salario"));
                    u.setMonto(rs.getInt("monto"));
                }
            }
            return u;
        }
    }
}
