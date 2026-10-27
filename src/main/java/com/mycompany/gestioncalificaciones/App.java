package com.mycompany.gestioncalificaciones;

import com.mycompany.gestioncalificaciones.dao.JPAUtil;
import com.mycompany.gestioncalificaciones.vista.VentanaPrincipal;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        VentanaPrincipal ventana = new VentanaPrincipal();
        ventana.mostrar(stage);
    }

    @Override
    public void stop() {
        // cierro la conexion cuando se cierra la ventana
        JPAUtil.cerrar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
