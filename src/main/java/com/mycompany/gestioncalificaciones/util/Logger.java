package com.mycompany.gestioncalificaciones.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
// Patron Logger de singleton, un logger para toda la estancia
public class Logger {

    private static Logger instancia;

    private ArrayList<String> registros = new ArrayList<>();
    private String archivo = "registro.log";
    private DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // constructor privado para que nadie haga new Logger()
    private Logger() {
    }

    public static synchronized Logger getInstancia() {
        if (instancia == null) {
            instancia = new Logger();
        }
        return instancia;
    }

    public void info(String mensaje) {
        escribir("INFO", mensaje);
    }

    public void error(String mensaje) {
        escribir("ERROR", mensaje);
    }

    private void escribir(String nivel, String mensaje) {
        String linea = "[" + LocalDateTime.now().format(formato) + "] " + nivel + ": " + mensaje;
        registros.add(linea);
        System.out.println(linea);
        try (FileWriter fw = new FileWriter(archivo, true)) {
            fw.write(linea + "\n");
        } catch (IOException e) {
            System.out.println("No se pudo escribir en el archivo de log");
        }
    }

    public ArrayList<String> getRegistros() {
        return registros;
    }
}
