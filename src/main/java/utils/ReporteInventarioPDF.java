package utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import model.Inventario;
import model.Producto;
// Cambiamos el import al modelo correcto que uses en el Presenter
import com.kardexmobil.views.InventarioPresenter.ProductoStockView; 
import com.gluonhq.attach.storage.StorageService;

import java.io.File;
import java.io.FileOutputStream;
import java.awt.Color;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReporteInventarioPDF {

    public static void generarReporteActual(List<Inventario> insumos, List<ProductoStockView> productos) {
        Document document = new Document(PageSize.A4, 30, 30, 40, 40);
        try {
            // 1. OBTENER RUTA DINÁMICA (SOLUCIONA ERROR DE DISCO C)
            File rutaBase = StorageService.create()
                    .flatMap(StorageService::getPrivateStorage)
                    .orElse(new File(System.getProperty("user.home")));
            
            File carpeta = new File(rutaBase, "Reportes");
            if (!carpeta.exists()) carpeta.mkdirs();

            String rutaFinal = new File(carpeta, "Reporte_Stock_" + System.currentTimeMillis() + ".pdf").getAbsolutePath();

            // 2. OBTENER NOMBRE DE SUCURSAL
            String nombreSucursal = obtenerNombreSucursal();

            PdfWriter.getInstance(document, new FileOutputStream(rutaFinal));
            document.open();

            // --- ESTILOS ---
            Color rojoCorp = new Color(225, 29, 42);
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font subFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, rojoCorp);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9);

            // --- LOGO (Ruta relativa segura) ---
            try {
                Image logo = Image.getInstance(ReporteInventarioPDF.class.getResource("/Imagenes/Adecco_logo.png"));
                logo.scaleToFit(70, 70);
                logo.setAlignment(Image.ALIGN_RIGHT);
                document.add(logo);
            } catch (Exception e) { /* Logo opcional */ }

            // --- ENCABEZADO ---
            Paragraph p = new Paragraph("ESTADO ACTUAL DE INVENTARIO", tituloFont);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(new Paragraph("Sucursal: " + nombreSucursal.toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            document.add(new Paragraph("Fecha: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            document.add(new Paragraph(" "));

            // --- TABLA INSUMOS ---
            document.add(new Paragraph("1. INSUMOS", subFont));
            PdfPTable tableIns = new PdfPTable(5);
            tableIns.setWidthPercentage(100);
            String[] headsIns = {"Insumo", "U.M.", "Inicial", "Consumido", "Disponible"};
            for (String h : headsIns) {
                PdfPCell c = new PdfPCell(new Phrase(h, headFont));
                c.setBackgroundColor(rojoCorp);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableIns.addCell(c);
            }

            for (Inventario i : insumos) {
                tableIns.addCell(new Phrase(i.getInsumo().getNombre(), normalFont));
                tableIns.addCell(new Phrase(i.getInsumo().getUnidad(), normalFont));
                tableIns.addCell(crearCeldaDerecha(i.getCantidadInicial().toString(), normalFont));
                tableIns.addCell(crearCeldaDerecha(i.getCantidadInicial().subtract(i.getCantidadActual()).toString(), normalFont));
                tableIns.addCell(crearCeldaDerecha(i.getCantidadActual().toString(), normalFont));
            }
            document.add(tableIns);

            document.close();
            
            // 3. ABRIR PDF (SOLUCIONA ERROR DE CMD)
            System.out.println("PDF generado en: " + rutaFinal);
            // Nota: En móviles se usa una acción de compartir, no ProcessBuilder.

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String obtenerNombreSucursal() {
        try (Connection cn = DBConnection.getConnection()) {
            String sql = "SELECT nombre FROM sucursal WHERE id = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(Sesion.getUsuario().getSucursal()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("nombre");
        } catch (Exception e) { }
        return "Oficina Central";
    }

    private static PdfPCell crearCeldaDerecha(String texto, Font fuente) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }
}