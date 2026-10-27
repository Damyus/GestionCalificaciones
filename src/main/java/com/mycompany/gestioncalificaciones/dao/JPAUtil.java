package com.mycompany.gestioncalificaciones.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// clase para no crear el EntityManagerFactory a cada rato
public class JPAUtil {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("CalificacionesPU");
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEmf().createEntityManager();
    }

    public static void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
