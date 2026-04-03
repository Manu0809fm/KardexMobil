package dao;

import model.MovimientoInventario;
import utils.DBConnection;
import utils.Sesion;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MovimientoInventarioDAO {

    public void registrar(MovimientoInventario mov) {
        try (Connection con = DBConnection.getConnection()) {
            registrarConConexion(con, mov);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registrarConConexion(Connection con, MovimientoInventario mov) throws SQLException {
        String sql = "INSERT INTO movimiento_inventario (sucursal_id, producto_id, insumo_id, tipo_movimiento, " +
                     "colaborador, cantidad_movida, costo_unitario, precio_total, saldo_cantidad, saldo_valor, " +
                     "referencia_documento, fecha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,CONVERT_TZ(NOW(), @@session.time_zone, '-05:00'))";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mov.getSucursalId());
            if (mov.getProductoId() != null) ps.setInt(2, mov.getProductoId()); else ps.setNull(2, Types.INTEGER);
            if (mov.getInsumoId() != null) ps.setInt(3, mov.getInsumoId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, mov.getTipoMovimiento());
            ps.setString(5, mov.getColaborador());
            ps.setBigDecimal(6, mov.getCantidadMovida());
            ps.setBigDecimal(7, mov.getCostoUnitario());
            ps.setBigDecimal(8, mov.getPrecioTotal());
            ps.setBigDecimal(9, mov.getSaldoCantidad());
            ps.setBigDecimal(10, mov.getSaldoValor());
            ps.setString(11, mov.getReferenciaDocumento());
            ps.executeUpdate();
        }
    }

    public List<MovimientoInventario> listarDetallado() {
        String sql = "SELECT m.*, " +
                     "COALESCE(p.nombre, i.nombre, 'Sin nombre') AS nombre_final, " +
                     "MAX(l.codigo_lote) AS codigo_lote, " +
                     "MAX(l.fecha_vencimiento) AS fecha_vencimiento " +
                     "FROM movimiento_inventario m " +
                     "LEFT JOIN producto p ON m.producto_id = p.id " +
                     "LEFT JOIN insumo i ON m.insumo_id = i.id " +
                     "LEFT JOIN lotes l ON (m.producto_id = l.producto_id OR m.insumo_id = l.insumo_id) " +
                     "WHERE m.sucursal_id = ? " +
                     "GROUP BY m.id, nombre_final " + 
                     "ORDER BY m.fecha DESC";
        return ejecutarConsulta(sql, null, null);
    }

    public List<MovimientoInventario> listarPorRangoFechasDetallado(LocalDate inicio, LocalDate fin) {
        String sql = "SELECT m.*, " +
                     "COALESCE(p.nombre, i.nombre, 'Sin nombre') AS nombre_final, " +
                     "MAX(l.codigo_lote) AS codigo_lote, " +
                     "MAX(l.fecha_vencimiento) AS fecha_vencimiento " +
                     "FROM movimiento_inventario m " +
                     "LEFT JOIN producto p ON m.producto_id = p.id " +
                     "LEFT JOIN insumo i ON m.insumo_id = i.id " +
                     "LEFT JOIN lotes l ON (m.producto_id = l.producto_id OR m.insumo_id = l.insumo_id) " +
                     "WHERE m.sucursal_id = ? AND DATE(m.fecha) BETWEEN ? AND ? " +
                     "GROUP BY m.id, nombre_final " +
                     "ORDER BY m.fecha DESC";
        return ejecutarConsulta(sql, inicio, fin);
    }

    private List<MovimientoInventario> ejecutarConsulta(String sql, LocalDate inicio, LocalDate fin) {
        List<MovimientoInventario> lista = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(Sesion.getUsuario().getSucursal()));
            if (inicio != null && fin != null) {
                ps.setDate(2, java.sql.Date.valueOf(inicio));
                ps.setDate(3, java.sql.Date.valueOf(fin));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearMovimiento(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR EN SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario m = new MovimientoInventario();
        m.setId(rs.getInt("id"));
        m.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        m.setTipoMovimiento(rs.getString("tipo_movimiento"));
        m.setCantidadMovida(rs.getBigDecimal("cantidad_movida"));
        m.setReferenciaDocumento(rs.getString("referencia_documento"));
        m.setColaborador(rs.getString("colaborador") == null ? "SISTEMA" : rs.getString("colaborador"));
        m.setNombreItem(rs.getString("nombre_final")); 
        
        // Manejo de Lote
        String lote = rs.getString("codigo_lote");
        m.setLote(lote == null ? "---" : lote);
        
        // Manejo de Vencimiento
        Date fv = rs.getDate("fecha_vencimiento");
        m.setVencimiento(fv != null ? fv.toString() : "---");
        
        return m;
    }  
    public Map<String, Integer> obtenerResumenMovimientos() {
    Map<String, Integer> mapa = new LinkedHashMap<>(); 
    // CAMBIO: Se usa 'fecha' en lugar de 'fecha_movimiento'
    String sql = "SELECT DATE(fecha) as fecha_dia, COUNT(*) as total " +
                 "FROM movimiento_inventario " +
                 "WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                 "GROUP BY DATE(fecha) " +
                 "ORDER BY fecha_dia ASC";
    
    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        while (rs.next()) {
            mapa.put(rs.getString("fecha_dia"), rs.getInt("total"));
        }
    } catch (SQLException e) {
        System.err.println("Error en obtenerResumenMovimientos: " + e.getMessage());
    }
    return mapa;
}
public int obtenerTotalMovimientos() {
    String sql = "SELECT COUNT(*) FROM movimiento_inventario";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}

}