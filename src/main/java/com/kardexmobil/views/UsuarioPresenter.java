package com.kardexmobil.views;

import com.gluonhq.charm.glisten.application.AppManager; // Cambiado por consistencia
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dao.SucursalDAO;
import dao.UsuarioDAO;
import javafx.application.Platform; // IMPORTANTE
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Sucursal;
import model.Usuario;

import static com.kardexmobil.KardexMobil.SUPERADMIN_VIEW;

public class UsuarioPresenter {

    @FXML private View usuarioView;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRol;
    @FXML private ComboBox<Sucursal> cbSucursal;
    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, String> colUser, colRol, colSucursal;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SucursalDAO sucursalDAO = new SucursalDAO();
    private int idSeleccionado = -1;

    public void initialize() {
        configurarColumnas();
        cbRol.getItems().addAll("ADMIN", "SUPER_ADMIN");
        
        cbRol.valueProperty().addListener((obs, viejo, nuevo) -> {
            boolean esSuper = "SUPER_ADMIN".equals(nuevo);
            cbSucursal.setDisable(esSuper);
            if (esSuper) cbSucursal.getSelectionModel().clearSelection();
        });

        // CORRECCIÓN: Usar Platform.runLater para evitar el error del AppBar
        usuarioView.showingProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                Platform.runLater(() -> {
                    configurarAppBar();
                    cargarCombos();
                    cargarTabla();
                });
            }
        });

        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, u) -> {
            if (u != null) prepararEdicion(u);
        });
    }

    private void configurarAppBar() {
        AppManager app = AppManager.getInstance();
        if (app != null && app.getAppBar() != null) {
            AppBar appBar = app.getAppBar();
            appBar.setVisible(true); // Asegurar visibilidad
            
            // Botón atrás que vuelve al Panel de Super Admin
            appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e -> 
                app.switchView(SUPERADMIN_VIEW))); 
            
            appBar.setTitleText("Gestión de Usuarios");
            appBar.getActionItems().clear();
        }
    }

    // ... (resto de tus métodos: cargarCombos, cargarTabla, guardar, etc., se mantienen igual)
    
    private void cargarCombos() {
        cbSucursal.getItems().setAll(sucursalDAO.listar());
    }

    private void cargarTabla() {
        tblUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.listarUsuarios()));
    }

    private void prepararEdicion(Usuario u) {
        idSeleccionado = u.getId();
        txtUsuario.setText(u.getUsuario());
        txtPassword.setText(""); 
        cbRol.setValue(u.getRol());
        
        cbSucursal.getItems().stream()
                .filter(s -> s.getNombre().equals(u.getSucursal()))
                .findFirst()
                .ifPresent(s -> cbSucursal.setValue(s));
    }

    @FXML
    private void guardar() {
        if (txtUsuario.getText().isEmpty() || cbRol.getValue() == null) {
            mostrarAlerta("Campos incompletos", Alert.AlertType.WARNING);
            return;
        }

        int sucId = "SUPER_ADMIN".equals(cbRol.getValue()) ? 0 : 
                   (cbSucursal.getValue() != null ? cbSucursal.getValue().getId() : -1);

        if (sucId == -1 && !"SUPER_ADMIN".equals(cbRol.getValue())) {
            mostrarAlerta("Debe seleccionar una sucursal para este rol", Alert.AlertType.WARNING);
            return;
        }

        if (idSeleccionado == -1) {
            usuarioDAO.crearUsuario(new Usuario(0, txtUsuario.getText(), cbRol.getValue(), ""), txtPassword.getText(), sucId);
        } else {
            Usuario u = new Usuario(idSeleccionado, txtUsuario.getText(), cbRol.getValue(), "");
            usuarioDAO.actualizarUsuario(u, txtPassword.getText(), sucId);
        }
        limpiarCampos();
        cargarTabla();
    }

    @FXML
    private void eliminar() {
        if (idSeleccionado == -1) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Desea eliminar este usuario?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(tipo -> {
            if (tipo == ButtonType.YES) {
                usuarioDAO.eliminarUsuario(idSeleccionado);
                limpiarCampos();
                cargarTabla();
            }
        });
    }

    @FXML
    private void limpiarCampos() {
        txtUsuario.clear();
        txtPassword.clear();
        cbRol.getSelectionModel().clearSelection();
        cbSucursal.getSelectionModel().clearSelection();
        idSeleccionado = -1;
        tblUsuarios.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String m, Alert.AlertType tipo) {
        Alert a = new Alert(tipo);
        a.setContentText(m);
        a.showAndWait();
    }

    private void configurarColumnas() {
        colUser.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colSucursal.setCellValueFactory(new PropertyValueFactory<>("sucursal"));
    }
}