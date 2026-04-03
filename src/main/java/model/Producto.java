package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Producto {

    private int id;
    private String nombre;
    private BigDecimal precio;
    private int sucursalId;
    private String estado;
    private String tipo;
    private int stock;
    private boolean manejaStock;
    private Integer stockInicial;
    private String UnidadMedida;

    private LocalDate proximoVencimiento;

    public Producto() {
    }

    public Producto(int id, String nombre, BigDecimal precio, int sucursalId,
            String estado, String tipo, int stock, boolean manejaStock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.sucursalId = sucursalId;
        this.estado = estado;
        this.tipo = tipo;
        this.stock = stock;
        this.manejaStock = manejaStock;
    }

    public String getUnidadMedida() {
        return UnidadMedida;
    }

    public void setUnidadMedida(String UnidadMedida) {
        this.UnidadMedida = UnidadMedida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(int sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isManejaStock() {
        return manejaStock;
    }

    public void setManejaStock(boolean manejaStock) {
        this.manejaStock = manejaStock;
    }

    public Integer getStockInicial() {
        return stockInicial;
    }

    public void setStockInicial(Integer stockInicial) {
        this.stockInicial = stockInicial;
    }

    public LocalDate getProximoVencimiento() {
        return proximoVencimiento;
    }

    public void setProximoVencimiento(LocalDate proximoVencimiento) {
        this.proximoVencimiento = proximoVencimiento;
    }

    @Override
    public String toString() {
        return (manejaStock) ? nombre + " (Stock: " + stock + ")" : nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Producto p = (Producto) obj;
        return id == p.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
