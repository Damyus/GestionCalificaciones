package com.mycompany.gestioncalificaciones.boletines;


// Patron Factory: segun el tipo que le paso me devuelve el boletin que corresponde

public class BoletinFactory {

    public static Boletin crearBoletin(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tenes que elegir un formato de boletin");
        }
        switch (tipo.toUpperCase()) {
            case "PDF":
                return new BoletinPDF();
            case "CSV":
                return new BoletinCSV();
            default:
                throw new IllegalArgumentException("Formato de boletin no soportado: " + tipo);
        }
    }
}
