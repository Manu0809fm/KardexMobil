package model;

public class Colaborador {

    private int id;
    private String nombre;
    private String documento;
    private String tipoDocumento;
    private String telefono;
    private String direccion;

    public Colaborador() {
    }

    public Colaborador(int id, String nombre, String documento, String tipoDocumento, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Getters y Setters
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
