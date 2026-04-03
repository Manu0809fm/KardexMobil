package com.kardexmobil.views;

import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dao.MovimientoInventarioDAO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.MovimientoInventario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistorialInventarioPresenter {

    @FXML private View historialView;
    @FXML private DatePicker dpInicio, dpFin;
    @FXML private TextField txtBuscar;
    @FXML private TableView<MovimientoInventario> tablaMovimientos;
    
    @FXML private TableColumn<MovimientoInventario, LocalDateTime> colFecha;
    @FXML private TableColumn<MovimientoInventario, String> colTipo, colMotivo, colID, colColaborador;
    @FXML private TableColumn<MovimientoInventario, BigDecimal> colCantidad;

    private final MovimientoInventarioDAO dao = new MovimientoInventarioDAO();
    private ObservableList<MovimientoInventario> listaSucursal;

   public void initialize() {
    // Solo configuramos las columnas que REALMENTE existen en el FXML
    if (colFecha != null) colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
    if (colTipo != null) colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));
    if (colID != null) colID.setCellValueFactory(new PropertyValueFactory<>("nombreItem"));

    // Formato de Fecha
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
    if (colFecha != null) {
        colFecha.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(dtf));
            }
        });
    }

    // Colores para Entradas y Salidas
    if (colTipo != null) {
        colTipo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toUpperCase());
                    if (item.equalsIgnoreCase("ENTRADA")) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); 
                    } else {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    txtBuscar.textProperty().addListener((obs, old, val) -> buscar());
    
    historialView.showingProperty().addListener((obs, oldV, newV) -> {
        if (newV) {
            configurarAppBar();
            cargarDatos();
        }
    });
}
    @FXML
private void exportarCSV() {
    List<model.MovimientoInventario> lista = tablaMovimientos.getItems();

    if (lista == null || lista.isEmpty()) {
        alerta("Información", "No hay datos en la tabla para exportar.");
        return; 
    }

    // 1. Obtener ruta privada (Compatible con Android, iOS y Desktop)
    File carpeta = com.gluonhq.attach.storage.StorageService.create()
            .flatMap(com.gluonhq.attach.storage.StorageService::getPrivateStorage)
            .orElseThrow(() -> new RuntimeException("Error: No se pudo acceder al almacenamiento del dispositivo."));

    String nombreArchivo = "Reporte_Movimientos_" + System.currentTimeMillis() + ".csv";
    File archivo = new File(carpeta, nombreArchivo);

    // 2. Escribir el archivo con codificación UTF-8 y BOM para Excel
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), "UTF-8"))) {
        
        bw.write('\ufeff'); // Escribir BOM para que Excel reconozca tildes

        // Definir encabezados
        String[] encabezados = {"FECHA", "ITEM", "TIPO", "CANTIDAD", "REFERENCIA", "RESPONSABLE"};
        bw.write(String.join(";", encabezados));
        bw.newLine();

        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // 3. Recorrer la lista y escribir filas
        for (model.MovimientoInventario m : lista) {
            // Usamos %s para la cantidad para evitar el error 'IllegalFormatConversionException' con BigDecimal
            String fila = String.format("%s;%s;%s;%s;%s;%s",
                m.getFecha() != null ? m.getFecha().format(dtf) : "---",
                limpiar(m.getNombreItem()),
                limpiar(m.getTipoMovimiento()),
                m.getCantidadMovida() != null ? m.getCantidadMovida().toString() : "0.00",
                limpiar(m.getReferenciaDocumento()),
                limpiar(m.getColaborador())
            );
            bw.write(fila);
            bw.newLine();
        }

        // 4. Notificar y Abrir menú de compartir (Vital en móviles)
        alerta("Éxito", "CSV generado correctamente.");
        
        com.gluonhq.attach.share.ShareService.create().ifPresent(service -> {
            service.share("Compartir Reporte de Inventario", archivo);
        });

    } catch (Exception e) {
        e.printStackTrace();
        alerta("Error", "No se pudo generar el archivo: " + e.getMessage());
    }
}
/**
     * Limpia el texto para que los ';' o saltos de línea no rompan las columnas del CSV.
     */

@FXML
private void exportarPDF() {
    List<MovimientoInventario> listaAExportar = tablaMovimientos.getItems();

    if (listaAExportar == null || listaAExportar.isEmpty()) {
        alerta("Alerta", "No hay datos para exportar.");
        return;
    }

    try {
        // 1. Enviar los datos al generador (Asegúrate que tu clase ReporteMovimientosPDF 
        // use StorageService para la ruta en lugar de "C:\")
        utils.ReporteMovimientosPDF.generar(listaAExportar);

        alerta("Éxito", "PDF generado en el almacenamiento de la aplicación.");
        
    } catch (Exception e) {
        e.printStackTrace();
        alerta("Error", "No se pudo generar el PDF: " + e.getMessage());
    }
}

private String limpiar(String texto) {
    if (texto == null) return "---";
    return texto.replace(";", ",").replace("\n", " ").replace("\r", " ").trim();
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
        appBar.setTitleText("Historial de Movimientos");
        
        // 4. Limpiar botones anteriores y añadir el botón de REFRESH
        appBar.getActionItems().clear(); 
        appBar.getActionItems().add(MaterialDesignIcon.REFRESH.button(e -> cargarDatos()));
    }
}

    @FXML
    private void cargarDatos() {
        List<MovimientoInventario> resultado;
        if (dpInicio.getValue() != null && dpFin.getValue() != null) {
            resultado = dao.listarPorRangoFechasDetallado(dpInicio.getValue(), dpFin.getValue());
        } else {
            resultado = dao.listarDetallado();
        }

        listaSucursal = FXCollections.observableArrayList(resultado != null ? resultado : FXCollections.observableArrayList());
        tablaMovimientos.setItems(listaSucursal);
    }

    private void buscar() {
        String texto = txtBuscar.getText().toLowerCase().trim();
        if (texto.isEmpty()) {
            tablaMovimientos.setItems(listaSucursal);
            return;
        }

        tablaMovimientos.setItems(listaSucursal.filtered(m -> 
            (m.getTipoMovimiento() != null && m.getTipoMovimiento().toLowerCase().contains(texto)) ||
            (m.getNombreItem() != null && m.getNombreItem().toLowerCase().contains(texto)) ||
            (m.getColaborador() != null && m.getColaborador().toLowerCase().contains(texto))
        ));
    }

    private void alerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setContentText(m);
        a.showAndWait();
    }
}