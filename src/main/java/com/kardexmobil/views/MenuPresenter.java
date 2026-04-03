package com.kardexmobil.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import utils.DBConnection;
import utils.Sesion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// IMPORTANTE: Coincidencia de nombres
import static com.kardexmobil.KardexMobil.*;

public class MenuPresenter {

    @FXML private View menuView; 
    @FXML private Label lblAlertasStock;
    @FXML private Label lblVencimientos;
    @FXML private Label lblAgotados;

    public void initialize() {
        menuView.showingProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                configurarAppBar();
                actualizarDashboard();
            }
        });
    }

     private void configurarAppBar() {
    // CAMBIO: Usar AppManager en lugar de MobileApplication
    AppManager appManager = AppManager.getInstance();
    
    if (appManager != null && appManager.getAppBar() != null) {
        AppBar appBar = appManager.getAppBar();
        
        // 1. Hacer la barra visible (ya que el login la ocultó)
        appBar.setVisible(true); 
        
        // 2. Configurar el ícono del menú lateral (Drawer)
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> appManager.getDrawer().open()));
        
        // 3. Título de la vista
        appBar.setTitleText("Dashboard");
        
        // 4. Limpiar botones anteriores y añadir el botón de REFRESH
        appBar.getActionItems().clear(); 
        appBar.getActionItems().add(MaterialDesignIcon.REFRESH.button(e -> actualizarDashboard()));
    }
}

    private void actualizarDashboard() {
        if (Sesion.getUsuario() == null) return;
        
        String sucursalId = Sesion.getUsuario().getSucursal();
        
        // Consultas SQL
        ejecutarConsultaContador("SELECT COUNT(*) FROM producto WHERE stock <= 10 AND stock > 0 AND sucursal_id = ?", lblAlertasStock, sucursalId);
        ejecutarConsultaContador("SELECT COUNT(*) FROM producto WHERE stock = 0 AND sucursal_id = ?", lblAgotados, sucursalId);
        ejecutarConsultaContador("SELECT COUNT(*) FROM lotes WHERE fecha_vencimiento <= CURRENT_DATE AND cantidad_actual > 0 AND sucursal_id = ?", lblVencimientos, sucursalId);
    }

    private void ejecutarConsultaContador(String sql, Label etiqueta, String idSede) {
        if (etiqueta == null) return;
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, idSede);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) etiqueta.setText(String.valueOf(rs.getInt(1)));
            }
        } catch (Exception e) {
            etiqueta.setText("!");
        }
    }

    @FXML private void abrirInventario() { MobileApplication.getInstance().switchView(INVENTARIO_VIEW); }
    @FXML private void abrirKardex() { MobileApplication.getInstance().switchView(HISTORIAL_VIEW); }
    @FXML private void abrirProductos() { MobileApplication.getInstance().switchView(PRODUCTO_VIEW); }
    @FXML private void abrirInsumos() { MobileApplication.getInstance().switchView(INSUMOS_VIEW); }
    
    @FXML 
    private void cerrarSesion() { 
        Sesion.setUsuario(null);
        MobileApplication.getInstance().switchView(LOGIN_VIEW); 
    }
}