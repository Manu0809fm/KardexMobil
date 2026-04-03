package dao;

import model.Colaborador;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorDAO {

    public boolean guardar(Colaborador c) {
        Colaborador existente = buscarPorDocumento(c.getDocumento());
        if (existente != null) {
            c.setId(existente.getId());
            return actualizar(c);
        } else {
            return insertar(c) > 0;
        }
    }
    public List<Colaborador> listarTodos() {
        List<Colaborador> lista = new ArrayList<>();
        String sql = "SELECT * FROM colaborador ORDER BY nombre";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearColaborador(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Colaborador obtenerPorId(int id) {
        String sql = "SELECT * FROM colaborador WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearColaborador(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Colaborador buscarPorDocumento(String documento) {
        String sql = "SELECT * FROM colaborador WHERE documento = ?";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearColaborador(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insertar(Colaborador c) {
        String sql = "INSERT INTO colaborador (nombre, documento, telefono, direccion, tipo_documento) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDocumento());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getDireccion());
            ps.setString(5, c.getTipoDocumento()); 

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    c.setId(id);
                    return id;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public boolean actualizar(Colaborador c) {
        String sql = "UPDATE colaborador SET nombre=?, documento=?, telefono=?, direccion=?, tipo_documento=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDocumento());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getDireccion());
            ps.setString(5, c.getTipoDocumento()); 
            ps.setInt(6, c.getId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean eliminar(int id) {
        String sql = "DELETE FROM colaborador WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Colaborador mapearColaborador(ResultSet rs) throws SQLException {
        Colaborador c = new Colaborador();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDocumento(rs.getString("documento"));
        c.setTelefono(rs.getString("telefono"));
        c.setDireccion(rs.getString("direccion"));
        c.setTipoDocumento(rs.getString("tipo_documento")); 
        return c;
    }
    public int contarTotalColaboradores() {
        String sql = "SELECT COUNT(*) FROM colaborador";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}