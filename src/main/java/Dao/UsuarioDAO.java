package dao;

import model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import utils.DBConnection;
import java.util.LinkedHashMap;
import java.util.Map;

public class UsuarioDAO {

    public Usuario login(String user, String pass) {

        String sql = "SELECT * FROM usuario WHERE usuario=? AND password=? AND activo=1";

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("usuario"),
                    rs.getString("rol"),
                    rs.getString("sucursal_id")
                );
            }

        } catch (Exception e) {
            System.out.println("Error login: " + e.getMessage());
        }

        return null;
    }
    public void crearUsuario(Usuario u, String pass, int sucursalId) {
    String sql = "INSERT INTO usuario (usuario, password, rol, sucursal_id) VALUES (?, ?, ?, ?)";
    try (Connection cn = DBConnection.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, u.getUsuario());
        ps.setString(2, pass);
        ps.setString(3, u.getRol());
        if (sucursalId <= 0) {
            ps.setNull(4, java.sql.Types.INTEGER); 
        } else {
            ps.setInt(4, sucursalId);
        }

        ps.executeUpdate();
    } catch (SQLException e) {
        if (e.getErrorCode() == 1062) {
             System.out.println("Error: El usuario ya existe.");
        } else {
             e.printStackTrace();
        }
    }
}
public int contarTotalUsuarios() {
    String sql = "SELECT COUNT(*) FROM usuario";
    try (Connection cn = DBConnection.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
    } catch (Exception e) { e.printStackTrace(); }
    return 0;
}
// Añadir estos métodos a UsuarioDAO.java
public List<Usuario> listarUsuarios() {
    List<Usuario> lista = new ArrayList<>();
    String sql = "SELECT u.id, u.usuario, u.rol, IFNULL(s.nombre, 'N/A') as sucursal_nombre " +
                 "FROM usuario u " +
                 "LEFT JOIN sucursal s ON u.sucursal_id = s.id";
    
    try (Connection cn = DBConnection.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            lista.add(new Usuario(
                rs.getInt("id"), 
                rs.getString("usuario"), 
                rs.getString("rol"), 
                rs.getString("sucursal_nombre")
            ));
        }
    } catch (Exception e) { e.printStackTrace(); }
    return lista;
}
public boolean actualizarUsuario(Usuario u, String pass, int sucursalId) {
    String sql = "UPDATE usuario SET usuario=?, password=?, rol=?, sucursal_id=? WHERE id=?";
    
    try (Connection cn = DBConnection.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        
        ps.setString(1, u.getUsuario());
        ps.setString(2, pass);
        ps.setString(3, u.getRol());
        ps.setInt(4, sucursalId);
        ps.setInt(5, u.getId());
        
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        System.out.println("Error actualizar: " + e.getMessage());
        return false;
    }
}

public boolean eliminarUsuario(int id) {
    String sql = "DELETE FROM usuario WHERE id = ?";
    
    try (Connection cn = DBConnection.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        System.out.println("Error eliminar: " + e.getMessage());
        return false;
    }
}
public boolean existeUsuario(String nombreUsuario) {
    String sql = "SELECT COUNT(*) FROM usuario WHERE usuario = ?";
    try (Connection cn = DBConnection.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setString(1, nombreUsuario);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt(1) > 0;
    } catch (Exception e) { e.printStackTrace(); }
    return false;
}
public Map<String, Integer> obtenerConteoPorRoles() {
    Map<String, Integer> datos = new LinkedHashMap<>();
    String sql = "SELECT rol, COUNT(*) as total FROM usuario GROUP BY rol";

    try (Connection cn = DBConnection.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            datos.put(rs.getString("rol"), rs.getInt("total"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return datos;
}
public int obtenerTotalUsuarios() {
        String sql = "SELECT COUNT(*) FROM usuario";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
public Map<String, Integer> obtenerConteoPorRol() {
        Map<String, Integer> mapa = new HashMap<>();
        String sql = "SELECT rol, COUNT(*) as total FROM usuario GROUP BY rol";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                mapa.put(rs.getString("rol"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mapa;
    }

}
