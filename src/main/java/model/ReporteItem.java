package model;

public class ReporteItem {

    private String descripcion;
    private int cantidad;
    private double total;

    public ReporteItem(String descripcion, int cantidad, double total) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.total = total;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getTotal() {
        return total;
    }
}
