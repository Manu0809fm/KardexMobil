package com.kardexmobil.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dao.InsumoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Insumo;
import utils.Sesion;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InsumoPresenter {

    @FXML private View insumoView;
    @FXML private TextField txtCodigoLote, txtNombre, txtStock, txtBuscar;
    @FXML private ComboBox<String> cbUnidad;
    @FXML private DatePicker dpVencimiento;
    @FXML private VBox contenedorLote;
    @FXML private TableView<Insumo> tblInsumos;
    @FXML private TableColumn<Insumo, String> colNombre;
    @FXML private TableColumn<Insumo, Double> colStock;
    @FXML private TableColumn<Insumo, LocalDate> colVencimiento;

    private final InsumoDAO insumoDAO = new InsumoDAO();
    private Insumo insumoSeleccionado;
    private ObservableList<Insumo> listaSucursal;

    public void initialize() {
        // Configuración de Combos y Columnas
        cbUnidad.getItems().addAll("Und.", "Par.", "Caja.", "Kit.", "paqu.");
        
        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
        colStock.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("stockInicial"));
        colVencimiento.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("fechaVencimiento"));

        // Formato de fecha en tabla
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colVencimiento.setCellFactory(column -> new TableCell<Insumo, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(empty ? null : "N/A");
                } else {
                    setText(item.format(dtf));
                }
            }
        });

        // Listener de selección
        tblInsumos.getSelectionModel().selectedItemProperty().addListener((obs, old, insumo) -> {
            if (insumo != null) seleccionarInsumo(insumo);
        });

        // Buscador dinámico
        txtBuscar.textProperty().addListener((obs, old, val) -> buscar());
// Evento de "mostrando vista"
    insumoView.showingProperty().addListener((obs, oldV, newV) -> {
        if (newV) {
            configurarAppBar(); 
            cargar();
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
        appBar.setTitleText("Gestion de EPPs");
        
        // 4. Limpiar botones anteriores y añadir el botón de REFRESH
        appBar.getActionItems().clear(); 
        appBar.getActionItems().add(MaterialDesignIcon.REFRESH.button(e -> cargar()));
    }
}

    private void seleccionarInsumo(Insumo insumo) {
        insumoSeleccionado = insumo;
        txtNombre.setText(insumo.getNombre());
        cbUnidad.setValue(insumo.getUnidad());
        txtStock.setText(String.valueOf(insumo.getStockInicial()));
        dpVencimiento.setValue(insumo.getFechaVencimiento());
        
        // Cargar detalles extra desde el DAO
        insumoDAO.cargarDetallesDesdeInventario(insumo.getId(), txtStock, txtCodigoLote, dpVencimiento);
    }

    @FXML
    private void guardarInsumo() {
        try {
            String nombre = txtNombre.getText().trim();
            String unidad = cbUnidad.getValue();
            if (nombre.isEmpty() || unidad == null) {
                alerta("Error", "Nombre y Unidad obligatorios.");
                return;
            }

            double stock = txtStock.getText().isEmpty() ? 0.0 : Double.parseDouble(txtStock.getText());
            String lote = txtCodigoLote.getText().trim().isEmpty() ? "N/A" : txtCodigoLote.getText().trim();
            LocalDate fechaVenc = dpVencimiento.getValue();

            // Validación zona horaria Perú
            LocalDate hoyPeru = LocalDate.now(ZoneId.of("America/Lima"));
            if (fechaVenc != null && fechaVenc.isBefore(hoyPeru)) {
                alerta("Error", "La fecha no puede ser anterior a hoy (" + hoyPeru + ").");
                return;
            }

            if (insumoSeleccionado == null) {
                Insumo nuevo = new Insumo();
                nuevo.setNombre(nombre);
                nuevo.setUnidad(unidad);
                insumoDAO.insertarConLote(nuevo, lote, fechaVenc, stock);
                alerta("Éxito", "EPP registrado.");
            } else {
                insumoSeleccionado.setNombre(nombre);
                insumoSeleccionado.setUnidad(unidad);
                insumoDAO.actualizarCompleto(insumoSeleccionado, lote, fechaVenc, stock);
                alerta("Éxito", "EPP actualizado.");
            }
            limpiar();
            cargar();
        } catch (Exception e) {
            alerta("Error", "Verifique los datos: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarInsumo() {
        if (insumoSeleccionado == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar permanentemente " + insumoSeleccionado.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                if (insumoDAO.eliminar(insumoSeleccionado.getId())) {
                    alerta("Éxito", "Registro eliminado.");
                    cargar();
                    limpiar();
                }
            }
        });
    }

    private void cargar() {
        List<Insumo> lista = insumoDAO.listar();
        listaSucursal = FXCollections.observableArrayList(lista);
        tblInsumos.setItems(listaSucursal);
    }

    private void buscar() {
        String texto = txtBuscar.getText().toLowerCase().trim();
        if (texto.isEmpty()) {
            tblInsumos.setItems(listaSucursal);
        } else {
            tblInsumos.setItems(listaSucursal.filtered(i -> 
                i.getNombre().toLowerCase().contains(texto)));
        }
    }

    @FXML
    private void limpiar() {
        insumoSeleccionado = null;
        txtNombre.clear();
        cbUnidad.setValue(null);
        txtStock.clear();
        txtCodigoLote.clear();
        dpVencimiento.setValue(null);
        tblInsumos.getSelectionModel().clearSelection();
    }

    private void alerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setContentText(m);
        a.showAndWait();
    }
}