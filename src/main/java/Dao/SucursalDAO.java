package dao;

import model.Sucursal;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SucursalDAO {
    public boolean crear(Sucursal s) {
        String sql = "INSERT INTO sucursal (nombre, direccion, activo) VALUES (?, ?, ?)";

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDireccion());
            ps.setInt(3, s.isActivo() ? 1 : 0);
            
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear sucursal: " + e.getMessage());
            return false;
        }
    }
    public List<Sucursal> listar() {
        List<Sucursal> lista = new ArrayList<>();
        String sql = "SELECT * FROM sucursal WHERE activo = 1 ORDER BY nombre ASC";

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearSucursal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar sucursales: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Sucursal s) {
        String sql = "UPDATE sucursal SET nombre = ?, direccion = ?, activo = ? WHERE id = ?";

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, s.getNombre());
            ps.setString(2, s.getDireccion());
            ps.setInt(3, s.isActivo() ? 1 : 0);
            ps.setInt(4, s.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar sucursal: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "UPDATE sucursal SET activo = 0 WHERE id = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar sucursal: " + e.getMessage());
            return false;
        }
    }

    private Sucursal mapearSucursal(ResultSet rs) throws SQLException {
        Sucursal s = new Sucursal();
        s.setId(rs.getInt("id"));
        s.setNombre(rs.getString("nombre"));
        s.setDireccion(rs.getString("direccion"));
        s.setActivo(rs.getInt("activo") == 1);
        return s;
    }
}