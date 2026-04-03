package com.kardexmobil.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.mvc.View;
import com.kardexmobil.KardexMobil;
import com.kardexmobil.DrawerManager; // Importado para reconstruir el menú
import dao.UsuarioDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Usuario;
import utils.Sesion;

public class LoginPresenter {

    @FXML private View loginView; 
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void initialize() {
        loginView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppManager appManager = AppManager.getInstance();
                if (appManager != null && appManager.getAppBar() != null) {
                    appManager.getAppBar().setVisible(false); 
                    // Bloqueamos el drawer en el login para evitar saltos sin permiso
                    appManager.getDrawer().setDisable(true);
                }
            }
        });
    }

    @FXML
    private void login() {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (user == null || user.isEmpty() || pass == null || pass.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Ingrese usuario y contraseña");
            return;
        }

        Usuario u = usuarioDAO.login(user, pass);
        
        if (u == null) {
            mostrarAlerta("Error", "Usuario o contraseña incorrectos");
            return;
        }

        // 1. Guardar la sesión
        Sesion.setUsuario(u);

        AppManager app = AppManager.getInstance();
        if (app != null) {
            // 2. Habilitar y Reconstruir el Drawer basado en el nuevo rol
            app.getDrawer().setDisable(false);
            DrawerManager.buildDrawer(app);

            // 3. Redirección por Rol
            if (u.getRol().equals("SUPER_ADMIN")) {
                app.switchView(KardexMobil.SUPERADMIN_VIEW); 
            } else if (u.getRol().equals("ADMIN")) {
                app.switchView(KardexMobil.MENU_VIEW); 
            } else {
                mostrarAlerta("Error", "Rol no permitido en esta versión móvil");
            }
        }
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}