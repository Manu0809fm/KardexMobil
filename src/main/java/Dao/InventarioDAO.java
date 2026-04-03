package dao;

import model.Inventario;
import model.Insumo;
import model.MovimientoInventario;
import utils.DBConnection;
import utils.Sesion;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    private final MovimientoInventarioDAO movDAO = new MovimientoInventarioDAO();

    public void agregarInventario(int idInsumo, BigDecimal cantidadAgregar, String documento, String nombreColaborador) throws SQLException {
        int idSucursal = Integer.parseInt(Sesion.getUsuario().getSucursal());
        String query = "SELECT id, cantidad, cantidad_inicial FROM inventario WHERE insumo_id = ? AND sucursal_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal stockActualBD = BigDecimal.ZERO;
                BigDecimal stockInicialBD = BigDecimal.ZERO;
                Integer idInventario = null;

                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setInt(1, idInsumo);
                    ps.setInt(2, idSucursal);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        idInventario = rs.getInt("id");
                        stockActualBD = rs.getBigDecimal("cantidad");
                        stockInicialBD = rs.getBigDecimal("cantidad_inicial");
                    }
                }

                if (idInventario != null) {
                    BigDecimal nuevoActual = stockActualBD.add(cantidadAgregar);
                    BigDecimal nuevoInicial = (stockActualBD.compareTo(BigDecimal.ZERO) <= 0) ? cantidadAgregar : stockInicialBD;

                    String update = "UPDATE inventario SET cantidad = ?, cantidad_inicial = ? WHERE id = ?";
                    try (PreparedStatement psUpd = con.prepareStatement(update)) {
                        psUpd.setBigDecimal(1, nuevoActual);
                        psUpd.setBigDecimal(2, nuevoInicial);
                        psUpd.setInt(3, idInventario);
                        psUpd.executeUpdate();
                    }
                } else {
                    String insert = "INSERT INTO inventario (sucursal_id, insumo_id, cantidad, cantidad_inicial) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psIns = con.prepareStatement(insert)) {
                        psIns.setInt(1, idSucursal);
                        psIns.setInt(2, idInsumo);
                        psIns.setBigDecimal(3, cantidadAgregar);
                        psIns.setBigDecimal(4, cantidadAgregar);
                        psIns.executeUpdate();
                    }
                }

                MovimientoInventario mov = new MovimientoInventario();
                mov.setSucursalId(idSucursal);
                mov.setInsumoId(idInsumo);
                mov.setTipoMovimiento("ENTRADA");
                mov.setCantidadMovida(cantidadAgregar);
                mov.setSaldoCantidad(stockActualBD.add(cantidadAgregar));
                mov.setReferenciaDocumento(documento);
                mov.setColaborador(nombreColaborador);
                mov.setCostoUnitario(BigDecimal.ZERO);
                mov.setPrecioTotal(BigDecimal.ZERO);
                mov.setSaldoValor(BigDecimal.ZERO);
                movDAO.registrarConConexion(con, mov);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Inventario> listar() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT i.sucursal_id, i.insumo_id, "
                + "SUM(i.cantidad) as cantidad_total, "
                + "SUM(i.cantidad_inicial) as inicial_total, "
                + "ins.nombre, ins.unidad "
                + "FROM inventario i "
                + "INNER JOIN insumo ins ON ins.id = i.insumo_id "
                + "WHERE i.sucursal_id = ? "
                + "GROUP BY i.insumo_id, i.sucursal_id, ins.nombre, ins.unidad";

        try (Connection cn = DBConnection.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(Sesion.getUsuario().getSucursal()));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Insumo insumo = new Insumo(rs.getInt("insumo_id"), rs.getString("nombre"), rs.getString("unidad"));
                Inventario inv = new Inventario();
                inv.setInsumo(insumo);
                inv.setCantidadActual(rs.getBigDecimal("cantidad_total"));
                inv.setCantidadInicial(rs.getBigDecimal("inicial_total"));
                inv.setSucursalId(rs.getInt("sucursal_id"));
                lista.add(inv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean descontar(Connection con, int idInsumo, int idSucursal, BigDecimal cantidadDescontar, String motivo, String nombreColaborador) throws SQLException {
        String sqlSelect = "SELECT cantidad FROM inventario WHERE insumo_id = ? AND sucursal_id = ? FOR UPDATE";
        BigDecimal stockActual = BigDecimal.ZERO;

        try (PreparedStatement ps = con.prepareStatement(sqlSelect)) {
            ps.setInt(1, idInsumo);
            ps.setInt(2, idSucursal);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stockActual = rs.getBigDecimal("cantidad");
            } else {
                return false;
            }
        }

        if (stockActual.compareTo(cantidadDescontar) < 0) {
            return false;
        }

        BigDecimal nuevoActual = stockActual.subtract(cantidadDescontar);

        String sqlUpdate = "UPDATE inventario SET cantidad = ? WHERE insumo_id = ? AND sucursal_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setBigDecimal(1, nuevoActual);
            ps.setInt(2, idInsumo);
            ps.setInt(3, idSucursal);
            ps.executeUpdate();
        }

        MovimientoInventario mov = new MovimientoInventario();
        mov.setSucursalId(idSucursal);
        mov.setInsumoId(idInsumo);
        mov.setTipoMovimiento("SALIDA");
        mov.setCantidadMovida(cantidadDescontar);
        mov.setSaldoCantidad(nuevoActual);
        mov.setReferenciaDocumento(motivo);
        mov.setColaborador(nombreColaborador);
        mov.setCostoUnitario(BigDecimal.ZERO);
        mov.setPrecioTotal(BigDecimal.ZERO);
        mov.setSaldoValor(BigDecimal.ZERO);
        movDAO.registrarConConexion(con, mov);
        return true;
    }

   public void agregarInventarioConLote(int idInsumo, BigDecimal cantidad, String lote, java.time.LocalDate vencimiento, String colaborador) throws SQLException {
    int idSucursal = Integer.parseInt(Sesion.getUsuario().getSucursal());

    String sqlLote = "INSERT INTO lotes (insumo_id, codigo_lote, fecha_vencimiento, cantidad_inicial, cantidad_actual, sucursal_id, estado) VALUES (?, ?, ?, ?, ?, ?, 'ACTIVO')";

    String sqlStock = "INSERT INTO inventario (sucursal_id, insumo_id, cantidad, cantidad_inicial) VALUES (?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "cantidad_inicial = IF(cantidad <= 0, VALUES(cantidad_inicial), cantidad_inicial), "
            + "cantidad = IF(cantidad <= 0, VALUES(cantidad), cantidad + VALUES(cantidad))";

    try (Connection con = DBConnection.getConnection()) {
        con.setAutoCommit(false);
        try {
            // Registro del Lote con validación de fecha nula
            try (PreparedStatement psLote = con.prepareStatement(sqlLote)) {
                psLote.setInt(1, idInsumo);
                psLote.setString(2, lote);
                
                // Si la fecha es null (no seleccionada), enviamos NULL a la DB
                if (vencimiento != null) {
                    psLote.setDate(3, java.sql.Date.valueOf(vencimiento));
                } else {
                    psLote.setNull(3, java.sql.Types.DATE);
                }
                
                psLote.setBigDecimal(4, cantidad);
                psLote.setBigDecimal(5, cantidad);
                psLote.setInt(6, idSucursal);
                psLote.executeUpdate();
            }

            // Actualización de stock principal
            try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                psStock.setInt(1, idSucursal);
                psStock.setInt(2, idInsumo);
                psStock.setBigDecimal(3, cantidad);
                psStock.setBigDecimal(4, cantidad);
                psStock.executeUpdate();
            }

            // Registro del movimiento en el Kardex
            MovimientoInventario mov = new MovimientoInventario();
            mov.setSucursalId(idSucursal);
            mov.setInsumoId(idInsumo);
            mov.setTipoMovimiento("ENTRADA");
            mov.setCantidadMovida(cantidad);
            mov.setReferenciaDocumento("INGRESO LOTE: " + lote);
            mov.setColaborador(colaborador);
            mov.setCostoUnitario(BigDecimal.ZERO);
            mov.setPrecioTotal(BigDecimal.ZERO);
            mov.setSaldoCantidad(cantidad); // Aquí podrías sumar el stock actual si fuera necesario
            mov.setSaldoValor(BigDecimal.ZERO);
            
            movDAO.registrarConConexion(con, mov);

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        }
    }
}
}
