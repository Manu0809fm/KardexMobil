package utils;

import model.Usuario;

public class Sesion {
    private static Usuario usuario;
    

    public static void setUsuario(Usuario u) {
        usuario = u;
    }

    public static Usuario getUsuario() {
        return usuario;
    }
}
