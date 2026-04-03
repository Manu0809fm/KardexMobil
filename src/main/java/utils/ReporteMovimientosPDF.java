package utils;

import com.gluonhq.attach.storage.StorageService; // Requiere Gluon Attach Storage
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import model.MovimientoInventario;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReporteMovimientosPDF {

    public static void generar(List<MovimientoInventario> movimientos) {
        // Orientación horizontal para Kardex
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);

        try {
            // 1. OBTENER NOMBRE DE SUCURSAL
            String nombreSucursal = "Sucursal Desconocida";
            try (Connection cn = utils.DBConnection.getConnection()) {
                String sql = "SELECT nombre FROM sucursal WHERE id = ?";
                PreparedStatement ps = cn.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(utils.Sesion.getUsuario().getSucursal()));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nombreSucursal = rs.getString("nombre");
                }
            } catch (Exception e) {
                System.err.println("Error sucursal: " + e.getMessage());
            }

            // 2. CONFIGURACIÓN DE RUTA MÓVIL (Cambio Crítico)
            // StorageService detecta automáticamente si es Android, iOS o Desktop
            File carpeta = StorageService.create()
                    .flatMap(StorageService::getPrivateStorage)
                    .orElseThrow(() -> new RuntimeException("No se puede acceder al almacenamiento"));

            String nombreArchivo = "Kardex_" + System.currentTimeMillis() + ".pdf";
            File archivoPDF = new File(carpeta, nombreArchivo);
            
            PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
            document.open();

            // 3. COLORES Y FUENTES
            Color rojoCorporativo = new Color(225, 29, 42); 
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // 4. LOGO (Uso de recursos compatible)
            try {
                // En Gluon, los recursos se acceden mejor vía ClassLoader
                Image logo = Image.getInstance(ReporteMovimientosPDF.class.getResource("/Imagenes/Adecco_logo.png"));
                logo.scaleToFit(80, 80);
                logo.setAlignment(Element.ALIGN_RIGHT);
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Logo no encontrado en recursos.");
            }

            // 5. ENCABEZADO
            Paragraph p = new Paragraph("REPORTE DETALLADO DE MOVIMIENTOS (KARDEX)", titleFont);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            
            document.add(new Paragraph("Sucursal: " + nombreSucursal.toUpperCase(), 
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            document.add(new Paragraph("Fecha reporte: " + java.time.LocalDateTime.now().format(dtf), normalFont));
            document.add(new Paragraph(" "));

            // 6. TABLA KARDEX
            PdfPTable tabla = new PdfPTable(8);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{12, 15, 10, 8, 10, 10, 12, 23});

            String[] headers = {"FECHA", "ITEM", "TIPO", "CANT.", "LOTE", "VENC.", "RESPONSABLE", "REFERENCIA"};

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
                cell.setBackgroundColor(rojoCorporativo);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                cell.setBorderColor(Color.WHITE);
                tabla.addCell(cell);
            }

            // 7. LLENADO DE DATOS
            if (movimientos != null) {
                for (MovimientoInventario m : movimientos) {
                    tabla.addCell(new Phrase(m.getFecha() != null ? m.getFecha().format(dtf) : "---", normalFont));
                    
                    String itemLabel = m.getNombreItem() != null ? m.getNombreItem() : "ID: "
                            + (m.getProductoId() != null ? "P-" + m.getProductoId() : "I-" + m.getInsumoId());
                    tabla.addCell(new Phrase(itemLabel, normalFont));
                    
                    tabla.addCell(new Phrase(m.getTipoMovimiento(), normalFont));
                    
                    String cant = (m.getCantidadMovida() != null) ? String.format("%.2f", m.getCantidadMovida()) : "0.00";
                    PdfPCell cCant = new PdfPCell(new Phrase(cant, normalFont));
                    cCant.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    tabla.addCell(cCant);
                    
                    tabla.addCell(new Phrase(m.getLote() != null ? m.getLote() : "---", normalFont));
                    tabla.addCell(new Phrase(m.getVencimiento() != null ? m.getVencimiento() : "---", normalFont));
                    tabla.addCell(new Phrase(m.getColaborador() != null ? m.getColaborador() : "SISTEMA", normalFont));
                    tabla.addCell(new Phrase(m.getReferenciaDocumento() != null ? m.getReferenciaDocumento() : "---", normalFont));
                }
            }

            document.add(tabla);
            document.close();
            
            System.out.println("PDF generado exitosamente en: " + archivoPDF.getAbsolutePath());

            // NOTA: Para abrir el archivo en móvil, se recomienda usar ShareService 
            // para que el usuario elija con qué app abrirlo.
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}