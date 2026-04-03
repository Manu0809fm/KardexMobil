package model;

import java.math.BigDecimal;

public class Inventario {

    private int id;
    private int sucursalId;
    private Insumo insumo;
    private BigDecimal cantidadInicial;
    private BigDecimal cantidadActual;
    private BigDecimal stockMinimo;

    public Inventario() {
    }

    public Inventario(int sucursalId, Insumo insumo, BigDecimal cantidadInicial, BigDecimal stockMinimo) {
        this.sucursalId = sucursalId;
        this.insumo = insumo;
        this.cantidadInicial = cantidadInicial;
        this.cantidadActual = cantidadInicial;
        this.stockMinimo = stockMinimo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(int sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getCantidadInicial() {
        return cantidadInicial;
    }

    public void setCantidadInicial(BigDecimal cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public BigDecimal getCantidadActual() {
        return cantidadActual;
    }

    public void setCantidadActual(BigDecimal cantidadActual) {
        this.cantidadActual = cantidadActual;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getCantidadUsada() {
        if (cantidadInicial == null || cantidadActual == null) {
            return BigDecimal.ZERO;
        }
        return cantidadInicial.subtract(cantidadActual);
    }

    public boolean esStockBajo() {
        return cantidadActual.compareTo(stockMinimo) <= 0;
    }
}
