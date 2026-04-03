package model;

import java.time.LocalDate;

public class Insumo {

    private int id;
    private String nombre;
    private String unidad;
    private double stockInicial;
    private LocalDate fechaVencimiento;

    public Insumo() {
    }

    public Insumo(int id, String nombre, String unidad) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
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

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public double getStockInicial() {
        return stockInicial;
    }

    public void setStockInicial(double stockInicial) {
        this.stockInicial = stockInicial;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    @Override
    public String toString() {
        return nombre + " [" + unidad + "]";
    }
}
