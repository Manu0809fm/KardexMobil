package dao;

import model.Producto;
import utils.DBConnection;
import utils.Sesion;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listarPorSucursal() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.*, "
                + "(SELECT MIN(fecha_vencimiento) FROM lotes l WHERE l.producto_id = p.id AND l.cantidad_actual > 0) as prox_venc "
                + "FROM producto p WHERE p.sucursal_id = ?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(Sesion.getUsuario().getSucursal()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Producto p = mapearProducto(rs);
                Date fv = rs.getDate("prox_venc");
                if (fv != null) {
                    p.setProximoVencimiento(fv.toLocalDate());
                }
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

  public void insertarConLote(Producto p, String codigoLote, LocalDate vencimiento) throws SQLException {
String sqlProd = "INSERT INTO producto (nombre, tipo, UndM, precio, sucursal_id, estado, stock, maneja_stock, stock_inicial) VALUES (?,?,?,?,?,?,?,?,?)";
String sqlLote = "INSERT INTO lotes (producto_id, codigo_lote, cantidad_inicial, cantidad_actual, fecha_vencimiento, sucursal_id) VALUES (?,?,?,?,?,?)";
    
    // SQL con todos los campos necesarios para que aparezca en el historial
    String sqlKardex = "INSERT INTO movimiento_inventario (sucursal_id, producto_id, tipo_movimiento, "
            + "cantidad_movida, saldo_cantidad, saldo_valor, costo_unitario, precio_total, referencia_documento, fecha, colaborador) "
            + "VALUES (?, ?, 'ENTRADA', ?, ?, 0, 0, 0, 'REGISTRO INICIAL', CONVERT_TZ(NOW(), @@session.time_zone, '-05:00'), ?)";

    try (Connection cn = DBConnection.getConnection()) {
        cn.setAutoCommit(false);
        try {
            int idProducto = 0;
            // 1. Insertar Producto
            try (PreparedStatement psP = cn.prepareStatement(sqlProd, Statement.RETURN_GENERATED_KEYS)) {
        psP.setString(1, p.getNombre());
        psP.setString(2, p.getTipo());
        psP.setString(3, p.getUnidadMedida()); // Nuevo parámetro
        psP.setBigDecimal(4, p.getPrecio());
        psP.setInt(5, p.getSucursalId());
                psP.setString(6, p.getEstado() != null ? p.getEstado() : "ACTIVO");
                psP.setInt(7, p.getStock());
                psP.setInt(8, p.isManejaStock() ? 1 : 0);
                psP.setInt(9, p.getStock());
                psP.executeUpdate();
                
                ResultSet rs = psP.getGeneratedKeys();
                if (rs.next()) idProducto = rs.getInt(1);
            }

            // 2. Insertar Lote
            try (PreparedStatement psL = cn.prepareStatement(sqlLote)) {
                psL.setInt(1, idProducto);
                psL.setString(2, codigoLote);
                psL.setBigDecimal(3, new BigDecimal(p.getStock()));
                psL.setBigDecimal(4, new BigDecimal(p.getStock()));
                if (vencimiento != null) {
                    psL.setDate(5, java.sql.Date.valueOf(vencimiento));
                } else {
                    psL.setNull(5, java.sql.Types.DATE);
                }
                psL.setInt(6, p.getSucursalId());
                psL.executeUpdate();
            }

            // 3. Insertar Kardex (Solo si hay stock inicial)
            if (p.getStock() >= 0) {
                try (PreparedStatement psK = cn.prepareStatement(sqlKardex)) {
                    psK.setInt(1, p.getSucursalId());                // sucursal_id
                    psK.setInt(2, idProducto);                      // producto_id
                    psK.setBigDecimal(3, new BigDecimal(p.getStock())); // cantidad_movida
                    psK.setBigDecimal(4, new BigDecimal(p.getStock())); // saldo_cantidad
                    psK.setString(5, Sesion.getUsuario().getUsuario()); // colaborador
                    psK.executeUpdate();
                }
            }
            cn.commit();
        } catch (SQLException e) {
            cn.rollback();
            throw e;
        }
    }
}

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET nombre=?, tipo=?, precio=?, estado=?, maneja_stock=? WHERE id=?";
        try (Connection cn = DBConnection.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTipo());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setString(4, p.getEstado());
            ps.setInt(5, p.isManejaStock() ? 1 : 0);
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    public Producto buscarPorId(int id) {
        String sql = "SELECT * FROM producto WHERE id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearProducto(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

   public void eliminar(int id) throws Exception {
    String sql = "DELETE FROM producto WHERE id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, id);
        ps.executeUpdate();
    } catch (SQLException e) {
        throw new Exception("Error al eliminar: " + e.getMessage());
    }
}
    public void registrarEntradaConLote(int idProducto, BigDecimal cantidad, String lote, java.time.LocalDate vencimiento, String colaborador) throws SQLException {
        int idSucursal = Integer.parseInt(utils.Sesion.getUsuario().getSucursal());
        String sqlLote = "INSERT INTO lotes (producto_id, codigo_lote, fecha_vencimiento, cantidad_inicial, cantidad_actual, sucursal_id, estado) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVO')";

        String sqlProd = "UPDATE producto SET "
                + "stock_inicial = IF(stock <= 0, ?, stock_inicial), "
                + "stock = stock + ? "
                + "WHERE id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlLote)) {
                    ps.setInt(1, idProducto);
                    ps.setString(2, lote);

                    if (vencimiento != null) {
                        ps.setDate(3, java.sql.Date.valueOf(vencimiento));
                    } else {
                        ps.setNull(3, java.sql.Types.DATE);
                    }
                    // --------------------------------------

                    ps.setBigDecimal(4, cantidad);
                    ps.setBigDecimal(5, cantidad);
                    ps.setInt(6, idSucursal);
                    ps.executeUpdate();
                }

                try (PreparedStatement psP = con.prepareStatement(sqlProd)) {
                    psP.setBigDecimal(1, cantidad);
                    psP.setBigDecimal(2, cantidad);
                    psP.setInt(3, idProducto);
                    psP.executeUpdate();
                }

                BigDecimal nuevoSaldo = BigDecimal.ZERO;
                try (PreparedStatement psStock = con.prepareStatement("SELECT stock FROM producto WHERE id = ?")) {
                    psStock.setInt(1, idProducto);
                    ResultSet rsS = psStock.executeQuery();
                    if (rsS.next()) {
                        nuevoSaldo = rsS.getBigDecimal("stock");
                    }
                }

                model.MovimientoInventario mov = new model.MovimientoInventario();
                mov.setSucursalId(idSucursal);
                mov.setProductoId(idProducto);
                mov.setTipoMovimiento("ENTRADA");
                mov.setCantidadMovida(cantidad);
                mov.setReferenciaDocumento("INGRESO LOTE:" + lote);
                mov.setColaborador(colaborador);
                mov.setCostoUnitario(BigDecimal.ZERO);
                mov.setPrecioTotal(BigDecimal.ZERO);
                mov.setSaldoCantidad(nuevoSaldo);
                mov.setSaldoValor(BigDecimal.ZERO);

                new MovimientoInventarioDAO().registrarConConexion(con, mov);

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void actualizarStockProducto(int idProducto, BigDecimal cantidad, String colaborador) throws SQLException {
    int idSucursal = Integer.parseInt(utils.Sesion.getUsuario().getSucursal());

    String sqlUpdate = "UPDATE producto SET "
            + "stock_inicial = IF(stock <= 0, ABS(?), stock_inicial), "
            + "stock = stock + ? "
            + "WHERE id = ?";

    // SQL con fecha y colaborador al final
    String sqlKardex = "INSERT INTO movimiento_inventario (sucursal_id, producto_id, tipo_movimiento, "
            + "cantidad_movida, saldo_cantidad, referencia_documento, costo_unitario, precio_total, saldo_valor, fecha, colaborador) "
            + "VALUES (?, ?, ?, ?, ?, ?, 0, 0, 0, CONVERT_TZ(NOW(), @@session.time_zone, '-05:00'), ?)";

    try (Connection con = DBConnection.getConnection()) {
        con.setAutoCommit(false);
        try {
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setBigDecimal(1, cantidad);
                ps.setBigDecimal(2, cantidad);
                ps.setInt(3, idProducto);
                ps.executeUpdate();
            }

            BigDecimal nuevoSaldo = BigDecimal.ZERO;
            try (PreparedStatement psSel = con.prepareStatement("SELECT stock FROM producto WHERE id = ?")) {
                psSel.setInt(1, idProducto);
                ResultSet rs = psSel.executeQuery();
                if (rs.next()) nuevoSaldo = rs.getBigDecimal("stock");
            }

            // Registro en Kardex (7 parámetros ?)
            try (PreparedStatement psK = con.prepareStatement(sqlKardex)) {
                psK.setInt(1, idSucursal);      // ? 1
                psK.setInt(2, idProducto);      // ? 2
                psK.setString(3, cantidad.compareTo(BigDecimal.ZERO) > 0 ? "ENTRADA" : "SALIDA"); // ? 3
                psK.setBigDecimal(4, cantidad.abs()); // ? 4
                psK.setBigDecimal(5, nuevoSaldo);     // ? 5
                psK.setString(6, "RETIRO DE INSUMOS"); // ? 6
                psK.setString(7, colaborador);        // ? 7
                psK.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        }
    }
}
    public void actualizarCompleto(Producto p, String lote, LocalDate vencimiento, String colaborador) throws SQLException {
String sqlProd = "UPDATE producto SET nombre=?, tipo=?, estado=?, stock=?, UndM=? WHERE id=?";
String sqlLote = "UPDATE lotes SET codigo_lote=?, fecha_vencimiento=?, cantidad_actual=? " +
                     "WHERE producto_id=? ORDER BY id DESC LIMIT 1";
    
    // SQL para el Kardex
    String sqlKardex = "INSERT INTO movimiento_inventario (sucursal_id, producto_id, tipo_movimiento, "
            + "cantidad_movida, saldo_cantidad, referencia_documento, fecha, colaborador, costo_unitario, precio_total, saldo_valor) "
            + "VALUES (?, ?, ?, ?, ?, ?, CONVERT_TZ(NOW(), @@session.time_zone, '-05:00'), ?, 0, 0, 0)";

    try (Connection cn = DBConnection.getConnection()) {
        cn.setAutoCommit(false);
        try {
            // A. Obtener stock anterior para calcular el diferencial
            int stockAnterior = 0;
            try (PreparedStatement psSel = cn.prepareStatement("SELECT stock FROM producto WHERE id = ?")) {
                psSel.setInt(1, p.getId());
                ResultSet rs = psSel.executeQuery();
                if (rs.next()) stockAnterior = rs.getInt("stock");
            }

            // B. Actualizar Producto
            try (PreparedStatement psP = cn.prepareStatement(sqlProd)) {
        psP.setString(1, p.getNombre());
        psP.setString(2, p.getTipo());
        psP.setString(3, p.getEstado());
        psP.setInt(4, p.getStock());
        psP.setString(5, p.getUnidadMedida()); // Coincide con la columna UndM
        psP.setInt(6, p.getId());
        psP.executeUpdate();
            }

            // C. Actualizar Lote más reciente
            try (PreparedStatement psL = cn.prepareStatement(sqlLote)) {
                psL.setString(1, lote);
                if (vencimiento != null) psL.setDate(2, java.sql.Date.valueOf(vencimiento));
                else psL.setNull(2, java.sql.Types.DATE);
                psL.setInt(3, p.getStock());
                psL.setInt(4, p.getId());
                psL.executeUpdate();
            }

            // D. Registrar Ajuste en Kardex (Solo si el stock cambió)
            int diferencial = p.getStock() - stockAnterior;
            if (diferencial != 0) {
                try (PreparedStatement psK = cn.prepareStatement(sqlKardex)) {
                    psK.setInt(1, p.getSucursalId());
                    psK.setInt(2, p.getId());
                    psK.setString(3, diferencial > 0 ? "ENTRADA" : "SALIDA");
                    psK.setBigDecimal(4, new java.math.BigDecimal(Math.abs(diferencial)));
                    psK.setBigDecimal(5, new java.math.BigDecimal(p.getStock()));
                    psK.setString(6, "AJUSTE MANUAL DE INVENTARIO");
                    psK.setString(7, colaborador);
                    psK.executeUpdate();
                }
            }

            cn.commit();
        } catch (SQLException e) {
            cn.rollback();
            throw e;
        }
    }
}
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setTipo(rs.getString("tipo"));
        p.setUnidadMedida(rs.getString("UndM"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setSucursalId(rs.getInt("sucursal_id"));
        p.setEstado(rs.getString("estado"));
        p.setStock(rs.getInt("stock"));
        p.setManejaStock(rs.getInt("maneja_stock") == 1);
        int si = rs.getInt("stock_inicial");
        p.setStockInicial(rs.wasNull() ? p.getStock() : si);
        return p;
    }
}
