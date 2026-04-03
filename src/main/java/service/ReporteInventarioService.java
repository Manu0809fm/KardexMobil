package service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import model.Inventario;
import model.Producto;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReporteInventarioService {

    public static void generarReporte(List<Inventario> inventarios, List<Producto> productos) {
        String userHome = System.getProperty("user.home");
        String ruta = userHome + File.separator + "reporte_inventario_" + System.currentTimeMillis() + ".pdf";

        Document doc = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));
            doc.open();

            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fuenteHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            Paragraph titulo = new Paragraph("REPORTE MAESTRO DE INVENTARIO", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(new Paragraph("Generado el: " + sdf.format(new Date())));
            doc.add(new Paragraph("\n"));

            doc.add(new Paragraph("1. INVENTARIO DE INSUMOS", fuenteHeader));
            doc.add(new Paragraph("\n"));

            PdfPTable tablaInsumos = new PdfPTable(4);
            tablaInsumos.setWidthPercentage(100);

            agregarCeldaHeader(tablaInsumos, "Insumo");
            agregarCeldaHeader(tablaInsumos, "Unidad");
            agregarCeldaHeader(tablaInsumos, "Stock Inicial");
            agregarCeldaHeader(tablaInsumos, "Stock Actual");

            if (inventarios != null) {
                for (Inventario i : inventarios) {
                    String nombreInsumo = (i.getInsumo() != null) ? i.getInsumo().getNombre() : "N/A";
                    String unidadInsumo = (i.getInsumo() != null) ? i.getInsumo().getUnidad() : "---";

                    tablaInsumos.addCell(nombreInsumo);
                    tablaInsumos.addCell(unidadInsumo);
                    // Uso de la función auxiliar para evitar errores de nulo y casteo
                    tablaInsumos.addCell(formatearNumero(i.getCantidadInicial()));
                    tablaInsumos.addCell(formatearNumero(i.getCantidadActual()));
                }
            }
            doc.add(tablaInsumos);
            doc.add(new Paragraph("\n"));

            doc.add(new Paragraph("2. INVENTARIO DE PRODUCTOS TERMINADOS", fuenteHeader));
            doc.add(new Paragraph("\n"));

            PdfPTable tablaProductos = new PdfPTable(3);
            tablaProductos.setWidthPercentage(100);

            agregarCeldaHeader(tablaProductos, "Producto");
            agregarCeldaHeader(tablaProductos, "Precio Unit.");
            agregarCeldaHeader(tablaProductos, "Stock Disponible");

            if (productos != null) {
                for (Producto p : productos) {
                    tablaProductos.addCell(p.getNombre() != null ? p.getNombre() : "Sin nombre");

                    String precioStr = "S/ " + formatearNumero(p.getPrecio());
                    tablaProductos.addCell(precioStr);

                    tablaProductos.addCell(formatearNumero(p.getStock()));
                }
            }

            doc.add(tablaProductos);
            doc.close();

            System.out.println("Reporte generado exitosamente en: " + ruta);

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String formatearNumero(Object valor) {
        if (valor == null) {
            return "0.00";
        }
        try {
            if (valor instanceof Number) {
                return String.format("%.2f", ((Number) valor).doubleValue());
            }
            return valor.toString();
        } catch (Exception e) {
            return "0.00";
        }
    }

    private static void agregarCeldaHeader(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setGrayFill(0.9f);
        celda.setPadding(5);
        tabla.addCell(celda);
    }
}
