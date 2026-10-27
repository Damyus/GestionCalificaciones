package com.mycompany.gestioncalificaciones.boletines;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.gestioncalificaciones.servicio.CalculadoraPromedios;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BoletinPDF implements Boletin {

    @Override
    public void generar(String estudiante, Map<String, List<Double>> notas, double promedioGeneral, String ruta) throws Exception {
        CalculadoraPromedios calc = new CalculadoraPromedios();
        Document documento = new Document();
        PdfWriter.getInstance(documento, new FileOutputStream(ruta));
        documento.open();

        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        documento.add(new Paragraph("Boletin de Calificaciones", fuenteTitulo));
        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Estudiante: " + estudiante));
        documento.add(new Paragraph("Fecha: " + LocalDate.now()));
        documento.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(3);
        tabla.addCell("Materia");
        tabla.addCell("Notas");
        tabla.addCell("Promedio");
        for (String materia : notas.keySet()) {
            tabla.addCell(materia);
            tabla.addCell(notas.get(materia).toString());
            tabla.addCell(String.format("%.2f", calc.promedio(notas.get(materia))));
        }
        documento.add(tabla);

        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Promedio general: " + String.format("%.2f", promedioGeneral), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        documento.close();
    }

    @Override
    public String getExtension() {
        return "pdf";
    }
}
