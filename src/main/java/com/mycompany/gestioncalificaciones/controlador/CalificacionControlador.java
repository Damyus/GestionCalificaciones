package com.mycompany.gestioncalificaciones.controlador;

import com.mycompany.gestioncalificaciones.boletines.Boletin;
import com.mycompany.gestioncalificaciones.boletines.BoletinFactory;
import com.mycompany.gestioncalificaciones.dao.CalificacionDAO;
import com.mycompany.gestioncalificaciones.excepciones.EstudianteSinNotasException;
import com.mycompany.gestioncalificaciones.excepciones.MateriaNoEncontradaException;
import com.mycompany.gestioncalificaciones.excepciones.NotaInvalidaException;
import com.mycompany.gestioncalificaciones.modelo.Calificacion;
import com.mycompany.gestioncalificaciones.servicio.CalculadoraPromedios;
import com.mycompany.gestioncalificaciones.util.Logger;
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

// el controlador une la vista con el DAO y la calculadora (MVC)
public class CalificacionControlador {

    private CalificacionDAO dao = new CalificacionDAO();
    private CalculadoraPromedios calc = new CalculadoraPromedios();
    private Logger log = Logger.getInstancia();

    public List<Calificacion> listar() {
        return dao.listarTodas();
    }

    public List<String> listarEstudiantes() {
        return dao.listarEstudiantes();
    }

    public List<Calificacion> listarPorEstudiante(String estudiante) {
        return dao.buscarPorEstudiante(estudiante);
    }

    public List<String> listarMaterias() {
        return dao.listarMaterias();
    }

    public List<Calificacion> filtrar(String estudiante, String materia) {
        return dao.filtrar(estudiante, materia);
    }

    public HashMap<String, Double> promediosPorEstudiante(List<Calificacion> lista) {
        return calc.promediosPorEstudiante(lista);
    }

    // promedio de todas las notas de una lista
    public double promedioDeLista(List<Calificacion> lista) {
        List<Double> notas = new java.util.ArrayList<>();
        for (Calificacion c : lista) {
            notas.add(c.getNota());
        }
        return calc.promedio(notas);
    }

    // pasa el texto a numero y lo valida
    public double convertirNota(String texto) throws NotaInvalidaException {
        double nota;
        try {
            nota = Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            log.error("Se ingreso una nota que no es numero: " + texto);
            throw new NotaInvalidaException("La nota tiene que ser un numero");
        }
        calc.validarNota(nota);
        return nota;
    }

    public void agregar(String estudiante, String materia, String notaTexto, LocalDate fecha) throws NotaInvalidaException {
        double nota = convertirNota(notaTexto);
        Calificacion c = new Calificacion(estudiante.trim(), materia.trim(), nota, fecha);
        dao.guardar(c);
        log.info("Se guardo la calificacion: " + c);
    }

    public void modificar(Calificacion c, String estudiante, String materia, String notaTexto, LocalDate fecha) throws NotaInvalidaException {
        double nota = convertirNota(notaTexto);
        c.setEstudiante(estudiante.trim());
        c.setMateria(materia.trim());
        c.setNota(nota);
        c.setFecha(fecha);
        dao.actualizar(c);
        log.info("Se modifico la calificacion id " + c.getId());
    }

    public void eliminar(Calificacion c) {
        dao.eliminar(c.getId());
        log.info("Se elimino la calificacion id " + c.getId());
    }

    public double promedioEstudiante(String estudiante) throws EstudianteSinNotasException {
        return calc.promedioEstudiante(dao.listarTodas(), estudiante);
    }

    public double promedioMateria(String materia) throws MateriaNoEncontradaException {
        return calc.promedioMateria(dao.listarTodas(), materia);
    }

    public HashMap<String, Double> promediosPorMateria(List<Calificacion> lista) {
        return calc.promediosPorMateria(lista);
    }

    // genera el boletin y devuelve donde se guardo
    public String generarBoletin(String estudiante, String tipo) throws Exception {
        List<Calificacion> lista = dao.buscarPorEstudiante(estudiante.trim());
        HashMap<String, List<Double>> notas = calc.notasPorMateria(lista, estudiante);
        double promedio = calc.promedioEstudiante(lista, estudiante);

        Boletin boletin = BoletinFactory.crearBoletin(tipo);

        File carpeta = new File("boletines");
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }
        String ruta = "boletines/boletin_" + estudiante.trim().replace(" ", "_") + "." + boletin.getExtension();
        boletin.generar(estudiante.trim(), notas, promedio, ruta);
        log.info("Boletin " + tipo + " generado en " + ruta);
        return new File(ruta).getAbsolutePath();
    }
}
