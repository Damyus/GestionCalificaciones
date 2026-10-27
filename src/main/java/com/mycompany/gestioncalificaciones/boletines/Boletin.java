package com.mycompany.gestioncalificaciones.boletines;

import java.util.List;
import java.util.Map;

public interface Boletin {

    void generar(String estudiante, Map<String, List<Double>> notas, double promedioGeneral, String ruta) throws Exception;

    String getExtension();
}
