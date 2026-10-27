package com.mycompany.gestioncalificaciones.vista;

import com.mycompany.gestioncalificaciones.controlador.CalificacionControlador;
import com.mycompany.gestioncalificaciones.excepciones.EstudianteSinNotasException;
import com.mycompany.gestioncalificaciones.excepciones.MateriaNoEncontradaException;
import com.mycompany.gestioncalificaciones.excepciones.NotaInvalidaException;
import com.mycompany.gestioncalificaciones.modelo.Calificacion;
import com.mycompany.gestioncalificaciones.util.Logger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VentanaPrincipal {

    private CalificacionControlador controlador = new CalificacionControlador();
    private Logger log = Logger.getInstancia();

    // tabla
    private TableView<Calificacion> tabla = new TableView<>();
    private ObservableList<Calificacion> datos = FXCollections.observableArrayList();

    // formulario
    private TextField txtEstudiante = new TextField();
    private TextField txtMateria = new TextField();
    private TextField txtNota = new TextField();
    private DatePicker dpFecha = new DatePicker(LocalDate.now());
    private ComboBox<String> cbFormato = new ComboBox<>();

    // filtros
    private ComboBox<String> cbEstudiante = new ComboBox<>();
    private ComboBox<String> cbMateria = new ComboBox<>();
    private Label lblPromedio = new Label("");
    private boolean cargandoCombos = false; // para que no se dispare el filtro mientras lleno los combos

    // grafico
    private VBox cajaGrafico = new VBox(8);
    private FlowPane nombresBarras = new FlowPane(14, 6); // los nombres que van debajo del grafico

    // aviso tipo post-it que se anima
    private Label lblAviso = new Label("");
    private SequentialTransition animacionAviso;

    public void mostrar(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(crearEncabezado());
        root.setLeft(crearFormulario());
        root.setCenter(crearTabla());
        root.setRight(crearGrafico());

        // el post-it arranca escondido afuera de la ventana (a la derecha)
        lblAviso.getStyleClass().add("postit");
        lblAviso.setRotate(-2);
        lblAviso.setTranslateX(700);
        HBox pie = new HBox(lblAviso);
        pie.setAlignment(Pos.CENTER_RIGHT);
        pie.setPadding(new Insets(0, 30, 14, 0));
        pie.setMinHeight(56);
        root.setBottom(pie);

        Scene scene = new Scene(root, 1320, 720);
        scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        stage.setTitle("Libreta de calificaciones");
        stage.setScene(scene);
        stage.show();

        cargarDatos();
    }

    private VBox crearEncabezado() {
        Label titulo = new Label("Libreta de calificaciones");
        titulo.getStyleClass().add("titulo");
        Label subtitulo = new Label("Programación III - IPESMI 2026");
        subtitulo.getStyleClass().add("subtitulo-encabezado");
        VBox encabezado = new VBox(2, titulo, subtitulo);
        encabezado.getStyleClass().add("encabezado");
        return encabezado;
    }

    private VBox crearFormulario() {
        Label lblCargar = new Label("Cargar nota");
        lblCargar.getStyleClass().add("seccion");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(10);
        grid.add(new Label("Estudiante"), 0, 0);
        grid.add(txtEstudiante, 1, 0);
        grid.add(new Label("Materia"), 0, 1);
        grid.add(txtMateria, 1, 1);
        grid.add(new Label("Nota"), 0, 2);
        grid.add(txtNota, 1, 2);
        grid.add(new Label("Fecha"), 0, 3);
        grid.add(dpFecha, 1, 3);

        txtEstudiante.setPromptText("Ej: Juan Perez");
        txtMateria.setPromptText("Ej: Matemática");
        txtNota.setPromptText("Del 0 al 10");
        dpFecha.setMaxWidth(Double.MAX_VALUE);

        Button btnGuardar = new Button("Guardar nota");
        Button btnModificar = new Button("Guardar cambios");
        Button btnEliminar = new Button("Borrar");
        Button btnLimpiar = new Button("Limpiar");
        btnGuardar.getStyleClass().add("boton");
        btnModificar.getStyleClass().add("boton-secundario");
        btnEliminar.getStyleClass().add("boton-borrar");
        btnLimpiar.getStyleClass().add("boton-secundario");
        ancho(btnGuardar, btnModificar, btnEliminar, btnLimpiar);

        btnGuardar.setOnAction(e -> guardar());
        btnModificar.setOnAction(e -> modificar());
        btnEliminar.setOnAction(e -> eliminar());
        btnLimpiar.setOnAction(e -> limpiar());

        Label ayuda = new Label("Para editar o borrar, tocá una fila de la tabla.");
        ayuda.getStyleClass().add("ayuda");
        ayuda.setWrapText(true);

        // promedios
        Label lblPromedios = new Label("Consultar promedio");
        lblPromedios.getStyleClass().add("seccion");
        Button btnPromEstudiante = new Button("Del estudiante");
        Button btnPromMateria = new Button("De la materia");
        btnPromEstudiante.getStyleClass().add("boton-secundario");
        btnPromMateria.getStyleClass().add("boton-secundario");
        ancho(btnPromEstudiante, btnPromMateria);
        btnPromEstudiante.setOnAction(e -> promedioEstudiante());
        btnPromMateria.setOnAction(e -> promedioMateria());

        // boletin
        Label lblBoletin = new Label("Boletín");
        lblBoletin.getStyleClass().add("seccion");
        cbFormato.getItems().addAll("PDF", "CSV");
        cbFormato.setValue("PDF");
        Button btnBoletin = new Button("Generar boletín");
        btnBoletin.getStyleClass().add("boton");
        btnBoletin.setOnAction(e -> generarBoletin());
        HBox filaBoletin = new HBox(8, cbFormato, btnBoletin);
        HBox.setHgrow(btnBoletin, Priority.ALWAYS);
        btnBoletin.setMaxWidth(Double.MAX_VALUE);

        VBox caja = new VBox(10,
                lblCargar, grid,
                fila(btnGuardar, btnModificar),
                fila(btnEliminar, btnLimpiar),
                ayuda,
                new Separator(),
                lblPromedios,
                fila(btnPromEstudiante, btnPromMateria),
                new Separator(),
                lblBoletin,
                filaBoletin);
        caja.getStyleClass().add("panel");
        caja.setPrefWidth(320);
        BorderPane.setMargin(caja, new Insets(16, 0, 0, 16));
        return caja;
    }

    // para que los botones ocupen todo el ancho
    private void ancho(Button... botones) {
        for (Button b : botones) {
            b.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(b, Priority.ALWAYS);
        }
    }

    private HBox fila(Button b1, Button b2) {
        return new HBox(8, b1, b2);
    }

    private VBox crearTabla() {
        TableColumn<Calificacion, Long> colId = new TableColumn<>("N°");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(55);
        colId.getStyleClass().add("col-margen");

        TableColumn<Calificacion, String> colEstudiante = new TableColumn<>("Estudiante");
        colEstudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));

        TableColumn<Calificacion, String> colMateria = new TableColumn<>("Materia");
        colMateria.setCellValueFactory(new PropertyValueFactory<>("materia"));

        TableColumn<Calificacion, Double> colNota = new TableColumn<>("Nota");
        colNota.setCellValueFactory(new PropertyValueFactory<>("nota"));
        colNota.setMaxWidth(80);

        TableColumn<Calificacion, LocalDate> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        tabla.getColumns().addAll(colId, colEstudiante, colMateria, colNota, colFecha);
        tabla.setItems(datos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPlaceholder(new Label("Todavía no hay notas. Cargá la primera desde el formulario."));
        tabla.getStyleClass().add("tabla-cuaderno");

        // cuando selecciono una fila se llenan los campos
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                txtEstudiante.setText(nuevo.getEstudiante());
                txtMateria.setText(nuevo.getMateria());
                txtNota.setText(String.valueOf(nuevo.getNota()));
                dpFecha.setValue(nuevo.getFecha());
            }
        });

        // filtros (cada nombre aparece una sola vez en los combos)
        cbEstudiante.setPrefWidth(180);
        cbMateria.setPrefWidth(160);
        cbEstudiante.setOnAction(e -> {
            if (!cargandoCombos) {
                filtrar();
            }
        });
        cbMateria.setOnAction(e -> {
            if (!cargandoCombos) {
                filtrar();
            }
        });
        Button btnQuitarFiltros = new Button("Ver todo");
        btnQuitarFiltros.getStyleClass().add("boton-link");
        btnQuitarFiltros.setOnAction(e -> {
            cargandoCombos = true;
            cbEstudiante.setValue("Todos");
            cbMateria.setValue("Todas");
            cargandoCombos = false;
            filtrar();
        });

        HBox filtros = new HBox(10, new Label("Estudiante"), cbEstudiante,
                new Label("Materia"), cbMateria, btnQuitarFiltros);
        filtros.setAlignment(Pos.CENTER_LEFT);

        lblPromedio.getStyleClass().add("promedio");

        Label lbl = new Label("Notas registradas");
        lbl.getStyleClass().add("seccion");

        VBox caja = new VBox(10, lbl, filtros, lblPromedio, tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        caja.setPadding(new Insets(16, 16, 0, 16));
        return caja;
    }

    private VBox crearGrafico() {
        // referencias de colores
        HBox referencias = new HBox(16,
                referencia("aprobado", "Aprobado (6 o más)"),
                referencia("desaprobado", "Desaprobado (menos de 6)"));
        referencias.setAlignment(Pos.CENTER);

        Label lblBarras = new Label("Qué representa cada barra (de izquierda a derecha):");
        lblBarras.getStyleClass().add("ayuda");

        // el grafico en si se agrega en actualizarGrafico (posicion 0)
        cajaGrafico.getChildren().addAll(new Label(""), lblBarras, nombresBarras, new Separator(), referencias);
        cajaGrafico.getStyleClass().add("panel");
        cajaGrafico.setPrefWidth(470);
        BorderPane.setMargin(cajaGrafico, new Insets(16, 16, 0, 0));
        return cajaGrafico;
    }

    private HBox referencia(String clase, String texto) {
        Region cuadrito = new Region();
        cuadrito.setPrefSize(12, 12);
        cuadrito.setMinSize(12, 12);
        cuadrito.getStyleClass().add(clase);
        HBox h = new HBox(6, cuadrito, new Label(texto));
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private void cargarDatos() {
        try {
            String estudianteElegido = cbEstudiante.getValue();
            String materiaElegida = cbMateria.getValue();

            cargandoCombos = true;
            llenarCombo(cbEstudiante, "Todos", controlador.listarEstudiantes(), estudianteElegido);
            llenarCombo(cbMateria, "Todas", controlador.listarMaterias(), materiaElegida);
            cargandoCombos = false;

            filtrar();
        } catch (Exception e) {
            cargandoCombos = false;
            e.printStackTrace();
            log.error("Error al cargar datos: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                    "No se pudo conectar con la base de datos.\nFijate que MySQL esté encendido y que exista la base calificaciones_db.");
        }
    }

    private void llenarCombo(ComboBox<String> combo, String primero, List<String> lista, String elegido) {
        combo.getItems().clear();
        combo.getItems().add(primero);
        combo.getItems().addAll(lista);
        if (elegido != null && combo.getItems().contains(elegido)) {
            combo.setValue(elegido);
        } else {
            combo.setValue(primero);
        }
    }

    // devuelve null si esta en "Todos"/"Todas"
    private String valorFiltro(ComboBox<String> combo) {
        String v = combo.getValue();
        if (v == null || v.equals("Todos") || v.equals("Todas")) {
            return null;
        }
        return v;
    }

    private void filtrar() {
        String estudiante = valorFiltro(cbEstudiante);
        String materia = valorFiltro(cbMateria);
        try {
            datos.setAll(controlador.filtrar(estudiante, materia));

            if (datos.isEmpty()) {
                lblPromedio.setText("No hay notas con ese filtro.");
            } else if (estudiante != null && materia != null) {
                lblPromedio.setText("Promedio de " + estudiante + " en " + materia + ": "
                        + String.format("%.2f", controlador.promedioDeLista(datos)));
            } else if (estudiante != null) {
                lblPromedio.setText("Promedio general de " + estudiante + ": "
                        + String.format("%.2f", controlador.promedioEstudiante(estudiante)));
            } else if (materia != null) {
                lblPromedio.setText("Promedio del curso en " + materia + ": "
                        + String.format("%.2f", controlador.promedioMateria(materia)));
            } else {
                lblPromedio.setText("Promedio general del curso: "
                        + String.format("%.2f", controlador.promedioDeLista(datos)));
            }
            actualizarGrafico(estudiante, materia);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo filtrar: " + e.getMessage());
        }
    }

    private void actualizarGrafico(String estudiante, String materia) {
        // creo el grafico de nuevo cada vez porque si reutilizaba el mismo
        // los nombres de abajo se desacomodaban o no aparecian
        CategoryAxis ejeX = new CategoryAxis();
        NumberAxis ejeY = new NumberAxis(0, 10, 1);
        ejeY.setLabel("Promedio");
        BarChart<String, Number> grafico = new BarChart<>(ejeX, ejeY);
        grafico.setLegendVisible(false);
        grafico.setAnimated(false);
        grafico.setCategoryGap(25);
        VBox.setVgrow(grafico, Priority.ALWAYS);

        HashMap<String, Double> promedios;
        if (materia != null) {
            // si filtro por materia, muestro cada estudiante en esa materia
            promedios = controlador.promediosPorEstudiante(datos);
            ejeX.setLabel("Estudiante");
            grafico.setTitle("Promedio de cada estudiante en " + materia);
        } else {
            promedios = controlador.promediosPorMateria(datos);
            ejeX.setLabel("Materia");
            if (estudiante != null) {
                grafico.setTitle("Promedios de " + estudiante + " por materia");
            } else {
                grafico.setTitle("Promedio de cada materia");
            }
        }

        // ordeno alfabeticamente para que no salgan mezclados
        ArrayList<String> nombres = new ArrayList<>(promedios.keySet());
        Collections.sort(nombres);

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        nombresBarras.getChildren().clear();
        for (String nombre : nombres) {
            double valor = promedios.get(nombre);
            XYChart.Data<String, Number> barra = new XYChart.Data<>(nombre, valor);
            barra.setNode(crearBarra(nombre, valor));
            serie.getData().add(barra);

            // nombre debajo del grafico con su color y su promedio
            Region cuadrito = new Region();
            cuadrito.setMinSize(12, 12);
            cuadrito.setPrefSize(12, 12);
            cuadrito.getStyleClass().add(valor >= 6 ? "aprobado" : "desaprobado");
            Label lblNombre = new Label(nombre + ": " + String.format("%.2f", valor));
            HBox item = new HBox(6, cuadrito, lblNombre);
            item.setAlignment(Pos.CENTER_LEFT);
            nombresBarras.getChildren().add(item);
        }
        if (nombres.isEmpty()) {
            nombresBarras.getChildren().add(new Label("No hay datos para mostrar."));
        }
        grafico.getData().add(serie);

        // reemplazo el grafico viejo por el nuevo
        cajaGrafico.getChildren().set(0, grafico);
    }

    // cada barra lleva el numero arriba y se pinta verde o rojo
    private StackPane crearBarra(String nombre, double valor) {
        Label numero = new Label(String.format("%.1f", valor));
        numero.getStyleClass().add("numero-barra");
        StackPane barra = new StackPane(numero);
        StackPane.setAlignment(numero, Pos.TOP_CENTER);
        if (valor >= 6) {
            barra.setStyle("-fx-background-color: #4f8a5f;");
        } else {
            barra.setStyle("-fx-background-color: #c8553d;");
        }
        Tooltip.install(barra, new Tooltip(nombre + ": " + String.format("%.2f", valor)));
        return barra;
    }

    // si escribio un nombre en el formulario uso ese, sino el del filtro
    private String estudianteElegido() {
        if (!txtEstudiante.getText().trim().isEmpty()) {
            return txtEstudiante.getText().trim();
        }
        return valorFiltro(cbEstudiante);
    }

    private String materiaElegida() {
        if (!txtMateria.getText().trim().isEmpty()) {
            return txtMateria.getText().trim();
        }
        return valorFiltro(cbMateria);
    }

    private boolean camposCompletos() {
        if (txtEstudiante.getText().trim().isEmpty() || txtMateria.getText().trim().isEmpty()
                || txtNota.getText().trim().isEmpty() || dpFecha.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Completá estudiante, materia, nota y fecha.");
            return false;
        }
        return true;
    }

    private void guardar() {
        if (!camposCompletos()) {
            return;
        }
        try {
            controlador.agregar(txtEstudiante.getText(), txtMateria.getText(), txtNota.getText(), dpFecha.getValue());
            cargarDatos();
            limpiar();
            mostrarAviso("Nota guardada");
        } catch (NotaInvalidaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Nota inválida", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    private void modificar() {
        Calificacion seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Elegí una nota", "Tocá una fila de la tabla para editarla.");
            return;
        }
        if (!camposCompletos()) {
            return;
        }
        try {
            controlador.modificar(seleccionada, txtEstudiante.getText(), txtMateria.getText(), txtNota.getText(), dpFecha.getValue());
            cargarDatos();
            limpiar();
            mostrarAviso("Cambios guardados");
        } catch (NotaInvalidaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Nota inválida", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo modificar: " + e.getMessage());
        }
    }

    private void eliminar() {
        Calificacion seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Elegí una nota", "Tocá una fila de la tabla para borrarla.");
            return;
        }
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Borrar nota");
        confirmar.setHeaderText(null);
        confirmar.setContentText("¿Borrar la nota de " + seleccionada.getEstudiante() + " en " + seleccionada.getMateria() + "?");
        Optional<ButtonType> respuesta = confirmar.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                controlador.eliminar(seleccionada);
                cargarDatos();
                limpiar();
                mostrarAviso("Nota borrada");
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo borrar: " + e.getMessage());
            }
        }
    }

    private void promedioEstudiante() {
        String estudiante = estudianteElegido();
        if (estudiante == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Falta el estudiante", "Elegí un estudiante en el filtro o escribí el nombre.");
            return;
        }
        try {
            double p = controlador.promedioEstudiante(estudiante);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Promedio",
                    "El promedio de " + estudiante + " es " + String.format("%.2f", p));
        } catch (EstudianteSinNotasException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin notas", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void promedioMateria() {
        String materia = materiaElegida();
        if (materia == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Falta la materia", "Elegí una materia en el filtro o escribí el nombre.");
            return;
        }
        try {
            double p = controlador.promedioMateria(materia);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Promedio",
                    "El promedio del curso en " + materia + " es " + String.format("%.2f", p));
        } catch (MateriaNoEncontradaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Materia no encontrada", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void generarBoletin() {
        String estudiante = estudianteElegido();
        if (estudiante == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Falta el estudiante", "Elegí un estudiante en el filtro o escribí el nombre.");
            return;
        }
        try {
            String ruta = controlador.generarBoletin(estudiante, cbFormato.getValue());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Boletín generado", "Se guardó en:\n" + ruta);
            mostrarAviso("Boletín " + cbFormato.getValue() + " listo");
        } catch (EstudianteSinNotasException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sin notas", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error al generar boletin: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo generar el boletín: " + e.getMessage());
        }
    }

    private void limpiar() {
        tabla.getSelectionModel().clearSelection();
        txtEstudiante.clear();
        txtMateria.clear();
        txtNota.clear();
        dpFecha.setValue(LocalDate.now());
    }

    /*
     * Animacion con TranslateTransition: el post-it entra desde la derecha,
     * se queda 2,5 segundos y despues se vuelve a ir.
     */
    private void mostrarAviso(String texto) {
        if (animacionAviso != null) {
            animacionAviso.stop();
        }
        lblAviso.setText(texto);

        TranslateTransition entrar = new TranslateTransition(Duration.millis(500), lblAviso);
        entrar.setFromX(700);
        entrar.setToX(0);

        PauseTransition esperar = new PauseTransition(Duration.seconds(2.5));

        TranslateTransition salir = new TranslateTransition(Duration.millis(500), lblAviso);
        salir.setToX(700);

        animacionAviso = new SequentialTransition(entrar, esperar, salir);
        animacionAviso.play();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
