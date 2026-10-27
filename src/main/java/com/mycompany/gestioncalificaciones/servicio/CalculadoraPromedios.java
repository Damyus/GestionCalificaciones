package com.mycompany.gestioncalificaciones.servicio;

import com.mycompany.gestioncalificaciones.excepciones.EstudianteSinNotasException;
import com.mycompany.gestioncalificaciones.excepciones.MateriaNoEncontradaException;
import com.mycompany.gestioncalificaciones.excepciones.NotaInvalidaException;
import com.mycompany.gestioncalificaciones.modelo.Calificacion;
import com.mycompany.gestioncalificaciones.util.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// clase que hace todos los calculos de promedios
public class CalculadoraPromedios {

    private Logger log = Logger.getInstancia();

    public void validarNota(double nota) throws NotaInvalidaException {
        if (nota < 0 || nota > 10) {
            log.error("Nota invalida: " + nota);
            throw new NotaInvalidaException("La nota tiene que estar entre 0 y 10 (se ingreso " + nota + ")");
        }
    }

    public double promedio(List<Double> notas) {
        if (notas == null || notas.isEmpty()) {
            return 0;
        }
        double suma = 0;
        for (Double n : notas) {
            suma = suma + n;
        }
        return suma / notas.size();
    }

    // arma el HashMap materia -> lista de notas de un estudiante
    public HashMap<String, List<Double>> notasPorMateria(List<Calificacion> lista, String estudiante)
            throws EstudianteSinNotasException {
        HashMap<String, List<Double>> mapa = new HashMap<>();
        for (Calificacion c : lista) {
            if (c.getEstudiante().equalsIgnoreCase(estudiante.trim())) {
                if (!mapa.containsKey(c.getMateria())) {
                    mapa.put(c.getMateria(), new ArrayList<>());
                }
                mapa.get(c.getMateria()).add(c.getNota());
            }
        }
        if (mapa.isEmpty()) {
            log.error("El estudiante " + estudiante + " no tiene notas");
            throw new EstudianteSinNotasException("El estudiante " + estudiante + " no tiene notas cargadas");
        }
        return mapa;
    }

    public double promedioEstudiante(List<Calificacion> lista, String estudiante)
            throws EstudianteSinNotasException {
        HashMap<String, List<Double>> mapa = notasPorMateria(lista, estudiante);
        List<Double> todas = new ArrayList<>();
        for (List<Double> notas : mapa.values()) {
            todas.addAll(notas);
        }
        double p = promedio(todas);
        log.info("Promedio calculado de " + estudiante + ": " + p);
        return p;
    }

    public double promedioMateria(List<Calificacion> lista, String materia)
            throws MateriaNoEncontradaException {
        List<Double> notas = new ArrayList<>();
        for (Calificacion c : lista) {
            if (c.getMateria().equalsIgnoreCase(materia.trim())) {
                notas.add(c.getNota());
            }
        }
        if (notas.isEmpty()) {
            log.error("No se encontro la materia " + materia);
            throw new MateriaNoEncontradaException("No se encontro la materia " + materia);
        }
        double p = promedio(notas);
        log.info("Promedio calculado de la materia " + materia + ": " + p);
        return p;
    }

    // promedio de cada estudiante (para el grafico cuando se filtra por materia)
    public HashMap<String, Double> promediosPorEstudiante(List<Calificacion> lista) {
        HashMap<String, List<Double>> mapa = new HashMap<>();
        for (Calificacion c : lista) {
            if (!mapa.containsKey(c.getEstudiante())) {
                mapa.put(c.getEstudiante(), new ArrayList<>());
            }
            mapa.get(c.getEstudiante()).add(c.getNota());
        }
        HashMap<String, Double> resultado = new HashMap<>();
        for (String estudiante : mapa.keySet()) {
            resultado.put(estudiante, promedio(mapa.get(estudiante)));
        }
        return resultado;
    }

    // promedio de cada materia, lo uso para el grafico de barras
    public HashMap<String, Double> promediosPorMateria(List<Calificacion> lista) {
        HashMap<String, List<Double>> mapa = new HashMap<>();
        for (Calificacion c : lista) {
            if (!mapa.containsKey(c.getMateria())) {
                mapa.put(c.getMateria(), new ArrayList<>());
            }
            mapa.get(c.getMateria()).add(c.getNota());
        }
        HashMap<String, Double> resultado = new HashMap<>();
        for (String materia : mapa.keySet()) {
            resultado.put(materia, promedio(mapa.get(materia)));
        }
        return resultado;
    }
}
