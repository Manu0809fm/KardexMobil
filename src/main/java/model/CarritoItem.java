package model;

public class CarritoItem {

    private String producto;
    private int cantidad;
    private double precio;

    public CarritoItem(String producto, int cantidad, double precio) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public String getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getSubtotal() {
        return cantidad * precio;
    }

    public void incrementar() {
        this.cantidad++;
    }
}
