package model;

public class Usuario {

    private final int id;
    private final String usuario;
    private final String rol;
    private final String sucursalId;

    public Usuario(int id, String usuario, String rol, String sucursalId) {
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
        this.sucursalId = sucursalId;
    }
       public static void setRol(String rol) {
     
    }
    public int getId() { 
        return id; 
    }
    public String getUsuario() { 
        return usuario; 
    }
    public String getRol() { 
        return rol; 
    }
    public String getSucursal() { 
        return sucursalId; 
    }

   

  
}
