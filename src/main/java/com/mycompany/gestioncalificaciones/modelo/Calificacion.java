package com.mycompany.gestioncalificaciones.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "calificaciones")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String estudiante;

    @Column(nullable = false, length = 100)
    private String materia;

    @Column(nullable = false)
    private double nota;

    private LocalDate fecha;

    // constructor vacio que pide JPA
    public Calificacion() {
    }

    public Calificacion(String estudiante, String materia, double nota, LocalDate fecha) {
        this.estudiante = estudiante;
        this.materia = materia;
        this.nota = nota;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return estudiante + " - " + materia + " - " + nota + " (" + fecha + ")";
    }
}
