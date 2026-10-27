package com.mycompany.gestioncalificaciones.boletines;

import com.mycompany.gestioncalificaciones.servicio.CalculadoraPromedios;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public class BoletinCSV implements Boletin {

    @Override
    public void generar(String estudiante, Map<String, List<Double>> notas, double promedioGeneral, String ruta) throws Exception {
        CalculadoraPromedios calc = new CalculadoraPromedios();
        // uso ; porque el excel en español separa con punto y coma
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write("Estudiante;" + estudiante + "\n");
            fw.write("Materia;Notas;Promedio\n");
            for (String materia : notas.keySet()) {
                String listaNotas = "";
                for (Double n : notas.get(materia)) {
                    listaNotas = listaNotas + n + " ";
                }
                fw.write(materia + ";" + listaNotas.trim() + ";" + String.format("%.2f", calc.promedio(notas.get(materia))) + "\n");
            }
            fw.write("Promedio general;;" + String.format("%.2f", promedioGeneral) + "\n");
        }
    }

    @Override
    public String getExtension() {
        return "csv";
    }
}
