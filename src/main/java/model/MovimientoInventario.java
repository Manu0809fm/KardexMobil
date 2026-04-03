package model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class MovimientoInventario {

    private int id;
    private int sucursalId;
    private Integer productoId;
    private Integer insumoId;
    private String tipoMovimiento;
    private BigDecimal cantidadMovida;
    private BigDecimal costoUnitario;
    private BigDecimal precioTotal;
    private BigDecimal saldoCantidad;
    private BigDecimal saldoValor;
    private String referenciaDocumento;
    private LocalDateTime fecha;
    private String colaborador;
    private String nombreItem;
    private String lote;
    private String vencimiento;

    public MovimientoInventario() {
    }

    public MovimientoInventario(int id, int sucursalId, Integer productoId, Integer insumoId, String tipoMovimiento, BigDecimal cantidadMovida, BigDecimal costoUnitario, BigDecimal precioTotal, BigDecimal saldoCantidad, BigDecimal saldoValor, String referenciaDocumento, LocalDateTime fecha, String colaborador) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.productoId = productoId;
        this.insumoId = insumoId;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidadMovida = cantidadMovida;
        this.costoUnitario = costoUnitario;
        this.precioTotal = precioTotal;
        this.saldoCantidad = saldoCantidad;
        this.saldoValor = saldoValor;
        this.referenciaDocumento = referenciaDocumento;
        this.fecha = fecha;
        this.colaborador = colaborador;
    }

    public MovimientoInventario(int sucursalId, Integer productoId, Integer insumoId,
            String tipoMovimiento, BigDecimal cantidadMovida,
            BigDecimal costoUnitario, String referenciaDocumento) {
        this.sucursalId = sucursalId;
        this.productoId = productoId;
        this.insumoId = insumoId;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidadMovida = cantidadMovida;
        this.costoUnitario = costoUnitario;
        this.referenciaDocumento = referenciaDocumento;
        this.fecha = LocalDateTime.now();
    }

    public String getNombreItem() {
        return nombreItem;
    }

    public void setNombreItem(String nombreItem) {
        this.nombreItem = nombreItem;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(String vencimiento) {
        this.vencimiento = vencimiento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColaborador() {
        return colaborador;
    }

    public void setColaborador(String colaborador) {
        this.colaborador = colaborador;
    }

    public int getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(int sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(Integer insumoId) {
        this.insumoId = insumoId;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getCantidadMovida() {
        return cantidadMovida;
    }

    public void setCantidadMovida(BigDecimal cantidadMovida) {
        this.cantidadMovida = cantidadMovida;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public BigDecimal getSaldoCantidad() {
        return saldoCantidad;
    }

    public void setSaldoCantidad(BigDecimal saldoCantidad) {
        this.saldoCantidad = saldoCantidad;
    }

    public BigDecimal getSaldoValor() {
        return saldoValor;
    }

    public void setSaldoValor(BigDecimal saldoValor) {
        this.saldoValor = saldoValor;
    }

    public String getReferenciaDocumento() {
        return referenciaDocumento;
    }

    public void setReferenciaDocumento(String referenciaDocumento) {
        this.referenciaDocumento = referenciaDocumento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
