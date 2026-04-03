package com.kardexmobil;

import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.attach.util.Services;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.control.NavigationDrawer.Item;
import com.gluonhq.charm.glisten.control.NavigationDrawer.ViewItem;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.scene.image.Image;
import utils.Sesion;
import model.Usuario;

import static com.kardexmobil.KardexMobil.*;

public class DrawerManager {

    public static void buildDrawer(AppManager app) {
        NavigationDrawer drawer = app.getDrawer();
        drawer.getItems().clear();
        
        NavigationDrawer.Header header = new NavigationDrawer.Header("Adecco",
                "Gestión de Inventario",
                new Avatar(21, new Image(DrawerManager.class.getResourceAsStream("/icon.png"))));
        drawer.setHeader(header);
        
        Usuario user = Sesion.getUsuario();

        if (user != null) {
            // --- CASO 1: ES SUPER ADMIN (Solo ve sus 3 opciones especiales) ---
            if ("SUPER_ADMIN".equals(user.getRol())) {
                
                drawer.getItems().add(new ViewItem("Panel Control", 
                        MaterialDesignIcon.SECURITY.graphic(), SUPERADMIN_VIEW));
                
                drawer.getItems().add(new ViewItem("Gestión de Usuarios", 
                        MaterialDesignIcon.PEOPLE.graphic(), USUARIOS_VIEW));
                
                drawer.getItems().add(new ViewItem("Sucursales", 
                        MaterialDesignIcon.BUSINESS.graphic(), SUCURSAL_VIEW));

            } 
            // --- CASO 2: ES ADMIN NORMAL (Ve las opciones de inventario) ---
            else if ("ADMIN".equals(user.getRol())) {
                
                drawer.getItems().add(new ViewItem("Dashboard", 
                        MaterialDesignIcon.DASHBOARD.graphic(), MENU_VIEW, ViewStackPolicy.SKIP));
                
                drawer.getItems().add(new ViewItem("Insumos y Herramientas", 
                        MaterialDesignIcon.SHOPPING_CART.graphic(), PRODUCTO_VIEW));
                
                drawer.getItems().add(new ViewItem("EPPs y Uniformes", 
                        MaterialDesignIcon.SHOPPING_CART.graphic(), INSUMOS_VIEW));

                drawer.getItems().add(new ViewItem("Inventario General", 
                        MaterialDesignIcon.STORAGE.graphic(), INVENTARIO_VIEW));
                
                drawer.getItems().add(new ViewItem("Historial Kardex", 
                        MaterialDesignIcon.HISTORY.graphic(), HISTORIAL_VIEW));
            }

            // --- OPCIÓN PARA AMBOS: CERRAR SESIÓN ---
            final Item logoutItem = new Item("Cerrar Sesión", MaterialDesignIcon.LOCK.graphic());
            logoutItem.selectedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    Sesion.setUsuario(null);
                    app.switchView(LOGIN_VIEW);
                }
            });
            drawer.getItems().add(logoutItem);
        }

        // Botón de salida física (Solo Desktop)
        if (Platform.isDesktop()) {
            final Item quitItem = new Item("Salir", MaterialDesignIcon.EXIT_TO_APP.graphic());
            quitItem.selectedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
                }
            });
            drawer.getItems().add(quitItem);
        }
    }
}