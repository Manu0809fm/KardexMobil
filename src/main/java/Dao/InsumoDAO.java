package dao;

import model.Insumo;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import utils.Sesion;

public class InsumoDAO {

    public void insertar(Insumo insumo) {
        String sqlInsumo = "INSERT INTO insumo (nombre, unidad) VALUES (?, ?)";
        String sqlInventario = "INSERT INTO inventario (sucursal_id, insumo_id, cantidad, cantidad_inicial) VALUES (?, ?, ?, ?)";
        String sqlLote = "INSERT INTO lotes (insumo_id, codigo_lote, cantidad_inicial, cantidad_actual, fecha_vencimiento, sucursal_id, estado) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVO')";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            int sucursalId = Integer.parseInt(Sesion.getUsuario().getSucursal());
            PreparedStatement ps = con.prepareStatement(sqlInsumo, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getUnidad());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int insumoId = rs.getInt(1);        
            PreparedStatement psInv = con.prepareStatement(sqlInventario);
            psInv.setInt(1, sucursalId);
            psInv.setInt(2, insumoId);
            psInv.setDouble(3, insumo.getStockInicial());
            psInv.setDouble(4, insumo.getStockInicial());
            psInv.executeUpdate();
            PreparedStatement psLote = con.prepareStatement(sqlLote);
            psLote.setInt(1, insumoId);
            psLote.setString(2, "LOTE-INI-" + insumoId); 
            psLote.setDouble(3, insumo.getStockInicial());
            psLote.setDouble(4, insumo.getStockInicial());
            psLote.setDate(5, Date.valueOf(insumo.getFechaVencimiento()));
            psLote.setInt(6, sucursalId);
            psLote.executeUpdate();
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   public List<Insumo> listar() {
    List<Insumo> lista = new ArrayList<>();
    // SQL optimizado para traer el stock y la fecha del ÚLTIMO lote registrado
    String sql = "    SELECT\n" + "        ins.id,\n" + "        ins.nombre,\n" + "        ins.unidad,\n" + "        inv.cantidad AS stock_actual,\n" + "        (SELECT l.fecha_vencimiento\n" + "         FROM lotes l\n" + "         WHERE l.insumo_id = ins.id\n" + "         ORDER BY l.id DESC LIMIT 1) AS fecha_vencimiento\n" + "    FROM insumo ins\n" + "    JOIN inventario inv ON inv.insumo_id = ins.id\n" + "    WHERE inv.sucursal_id = ?\n" + "    ORDER BY ins.nombre\n";

    try (Connection cn = DBConnection.getConnection(); 
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setInt(1, Integer.parseInt(Sesion.getUsuario().getSucursal()));
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Insumo i = new Insumo(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("unidad")
            );
            i.setStockInicial(rs.getDouble("stock_actual"));

            Date fechaSql = rs.getDate("fecha_vencimiento");
            if (fechaSql != null) {
                i.setFechaVencimiento(fechaSql.toLocalDate());
            }

            lista.add(i);
        }
    } catch (Exception e) {
        System.err.println("Error al listar insumos: " + e.getMessage());
        e.printStackTrace();
    }
    return lista;
}
    public void insertarConLote(Insumo i, String codigoLote, java.time.LocalDate vencimiento, double cantidad) throws SQLException {
    String sqlInsumo = "INSERT INTO insumo (nombre, unidad) VALUES (?, ?)";
    String sqlInventario = "INSERT INTO inventario (sucursal_id, insumo_id, cantidad, cantidad_inicial) VALUES (?, ?, ?, ?)";
    String sqlLote = "INSERT INTO lotes (insumo_id, codigo_lote, cantidad_inicial, cantidad_actual, fecha_vencimiento, sucursal_id, estado) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVO')";
    String sqlKardex = "INSERT INTO movimiento_inventario (insumo_id, sucursal_id, tipo_movimiento, cantidad_movida, saldo_cantidad, saldo_valor, referencia_documento, fecha) "
                     + "VALUES (?, ?, 'ENTRADA', ?, ?, ?, ?, CONVERT_TZ(NOW(), @@session.time_zone, '-05:00'))";
    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);
        try {
            int sucursalId = Integer.parseInt(Sesion.getUsuario().getSucursal());
            int idInsumo;

            // 1. Insertar el Insumo base
            try (PreparedStatement psI = conn.prepareStatement(sqlInsumo, Statement.RETURN_GENERATED_KEYS)) {
                psI.setString(1, i.getNombre());
                psI.setString(2, i.getUnidad());
                psI.executeUpdate();
                ResultSet rs = psI.getGeneratedKeys();
                if (rs.next()) {
                    idInsumo = rs.getInt(1);
                } else {
                    throw new SQLException("Error al obtener ID del EPPs.");
                }
            }

            // 2. Insertar en Inventario (Stock general)
            try (PreparedStatement psInv = conn.prepareStatement(sqlInventario)) {
                psInv.setInt(1, sucursalId);
                psInv.setInt(2, idInsumo);
                psInv.setDouble(3, cantidad);
                psInv.setDouble(4, cantidad);
                psInv.executeUpdate();
            }

            // 3. Insertar el Lote (Aquí manejamos la fecha opcional)
            try (PreparedStatement psL = conn.prepareStatement(sqlLote)) {
                psL.setInt(1, idInsumo);
                psL.setString(2, (codigoLote == null || codigoLote.isEmpty()) ? "N/A" : codigoLote);
                psL.setDouble(3, cantidad);
                psL.setDouble(4, cantidad);
                
                // Si vencimiento es null, enviamos NULL a la BD sin romper el código
                if (vencimiento != null) {
                    psL.setDate(5, java.sql.Date.valueOf(vencimiento));
                } else {
                    psL.setNull(5, java.sql.Types.DATE);
                }
                
                psL.setInt(6, sucursalId);
                psL.executeUpdate();
            }

            // 4. Registrar en el Kardex (Historial)
            try (PreparedStatement psK = conn.prepareStatement(sqlKardex)) {
                psK.setInt(1, idInsumo);                 
                psK.setInt(2, sucursalId);              
                psK.setDouble(3, cantidad);              
                psK.setDouble(4, cantidad);              
                psK.setDouble(5, 0.0);                   
                psK.setString(6, "CREACION");            
                psK.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }
}

    public boolean actualizar(Insumo i) {
    String sql = "UPDATE insumo SET nombre = ?, unidad = ? WHERE id = ?";
    try (Connection con = DBConnection.getConnection(); 
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, i.getNombre());
        ps.setString(2, i.getUnidad());
        ps.setInt(3, i.getId());
        
        int filasAfectadas = ps.executeUpdate();
        return filasAfectadas > 0; // Devuelve true si se actualizó al menos una fila
        
    } catch (Exception e) {
        e.printStackTrace();
        return false; // Devuelve false si hubo un error de SQL
    }
}

   public boolean eliminar(int id) {
    // Solo necesitamos borrar el registro padre. 
    // La base de datos (TiDB) borrará el resto por nosotros.
    String sql = "DELETE FROM insumo WHERE id = ?";

    try (Connection con = DBConnection.getConnection(); 
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, id);
        int filasAfectadas = ps.executeUpdate();
        
        return filasAfectadas > 0;

    } catch (SQLException e) {
        System.err.println("Error al eliminar EPPs: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
   public void actualizarCompleto(Insumo i, String lote, java.time.LocalDate vencimiento, double nuevoStock) throws SQLException {
    String sqlInsumo = "UPDATE insumo SET nombre = ?, unidad = ? WHERE id = ?";
    String sqlInventario = "UPDATE inventario SET cantidad = ? WHERE insumo_id = ? AND sucursal_id = ?";
    // Actualiza el lote más reciente
    String sqlLote = "UPDATE lotes SET codigo_lote = ?, fecha_vencimiento = ?, cantidad_actual = ? " +
                     "WHERE insumo_id = ? AND sucursal_id = ? ORDER BY id DESC LIMIT 1";
    
    String sqlKardex = "INSERT INTO movimiento_inventario (insumo_id, sucursal_id, tipo_movimiento, cantidad_movida, saldo_cantidad, saldo_valor, referencia_documento, fecha, colaborador) "
                     + "VALUES (?, ?, ?, ?, ?, 0, ?, CONVERT_TZ(NOW(), @@session.time_zone, '-05:00'), ?)";

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);
        try {
            int sucursalId = Integer.parseInt(Sesion.getUsuario().getSucursal());
            
            // 1. Obtener stock actual para calcular la diferencia del Kardex
            double stockAnterior = 0;
            try (PreparedStatement psSel = conn.prepareStatement("SELECT cantidad FROM inventario WHERE insumo_id = ? AND sucursal_id = ?")) {
                psSel.setInt(1, i.getId());
                psSel.setInt(2, sucursalId);
                ResultSet rs = psSel.executeQuery();
                if (rs.next()) stockAnterior = rs.getDouble("cantidad");
            }

            // 2. Actualizar Insumo
            try (PreparedStatement psI = conn.prepareStatement(sqlInsumo)) {
                psI.setString(1, i.getNombre());
                psI.setString(2, i.getUnidad());
                psI.setInt(3, i.getId());
                psI.executeUpdate();
            }

            // 3. Actualizar Inventario
            try (PreparedStatement psInv = conn.prepareStatement(sqlInventario)) {
                psInv.setDouble(1, nuevoStock);
                psInv.setInt(2, i.getId());
                psInv.setInt(3, sucursalId);
                psInv.executeUpdate();
            }

            // 4. Actualizar Lote
            try (PreparedStatement psL = conn.prepareStatement(sqlLote)) {
                psL.setString(1, lote);
                if (vencimiento != null) psL.setDate(2, java.sql.Date.valueOf(vencimiento));
                else psL.setNull(2, java.sql.Types.DATE);
                psL.setDouble(3, nuevoStock);
                psL.setInt(4, i.getId());
                psL.setInt(5, sucursalId);
                psL.executeUpdate();
            }

            // 5. Registrar en Kardex si hubo cambio de stock
            if (nuevoStock != stockAnterior) {
                double diferencia = nuevoStock - stockAnterior;
                try (PreparedStatement psK = conn.prepareStatement(sqlKardex)) {
                    psK.setInt(1, i.getId());
                    psK.setInt(2, sucursalId);
                    psK.setString(3, diferencia > 0 ? "ENTRADA" : "SALIDA");
                    psK.setDouble(4, Math.abs(diferencia));
                    psK.setDouble(5, nuevoStock);
                    psK.setString(6, "AJUSTE DE INVENTARIO");
                    psK.setString(7, Sesion.getUsuario().getUsuario()); // Colaborador
                    psK.executeUpdate();
                }
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }
}
   // Agrega este método a tu InsumoDAO.java
public void cargarDetallesDesdeInventario(int insumoId, TextField txtStock, TextField txtLote, DatePicker dpVenc) {
    String sql = "SELECT inv.cantidad, l.codigo_lote, l.fecha_vencimiento\n" + "FROM inventario inv\n" + "LEFT JOIN lotes l ON inv.insumo_id = l.insumo_id\n" + "WHERE inv.insumo_id = ? AND inv.sucursal_id = ?\n" + "ORDER BY l.id DESC LIMIT 1\n";
    
    try (Connection cn = DBConnection.getConnection(); 
         PreparedStatement ps = cn.prepareStatement(sql)) {
        
        ps.setInt(1, insumoId);
        ps.setInt(2, Integer.parseInt(Sesion.getUsuario().getSucursal()));
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            txtStock.setText(String.valueOf(rs.getDouble("cantidad")));
            txtLote.setText(rs.getString("codigo_lote") != null ? rs.getString("codigo_lote") : "N/A");
            Date fVenc = rs.getDate("fecha_vencimiento");
            if (fVenc != null) dpVenc.setValue(fVenc.toLocalDate());
            else dpVenc.setValue(null);
        }
    } catch (Exception e) {
        System.err.println("Error al cargar detalles de inventario: " + e.getMessage());
    }
}
}
