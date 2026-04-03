package com.kardexmobil.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dao.UsuarioDAO;
import dao.MovimientoInventarioDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import utils.Sesion;
import java.util.Map;

import static com.kardexmobil.KardexMobil.LOGIN_VIEW;

public class SuperAdminPresenter {

    @FXML private View superAdminView;
    @FXML private BarChart<String, Number> chartVentas;
    @FXML private PieChart chartRoles;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblTotalventas;
    @FXML private Label lblSucursalTop;
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAO();

    public void initialize() {
        superAdminView.showingProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                // Clave: Ejecutar después de que la vista se asiente
                Platform.runLater(() -> {
                    configurarAppBar();
                    cargarDashboard();
                });
            }
        });
    }

    private void configurarAppBar() {
        AppManager app = AppManager.getInstance();
        if (app != null && app.getAppBar() != null) {
            AppBar appBar = app.getAppBar();
            appBar.setVisible(true); // Forzamos visibilidad por si venimos del login
            
            // Icono de menú para abrir el Drawer
            appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> app.getDrawer().open()));
            appBar.setTitleText("Panel Super Admin");
            
            appBar.getActionItems().clear();
            
            // Botón de refresco manual
            appBar.getActionItems().add(MaterialDesignIcon.REFRESH.button(e -> cargarDashboard()));
            
            // Botón de cerrar sesión
            appBar.getActionItems().add(MaterialDesignIcon.EXIT_TO_APP.button(e -> cerrarSesion()));
        }
    }

    private void cargarDashboard() {
        try {
            // 1. Usuarios
            lblTotalUsuarios.setText(String.valueOf(usuarioDAO.obtenerTotalUsuarios()));

            // 2. Gráfico de Roles
            Map<String, Integer> conteoRoles = usuarioDAO.obtenerConteoPorRol();
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            conteoRoles.forEach((rol, cantidad) -> pieData.add(new PieChart.Data(rol, cantidad)));
            chartRoles.setData(pieData);

            // 3. Resumen de Movimientos
            Map<String, Integer> flujoPorDia = movimientoDAO.obtenerResumenMovimientos();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Movimientos"); // Añadido nombre para evitar errores de render
            
            flujoPorDia.forEach((fecha, cantidad) -> {
                series.getData().add(new XYChart.Data<>(fecha, cantidad));
            });

            chartVentas.getData().clear();
            chartVentas.getData().add(series);

            // 4. Totales
            lblTotalventas.setText(String.valueOf(movimientoDAO.obtenerTotalMovimientos()));
            lblSucursalTop.setText("Sede Central - Adecco");

        } catch (Exception e) {
            System.err.println("Error dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        Sesion.setUsuario(null); // Importante limpiar la sesión en memoria
        AppManager.getInstance().switchView(LOGIN_VIEW);
    }
}