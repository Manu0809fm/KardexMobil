package com.kardexmobil.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dao.SucursalDAO;
import javafx.application.Platform; // IMPORTANTE
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Sucursal;

public class SucursalPresenter {

    @FXML private View sucursalView;
    @FXML private TabPane tabPanePrincipal;
    @FXML private TextField txtNombre, txtDireccion;
    @FXML private TableView<Sucursal> tblSucursales;
    @FXML private TableColumn<Sucursal, String> colNombre, colDireccion;
    @FXML private Button btnGuardar, btnEliminar;

    private final SucursalDAO dao = new SucursalDAO();
    private Sucursal sucursalSeleccionada;

    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        // CORRECCIÓN: Platform.runLater para esperar la construcción del AppBar
        sucursalView.showingProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                Platform.runLater(() -> {
                    configurarAppBar();
                    cargarDatos();
                });
            }
        });

        tblSucursales.getSelectionModel().selectedItemProperty().addListener((obs, vieja, nueva) -> {
            if (nueva != null) {
                sucursalSeleccionada = nueva;
                txtNombre.setText(nueva.getNombre());
                txtDireccion.setText(nueva.getDireccion());
                btnGuardar.setText("ACTUALIZAR DATOS");
                btnEliminar.setVisible(true);
                tabPanePrincipal.getSelectionModel().select(1);
            }
        });
        
        btnEliminar.setVisible(false);
    }

    private void configurarAppBar() {
        AppManager app = AppManager.getInstance();
        if (app != null && app.getAppBar() != null) {
            AppBar appBar = app.getAppBar();
            appBar.setVisible(true);
            
            // Icono de hamburguesa
            appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> app.getDrawer().open()));
            appBar.setTitleText("Gestión de Sucursales");
            
            appBar.getActionItems().clear();
            appBar.getActionItems().add(MaterialDesignIcon.REFRESH.button(e -> cargarDatos()));
            appBar.getActionItems().add(MaterialDesignIcon.ADD.button(e -> {
                limpiarCampos();
                tabPanePrincipal.getSelectionModel().select(1);
            }));
        }
    }

    // ... (resto de tus métodos: cargarDatos, guardar, eliminar, limpiarCampos se mantienen igual)

    private void cargarDatos() {
        try {
            ObservableList<Sucursal> lista = FXCollections.observableArrayList(dao.listar());
            tblSucursales.setItems(lista);
        } catch (Exception e) {
            mostrarAlerta("Error", "Fallo al conectar con la base de datos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty()) {
            mostrarAlerta("Validación", "Debes ingresar nombre y dirección.", Alert.AlertType.WARNING);
            return;
        }

        boolean exito;
        if (sucursalSeleccionada == null) {
            Sucursal nueva = new Sucursal();
            nueva.setNombre(nombre);
            nueva.setDireccion(direccion);
            nueva.setActivo(true);
            exito = dao.crear(nueva);
        } else {
            sucursalSeleccionada.setNombre(nombre);
            sucursalSeleccionada.setDireccion(direccion);
            exito = dao.actualizar(sucursalSeleccionada);
        }

        if (exito) {
            mostrarAlerta("Éxito", "Información guardada.", Alert.AlertType.INFORMATION);
            limpiarCampos();
            cargarDatos();
            tabPanePrincipal.getSelectionModel().select(0);
        }
    }

    @FXML
    private void eliminar() {
        if (sucursalSeleccionada == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Dar de baja a " + sucursalSeleccionada.getNombre() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (dao.eliminar(sucursalSeleccionada.getId())) {
                    limpiarCampos();
                    cargarDatos();
                    tabPanePrincipal.getSelectionModel().select(0);
                }
            }
        });
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtDireccion.clear();
        sucursalSeleccionada = null;
        btnGuardar.setText("GUARDAR NUEVO");
        btnEliminar.setVisible(false);
        tblSucursales.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String t, String m, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}