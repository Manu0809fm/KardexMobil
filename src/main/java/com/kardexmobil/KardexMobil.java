package com.kardexmobil;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.visual.Swatch;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;

import static com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW;

public class KardexMobil extends Application {

    // 1. Definición de constantes para las vistas
    public static final String LOGIN_VIEW      = HOME_VIEW; 
    public static final String MENU_VIEW       = "MenuView";
    public static final String PRODUCTO_VIEW   = "ProductoView";
    public static final String INVENTARIO_VIEW = "InventarioView";
    public static final String INSUMOS_VIEW    = "InsumosView";
    public static final String HISTORIAL_VIEW  = "HistorialView";
    public static final String SUPERADMIN_VIEW = "SuperAdminView";
    public static final String USUARIOS_VIEW   = "UsuariosView";
    public static final String SUCURSAL_VIEW   = "SucursalView";

    private final AppManager appManager = AppManager.initialize(this::postInit);

    @Override
    public void init() {
        // 2. Registro de Fábricas de Vistas
        appManager.addViewFactory(LOGIN_VIEW,      () -> cargarVista("/com/kardexmobil/views/login.fxml"));
        appManager.addViewFactory(MENU_VIEW,       () -> cargarVista("/com/kardexmobil/views/menu.fxml"));
        appManager.addViewFactory(PRODUCTO_VIEW,   () -> cargarVista("/com/kardexmobil/views/productos.fxml"));
        appManager.addViewFactory(INVENTARIO_VIEW, () -> cargarVista("/com/kardexmobil/views/inventario.fxml"));
        appManager.addViewFactory(INSUMOS_VIEW,    () -> cargarVista("/com/kardexmobil/views/insumo.fxml"));
        appManager.addViewFactory(HISTORIAL_VIEW,  () -> cargarVista("/com/kardexmobil/views/kardex.fxml"));
        appManager.addViewFactory(SUPERADMIN_VIEW, () -> cargarVista("/com/kardexmobil/views/superadmin.fxml"));
        appManager.addViewFactory(USUARIOS_VIEW,   () -> cargarVista("/com/kardexmobil/views/usuario.fxml"));
        appManager.addViewFactory(SUCURSAL_VIEW,   () -> cargarVista("/com/kardexmobil/views/sucursal.fxml"));
        
        // 3. Construcción del Menú Lateral (Drawer)
        DrawerManager.buildDrawer(appManager);
    }

    private View cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            return loader.load();
        } catch (IOException e) {
            System.err.println("FALLO CRÍTICO: No se encontró el archivo en " + fxmlPath);
            e.printStackTrace(); 
            return new View(); 
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        appManager.start(primaryStage);
    }

    private void postInit(Scene scene) {
        Swatch.RED.assignTo(scene);
        
        // --- CAMBIO PARA EL ICONO ---
        Stage stage = (Stage) scene.getWindow();
        try (InputStream is = getClass().getResourceAsStream("/Adecco.png")) {
            if (is != null) {
                stage.getIcons().add(new Image(is));
            } else {
                System.out.println("Aviso: No se encontró el archivo icon.png en src/main/resources/");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ----------------------------

        if (getClass().getResource("style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        }
    }

    public static void main(String args[]) {
        launch(args);
    }
}