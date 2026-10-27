package com.mycompany.gestioncalificaciones.dao;

import com.mycompany.gestioncalificaciones.modelo.Calificacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class CalificacionDAO {

    public void guardar(Calificacion c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void actualizar(Calificacion c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(c);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Calificacion c = em.find(Calificacion.class, id);
            if (c != null) {
                em.remove(c);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Calificacion buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Calificacion.class, id);
        } finally {
            em.close();
        }
    }

    // consulta JPQL para traer todas
    public List<Calificacion> listarTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Calificacion c ORDER BY c.estudiante, c.materia", Calificacion.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // trae los nombres de los estudiantes sin repetir
    public List<String> listarEstudiantes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT c.estudiante FROM Calificacion c ORDER BY c.estudiante", String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // materias sin repetir
    public List<String> listarMaterias() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT c.materia FROM Calificacion c ORDER BY c.materia", String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // filtra por estudiante y/o materia.
    public List<Calificacion> filtrar(String estudiante, String materia) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Calificacion c WHERE 1=1";
            if (estudiante != null) {
                jpql += " AND c.estudiante = :est";
            }
            if (materia != null) {
                jpql += " AND c.materia = :mat";
            }
            jpql += " ORDER BY c.estudiante, c.materia";

            TypedQuery<Calificacion> consulta = em.createQuery(jpql, Calificacion.class);
            if (estudiante != null) {
                consulta.setParameter("est", estudiante);
            }
            if (materia != null) {
                consulta.setParameter("mat", materia);
            }
            return consulta.getResultList();
        } finally {
            em.close();
        }
    }

    // consulta JPQL con parametro
    public List<Calificacion> buscarPorEstudiante(String estudiante) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Calificacion c WHERE c.estudiante = :est ORDER BY c.materia", Calificacion.class)
                    .setParameter("est", estudiante)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
