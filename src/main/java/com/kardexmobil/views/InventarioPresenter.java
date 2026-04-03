    package com.kardexmobil.views;

    import com.gluonhq.charm.glisten.application.AppManager;
    import com.gluonhq.charm.glisten.application.MobileApplication;
    import com.gluonhq.charm.glisten.control.AppBar;
    import com.gluonhq.charm.glisten.mvc.View;
    import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
    import dao.InsumoDAO;
    import dao.InventarioDAO;
    import dao.ProductoDAO;
    import dao.ColaboradorDAO;
    import model.Inventario;
    import model.Colaborador;
    import utils.Sesion;
    import utils.DBConnection;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.beans.property.SimpleObjectProperty;
    import javafx.beans.property.SimpleStringProperty;
    import java.math.BigDecimal;
    import java.sql.Connection;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import pe.gob.sunat.servicio2.registro.consultarucws.service.RucByRucBean;
    import pe.gob.sunat.servicio2.registro.consultarucws.service.RucByTitularBean;
    import pe.gob.sunat.servicio2.registro.consultarucws.service.ServicioRucAdmColWebService;
    import pe.gob.sunat.servicio2.registro.consultarucws.service.ServicioRucAdmColWebServiceImplService;
    import com.gluonhq.attach.storage.StorageService;
    import com.gluonhq.charm.glisten.application.AppManager;
    import com.gluonhq.charm.glisten.control.AppBar;
    import com.gluonhq.charm.glisten.mvc.View;
    import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.beans.property.SimpleObjectProperty;
    import javafx.beans.property.SimpleStringProperty;

    import java.io.File;
    import java.io.BufferedWriter;
    import java.io.FileOutputStream;
    import java.io.OutputStreamWriter;
    import java.math.BigDecimal;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;
    import java.time.LocalDate;
    public class InventarioPresenter {

        @FXML private View inventarioView;
        @FXML private TableView<Inventario> tblInventario;
        @FXML private TableColumn<Inventario, String> colInsumo, colUnidad;
        @FXML private TableColumn<Inventario, BigDecimal> colInicial, colActual, colUsado;

        @FXML private TextField txtDniColaborador, txtBuscarP, txtBuscarI;
        @FXML private Label lblNombreColaborador;

        @FXML private TableView<ProductoStockView> tblProductosStock;
        @FXML private TableColumn<ProductoStockView, String> colProdNombre;
        @FXML private TableColumn<ProductoStockView, BigDecimal> colProdInicial, colProdStock, colProdConsumo;

        private final InsumoDAO insumoDAO = new InsumoDAO();
        private final InventarioDAO inventarioDAO = new InventarioDAO();
        private final ProductoDAO productoDAO = new ProductoDAO();
        private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

        private ProductoStockView productoSeleccionado;
        private Inventario inventarioSeleccionado;
        private String colaboradorIdentificado = null;
        private ObservableList<ProductoStockView> listaSucursal;
        private ObservableList<Inventario> listaSucursalI;

        public void initialize() {
            configurarTablas();

            // Listeners de búsqueda (Buscador dinámico)
            txtBuscarP.textProperty().addListener((obs, old, val) -> buscarP());
            txtBuscarI.textProperty().addListener((obs, old, val) -> buscarI());

            // Manejo de selecciones
            tblInventario.getSelectionModel().selectedItemProperty().addListener((obs, old, inv) -> inventarioSeleccionado = inv);
            tblProductosStock.getSelectionModel().selectedItemProperty().addListener((obs, old, prod) -> productoSeleccionado = prod);

            inventarioView.showingProperty().addListener((obs, oldV, newV) -> {
                if (newV) {
                    configurarAppBar();
                    cargarDatos();
                }
            });
        }

       private void configurarAppBar() {
        AppManager appManager = AppManager.getInstance();
        if (appManager != null && appManager.getAppBar() != null) {
            AppBar appBar = appManager.getAppBar();
            appBar.setVisible(true); 
            appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> appManager.getDrawer().open()));
            appBar.setTitleText("Inventario");

            appBar.getActionItems().clear(); 
            appBar.getActionItems().addAll(
                MaterialDesignIcon.REFRESH.button(e -> cargarDatos()),
                MaterialDesignIcon.PICTURE_AS_PDF.button(e -> generarReporteInventario()),
                MaterialDesignIcon.INSERT_DRIVE_FILE.button(e -> generarReporteCSV())
            );
        }
    }


        // --- LÓGICA DE BÚSQUEDA ---
        private void buscarP() {
            String texto = txtBuscarP.getText().toLowerCase().trim();
            if (texto.isEmpty()) { tblProductosStock.setItems(listaSucursal); return; }
            ObservableList<ProductoStockView> filtrada = FXCollections.observableArrayList();
            listaSucursal.forEach(m -> {
                if (m.getNombre().toLowerCase().contains(texto)) filtrada.add(m);
            });
            tblProductosStock.setItems(filtrada);
        }

        private void buscarI() {
            String texto = txtBuscarI.getText().toLowerCase().trim();
            if (texto.isEmpty()) { tblInventario.setItems(listaSucursalI); return; }
            ObservableList<Inventario> filtrada = FXCollections.observableArrayList();
            listaSucursalI.forEach(m -> {
                String nombre = (m.getInsumo() != null) ? m.getInsumo().getNombre().toLowerCase() : "";
                if (nombre.contains(texto)) filtrada.add(m);
            });
            tblInventario.setItems(filtrada);
        }

        // --- CONEXIÓN SUNAT ---
        @FXML
        private void buscarColaborador() {
            String documento = txtDniColaborador.getText().trim();
            if (documento.length() != 8 && documento.length() != 11) {
                alerta("Documento inválido (8 o 11 dígitos)");
                return;
            }

            Colaborador cLocal = colaboradorDAO.buscarPorDocumento(documento);
            if (cLocal != null) {
                actualizarUIColaborador(cLocal.getNombre(), false);
                return;
            }

            // Simulación o llamada a API (Mantener tu lógica de WS)
            Colaborador cApi = consultarApiColaborador(documento);
            if (cApi != null) {
                colaboradorDAO.guardar(cApi);
                actualizarUIColaborador(cApi.getNombre(), true);
            } else {
                lblNombreColaborador.setText("❌ No identificado");
                colaboradorIdentificado = null;
            }
        }

        private Colaborador consultarApiColaborador(String documento) {
            try {
                ServicioRucAdmColWebService service = new ServicioRucAdmColWebServiceImplService()
                        .getServicioRucAdmColWebServiceImplPort();

                ((jakarta.xml.ws.BindingProvider) service)
                        .getRequestContext()
                        .put(jakarta.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                                "https://ww1.sunat.gob.pe/cl-ti-iaconsultaruc-ws/ServicioRucAdmColService.htm");

                Colaborador colab = new Colaborador();

                if (documento.length() == 11) {
                    RucByRucBean bean = service.buscarRuc(documento);
                    if (bean == null) {
                        return null;
                    }

                    colab.setDocumento(bean.getNumRuc());
                    colab.setNombre(bean.getNombre());
                    return colab;

                } else if (documento.length() == 8) {
                    RucByTitularBean bean = service.buscarRucByTitular("1", documento);
                    if (bean == null) {
                        return null;
                    }

                    colab.setDocumento(documento);
                    colab.setNombre(bean.getNombre());
                    return colab;
                }

            } catch (Exception e) {
                System.err.println("Error en conexión con SUNAT: " + e.getMessage());
                e.printStackTrace();
            }
            return null; 
        }

        private void actualizarUIColaborador(String nombre, boolean esSunat) {
            colaboradorIdentificado = nombre;
            lblNombreColaborador.setText((esSunat ? "✅ (SUNAT) " : "✅ ") + nombre);
        }
         @FXML
        private void agregarStockProducto() {
            if (colaboradorIdentificado == null) {
                alerta("Verifique un colaborador primero.");
                return;
            }
            if (productoSeleccionado == null) {
                alerta("Seleccione un Insumo de la tabla primero.");
                return;
            }

            mostrarDialogoLote("Nuevo Lote de Insumos", "Ingreso para: " + productoSeleccionado.getNombre()).ifPresent(data -> {
                try {
                    BigDecimal cant = new BigDecimal(data.cantidad.replace(",", "."));
                    productoDAO.registrarEntradaConLote(
                            productoSeleccionado.getId(),
                            cant,
                            data.lote,
                            data.vencimiento,
                            colaboradorIdentificado
                    );
                    cargarDatos();
                    alerta("✅ Producto registrado correctamente.");
                } catch (Exception e) {
                    alerta("Error: " + e.getMessage());
                }
            });
        }

        private Optional<LoteData> mostrarDialogoLote(String titulo, String cabecera) {
        Dialog<LoteData> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecera);

        ButtonType guardarBtnType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarBtnType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(15); // Un poco más de espacio vertical para dedos móviles
        grid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20)); // Padding simétrico

        TextField txtCant = new TextField();
        txtCant.setPromptText("0.00");
        // Forzamos teclado numérico en móviles si es posible
        txtCant.setPrefWidth(200); 

        TextField txtLote = new TextField();
        txtLote.setPromptText("LOTE-001");

        DatePicker dpVence = new DatePicker();
        dpVence.setPromptText("Opcional");
        dpVence.setMaxWidth(Double.MAX_VALUE); // Para que ocupe el ancho del grid

        grid.add(new Label("Cantidad:"), 0, 0);
        grid.add(txtCant, 1, 0);
        grid.add(new Label("Lote:"), 0, 1);
        grid.add(txtLote, 1, 1);
        grid.add(new Label("Vencimiento:"), 0, 2);
        grid.add(dpVence, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
        if (btn == guardarBtnType) {
            return new LoteData(
                txtCant.getText(), 
                txtLote.getText(), 
                dpVence.getValue() // <--- Enviamos el LocalDate directamente
            );
        }
        return null;
    });
        return dialog.showAndWait();
    }

    // Asegúrate de que tu clase LoteData acepte String en el tercer parámetro
    private static class LoteData {
        String cantidad;
        String lote;
        java.time.LocalDate vencimiento; // <--- Cambiado a LocalDate

        LoteData(String c, String l, java.time.LocalDate v) {
            this.cantidad = c; 
            this.lote = l; 
            this.vencimiento = v;
        }
    }
    @FXML
        private void agregarInventario() {
            if (colaboradorIdentificado == null) {
                alerta("Verifique un colaborador primero.");
                return;
            }

            if (inventarioSeleccionado == null || inventarioSeleccionado.getInsumo() == null) {
                alerta("❌ Por favor, haz clic en una fila de la tabla de INSUMOS.");
                return;
            }

            mostrarDialogoLote("Nuevo Lote de EPPs", "Ingreso para: " + inventarioSeleccionado.getInsumo().getNombre())
                    .ifPresent(data -> {
                        try {
                            BigDecimal cant = new BigDecimal(data.cantidad.replace(",", "."));
                            inventarioDAO.agregarInventarioConLote(
                                    inventarioSeleccionado.getInsumo().getId(),
                                    cant,
                                    data.lote,
                                    data.vencimiento,
                                    colaboradorIdentificado
                            );
                            cargarDatos();
                            alerta("✅ EPPs agregado con éxito.");
                        } catch (Exception e) {
                            alerta("Error al registrar: " + e.getMessage());
                        }
                    });
        }
        // --- ACCIONES DE STOCK ---
        @FXML
        private void descontarInsumo() {
            if (colaboradorIdentificado == null || inventarioSeleccionado == null) {
                alerta("Verifique colaborador y seleccione un ítem.");
                return;
            }

            // En móvil usamos un diálogo simple o un TextField auxiliar
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Retirar EPP");
            dialog.setHeaderText("Cantidad a retirar de " + inventarioSeleccionado.getInsumo().getNombre());

            dialog.showAndWait().ifPresent(val -> {
                try (Connection con = DBConnection.getConnection()) {
                    con.setAutoCommit(false);
                    BigDecimal cant = new BigDecimal(val.replace(",", "."));
                    boolean exito = inventarioDAO.descontar(con, inventarioSeleccionado.getInsumo().getId(), 
                            Integer.parseInt(Sesion.getUsuario().getSucursal()), cant, "RETIRO MOVIL", colaboradorIdentificado);

                    if (exito) {
                        con.commit();
                        cargarDatos();
                        alerta("✅ Retiro exitoso.");
                    } else {
                        alerta("¡Error! Stock insuficiente.");
                    }
                } catch (Exception e) {
                    alerta("Error: " + e.getMessage());
                }
            });
        }

        private void cargarDatos() {
            // Lógica de carga idéntica a tu original
            listaSucursalI = FXCollections.observableArrayList(inventarioDAO.listar());
            tblInventario.setItems(listaSucursalI);

            List<ProductoStockView> listaProd = new ArrayList<>();
            productoDAO.listarPorSucursal().forEach(p -> {
                BigDecimal inicial = (p.getStockInicial() != null) ? new BigDecimal(p.getStockInicial()) : new BigDecimal(p.getStock());
                BigDecimal actual = new BigDecimal(p.getStock());
                BigDecimal consumo = (inicial.compareTo(actual) >= 0) ? inicial.subtract(actual) : BigDecimal.ZERO;
                listaProd.add(new ProductoStockView(p.getId(), p.getNombre(), inicial, actual, consumo));
            });
            listaSucursal = FXCollections.observableArrayList(listaProd);
            tblProductosStock.setItems(listaSucursal);
        }
        // Agrega este método dentro de InventarioPresenter.java 
    // preferiblemente después de descontarInsumo()

    @FXML
    private void descontarStockProducto() {
        if (colaboradorIdentificado == null || productoSeleccionado == null) {
            alerta("Verifique colaborador y seleccione una herramienta.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Retirar Herramienta");
        dialog.setHeaderText("Cantidad a retirar de " + productoSeleccionado.getNombre());

        dialog.showAndWait().ifPresent(val -> {
            try {
                // Convertimos el valor a negativo porque tu DAO hace "stock = stock + ?"
                // Si el usuario pone 5, enviamos -5 para que reste.
                BigDecimal cant = new BigDecimal(val.replace(",", ".")).negate();

                // LLAMADA EXACTA A TU DAO (3 parámetros):
                // public void actualizarStockProducto(int idProducto, BigDecimal cantidad, String colaborador)
                productoDAO.actualizarStockProducto(
                    productoSeleccionado.getId(), 
                    cant, 
                    colaboradorIdentificado
                );

                cargarDatos(); // Refresca las tablas después del éxito
                alerta("✅ Retiro de herramienta registrado.");

            } catch (NumberFormatException e) {
                alerta("Error: Ingrese un número válido.");
            } catch (Exception e) {
                alerta("Error al actualizar: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    @FXML
    private void generarReporteInventario() {
        List<Inventario> listaInsumos = tblInventario.getItems();
        List<ProductoStockView> listaProductos = tblProductosStock.getItems();

        if (listaInsumos.isEmpty() && listaProductos.isEmpty()) {
            alerta("No hay datos para exportar.");
            return;
        }

        try {
            // Obtenemos la ruta interna del celular (Android/iOS)
            File rutaBase = StorageService.create()
                    .flatMap(StorageService::getPrivateStorage)
                    .orElse(new File(System.getProperty("user.home"))); // Fallback para PC

            File carpeta = new File(rutaBase, "Reportes");
            if (!carpeta.exists()) carpeta.mkdirs();

            // IMPORTANTE: Tu clase ReporteInventarioPDF debe aceptar la ruta como parámetro
            // Si no la acepta, deberás modificarla para que reciba el objeto 'carpeta'
                utils.ReporteInventarioPDF.generarReporteActual(listaInsumos, listaProductos);

            alerta("✅ PDF generado en la carpeta de la aplicación.");
        } catch (Exception e) {
            alerta("Error al generar PDF: " + e.getMessage());
        }
    }

    @FXML       
    private void generarReporteCSV() {
        List<Inventario> listaInsumos = tblInventario.getItems();
        List<ProductoStockView> listaProductos = tblProductosStock.getItems();

        if (listaInsumos.isEmpty() && listaProductos.isEmpty()) {
            alerta("No hay datos para exportar.");
            return;
        }

        try {
            // Obtener ruta compatible con Android/iOS
            File rutaBase = StorageService.create()
                    .flatMap(StorageService::getPrivateStorage)
                    .orElse(new File(System.getProperty("user.home")));

            File carpeta = new File(rutaBase, "Reportes");
            if (!carpeta.exists()) carpeta.mkdirs();

            String fileName = "Inventario_" + System.currentTimeMillis() + ".csv";
            File archivo = new File(carpeta, fileName);

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), "UTF-8"))) {
                bw.write('\ufeff'); // Para que Excel en móvil reconozca tildes

                // --- SECCIÓN EPPS ---
                bw.write("REPORTE DE EPPS Y UNIFORMES");
                bw.newLine();
                bw.write("Nombre;U.M.;Apertura;Consumo;Disponible");
                bw.newLine();

                for (Inventario inv : listaInsumos) {
                    String nombre = (inv.getInsumo() != null) ? inv.getInsumo().getNombre() : "---";
                    String unidad = (inv.getInsumo() != null) ? inv.getInsumo().getUnidad() : "---";
                    BigDecimal apertura = (inv.getCantidadInicial() != null) ? inv.getCantidadInicial() : BigDecimal.ZERO;
                    BigDecimal actual = (inv.getCantidadActual() != null) ? inv.getCantidadActual() : BigDecimal.ZERO;
                    BigDecimal usado = apertura.subtract(actual);

                    bw.write(String.format("%s;%s;%s;%s;%s", 
                            nombre, unidad, apertura, usado, actual));
                    bw.newLine();
                }

                bw.newLine();

                // --- SECCIÓN HERRAMIENTAS ---
                bw.write("REPORTE DE INSUMOS Y HERRAMIENTAS");
                bw.newLine();
                bw.write("Insumo/Herramienta;Inicial;Salidas;Actual");
                bw.newLine();

                for (ProductoStockView prod : listaProductos) {
                    bw.write(String.format("%s;%s;%s;%s", 
                            prod.getNombre(), prod.getInicial(), prod.getConsumo(), prod.getStockActual()));
                    bw.newLine();
                }
            }

            alerta("✅ CSV generado: " + archivo.getName());

            // Solo intentar abrir automáticamente si estás probando en Windows
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "start", archivo.getAbsolutePath()).start();
            }

        } catch (Exception e) {
            alerta("Error al generar CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

        private void configurarTablas() {
        // Tabla EPP (EPPS Y UNIFORMES)
        colInsumo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getInsumo().getNombre()));
        // colUnidad no existe en tu FXML, si la usas dará error. Asegúrate de comentarla si no está en el XML.
        // colUnidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getInsumo().getUnidad()));
        colActual.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCantidadActual()));

        // Tabla PRODUCTOS (HERRAMIENTAS)
        colProdNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));
        colProdStock.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getStockActual()));
    }

        private void alerta(String m) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(m);
            a.showAndWait();
        }

        // Clase interna para la vista de tabla
        public static class ProductoStockView {
            private final int id;
            private final String nombre;
            private final BigDecimal inicial, stockActual, consumo;

            public ProductoStockView(int id, String n, BigDecimal i, BigDecimal s, BigDecimal c) {
                this.id = id; this.nombre = n; this.inicial = i; this.stockActual = s; this.consumo = c;
            }
            public int getId() { return id; }
            public String getNombre() { return nombre; }
            public BigDecimal getInicial() { return inicial; }
            public BigDecimal getStockActual() { return stockActual; }
            public BigDecimal getConsumo() { return consumo; }
        }
    }