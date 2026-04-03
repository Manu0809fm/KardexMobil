package com.kardexmobil.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dao.ProductoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Producto;
import utils.Sesion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ProductosPresenter {

    @FXML private View productosView;
    @FXML private TabPane tabPanePrincipal; // Añadido para controlar el cambio de vista
    @FXML private Tab tabFormulario;        // Añadido para referenciar la pestaña de registro
    @FXML private TextField txtNombre, txtStock, txtLote, txtBuscar;
    @FXML private DatePicker dpVencimiento;
    @FXML private ComboBox<String> cmbEstado, cmbUndM, cmbTipo;
    @FXML private Button btnGuardar;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colNombre, colStock, colVencimiento, colUnidadMedida, colTipo;

    private Producto productoSeleccionado;
    private ObservableList<Producto> listaSucursal;
    private final ProductoDAO dao = new ProductoDAO();

    public void initialize() {
    productosView.showingProperty().addListener((obs, oldV, newV) -> {
        if (newV) {
            // Solo configuramos si la vista realmente se está mostrando
            configurarAppBar();
            
            if (colNombre != null && colTipo != null) {
                configurarTabla();
            }
            cargarTabla();
        }
    });

    cargarCombos();
    
    // Listener de búsqueda (validando que txtBuscar no sea null)
    if (txtBuscar != null) {
        txtBuscar.textProperty().addListener((obs, old, val) -> buscar());
    }

    // Selección de tabla
    tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
        if (p != null) {
            mapearAFormulario(p);
            seleccionarTab(1); 
        }
    });
}

    private void cargarCombos() {
    // Verificamos cmbEstado
    if (cmbEstado != null) {
        cmbEstado.getItems().setAll("ACTIVO", "INACTIVO");
        cmbEstado.setValue("ACTIVO");
    }
    
    // Verificamos cmbUndM
    if (cmbUndM != null) {
        cmbUndM.getItems().setAll("UNIDAD", "KG", "LITROS", "PAQUETE", "CAJA", "BOLSAS");
        cmbUndM.setValue("UNIDAD");
    }
    
    // Verificamos cmbTipo
    if (cmbTipo != null) {
        cmbTipo.getItems().setAll("HERRAMIENTAS MANUALES", "INSUMOS", "INSUMOS DE LIMPIEZA");
        cmbTipo.setValue("INSUMOS");
    }
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
        appBar.setTitleText("Gestion de Insumos");
        
        // 4. Limpiar botones anteriores y añadir el botón de REFRESH
        appBar.getActionItems().clear(); 
        appBar.getActionItems().add(MaterialDesignIcon.REFRESH.button(e -> cargarTabla()));
    }
}
    private void configurarTabla() {
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colTipo.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTipo()));
        colStock.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getStock())));
        colVencimiento.setCellValueFactory(d -> {
            LocalDate f = d.getValue().getProximoVencimiento();
            return new javafx.beans.property.SimpleStringProperty(f != null ? f.toString() : "N/A");
        });
        colUnidadMedida.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getUnidadMedida()));

        // Alertas visuales por filas (Colores para móvil)
        tblProductos.setRowFactory(tv -> new TableRow<Producto>() {
            @Override
            protected void updateItem(Producto p, boolean empty) {
                super.updateItem(p, empty);
                if (p == null || empty) {
                    setStyle("");
                } else {
                    if (p.getStock() <= 10) {
                        setStyle("-fx-background-color: #ffcdd2;"); // Rojo suave: Stock bajo
                    } else if (p.getProximoVencimiento() != null) {
                        long dias = ChronoUnit.DAYS.between(LocalDate.now(), p.getProximoVencimiento());
                        if (dias <= 0) setStyle("-fx-background-color: #cfd8dc;"); // Gris: Vencido
                        else if (dias <= 7) setStyle("-fx-background-color: #ffe0b2;"); // Naranja: Próximo
                        else setStyle("");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    @FXML
    private void guardar() {
        try {
            if (txtNombre.getText().trim().isEmpty()) {
                alerta("Error", "El nombre es obligatorio.");
                return;
            }

            String nombre = txtNombre.getText().trim();
            int stockVal = txtStock.getText().isEmpty() ? 0 : Integer.parseInt(txtStock.getText());
            String usuarioActual = Sesion.getUsuario().getUsuario();

            if (productoSeleccionado == null) {
                Producto p = new Producto();
                p.setNombre(nombre);
                p.setStock(stockVal);
                p.setSucursalId(Integer.parseInt(Sesion.getUsuario().getSucursal()));
                p.setTipo(cmbTipo.getValue());
                p.setUnidadMedida(cmbUndM.getValue());
                p.setEstado(cmbEstado.getValue());
                p.setManejaStock(true);
                p.setPrecio(BigDecimal.ZERO);

                dao.insertarConLote(p, txtLote.getText(), dpVencimiento.getValue());
            } else {
                productoSeleccionado.setNombre(nombre);
                productoSeleccionado.setStock(stockVal);
                productoSeleccionado.setTipo(cmbTipo.getValue());
                productoSeleccionado.setUnidadMedida(cmbUndM.getValue());
                dao.actualizarCompleto(productoSeleccionado, txtLote.getText(), dpVencimiento.getValue(), usuarioActual);
            }

            cargarTabla();
            limpiar();
            seleccionarTab(0); // Regresa al catálogo tras guardar
            alerta("Éxito", "Operación realizada correctamente.");
            
        } catch (NumberFormatException e) {
            alerta("Error", "El stock debe ser un número válido.");
        } catch (Exception e) {
            alerta("Error", e.getMessage());
        }
    }

    private void cargarTabla() {
        List<Producto> productos = dao.listarPorSucursal();
        listaSucursal = FXCollections.observableArrayList(productos);
        tblProductos.setItems(listaSucursal);
    }

    private void mapearAFormulario(Producto p) {
        productoSeleccionado = p;
        txtNombre.setText(p.getNombre());
        txtStock.setText(String.valueOf(p.getStock()));
        cmbTipo.setValue(p.getTipo());
        cmbUndM.setValue(p.getUnidadMedida());
        btnGuardar.setText("ACTUALIZAR PRODUCTO");
    }

    @FXML
    private void limpiar() {
        productoSeleccionado = null;
        txtNombre.clear();
        txtStock.clear();
        txtLote.clear();
        dpVencimiento.setValue(null);
        btnGuardar.setText("GUARDAR NUEVO");
        tblProductos.getSelectionModel().clearSelection();
    }

    private void buscar() {
        String texto = txtBuscar.getText().toLowerCase();
        if (texto.isEmpty()) {
            tblProductos.setItems(listaSucursal);
            return;
        }
        ObservableList<Producto> filtrada = listaSucursal.filtered(p -> 
            p.getNombre().toLowerCase().contains(texto) || 
            p.getTipo().toLowerCase().contains(texto)
        );
        tblProductos.setItems(filtrada);
    }

    private void seleccionarTab(int indice) {
        if (tabPanePrincipal != null) {
            tabPanePrincipal.getSelectionModel().select(indice);
        }
    }

    private void retroceder() {
        MobileApplication.getInstance().switchView("Home"); 
    }

    private void alerta(String t, String m) {
        // En Gluon, es preferible usar Toast o Dialogs específicos, 
        // pero Alert sigue funcionando en la capa de JavaFX.
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}