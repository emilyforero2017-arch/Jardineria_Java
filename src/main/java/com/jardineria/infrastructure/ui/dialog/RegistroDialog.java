package com.jardineria.infrastructure.ui.dialog;

import java.util.ArrayList;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.stage.Screen;
import javafx.stage.FileChooser;

/** Formulario reutilizable para los datos de empleados y clientes. */
public final class RegistroDialog {
    private RegistroDialog() { }
    public record Campo(String nombre, String valor, boolean obligatorio, int longitud, String ayuda, boolean multilinea, boolean archivo) {
        public Campo(String nombre, String valor, boolean obligatorio, int longitud, String ayuda) {
            this(nombre, valor, obligatorio, longitud, ayuda, false, false);
        }
    }

    public static Optional<List<String>> mostrar(Window ventana, String entidad, boolean editando,
            List<Campo> campos, Function<List<String>, String> validar) {
        Dialog<List<String>> dialogo = new Dialog<>();
        dialogo.initOwner(ventana);
        ModalFondo.redondear(dialogo);
        dialogo.setTitle(editando ? "Editar " + entidad : "Nuevo registro");
        dialogo.setHeaderText(editando ? "Editar " + entidad : "Registrar " + entidad);
        dialogo.getDialogPane().getStylesheets().addAll(ventana.getScene().getStylesheets());
        dialogo.getDialogPane().getStyleClass().addAll("brand-dialog", "office-dialog");
        double anchoDisponible = Screen.getScreensForRectangle(ventana.getX(), ventana.getY(),
                ventana.getWidth(), ventana.getHeight()).stream().findFirst().orElse(Screen.getPrimary())
                .getVisualBounds().getWidth();
        double anchoFormulario = Math.min(860, anchoDisponible - 60);
        int columnas = anchoFormulario >= 720 ? 3 : 2;
        dialogo.getDialogPane().setPrefWidth(anchoFormulario);
        List<TextInputControl> entradas = new ArrayList<>();
        GridPane cuadricula = new GridPane();
        cuadricula.setHgap(20); cuadricula.setVgap(17); cuadricula.setPadding(new Insets(8, 6, 12, 2));
        for (int columna = 0; columna < columnas; columna++) {
            ColumnConstraints restriccion = new ColumnConstraints();
            restriccion.setPercentWidth(100.0 / columnas);
            restriccion.setHgrow(Priority.ALWAYS);
            cuadricula.getColumnConstraints().add(restriccion);
        }
        for (int i = 0; i < campos.size(); i++) {
            Campo dato = campos.get(i);
            // Conservamos la imagen existente, aunque no se muestre en el formulario.
            if ("gama de productos".equals(entidad) && dato.archivo()) {
                entradas.add(new TextField(dato.valor() == null ? "" : dato.valor()));
                continue;
            }
            TextInputControl entrada;
            if (dato.multilinea()) {
                TextArea area = new TextArea(dato.valor() == null ? "" : dato.valor());
                area.setWrapText(true);
                area.setPrefRowCount(3);
                entrada = area;
            } else {
                entrada = new TextField(dato.valor() == null ? "" : dato.valor());
            }
            entrada.setPromptText(dato.ayuda());
            entrada.setMinWidth(0);
            entrada.setMaxWidth(Double.MAX_VALUE);
            if (editando && (i == 0 || (("detalle de pedido".equals(entidad) || "pago".equals(entidad)) && i == 1))) entrada.setEditable(false);
            entradas.add(entrada);
            Label etiqueta = new Label(dato.nombre() + (dato.obligatorio() ? " *" : ""));
            etiqueta.getStyleClass().add("form-label");
            Node control = entrada;
            if (dato.archivo()) {
                entrada.setEditable(false);
                Button elegir = new Button("Elegir imagen");
                elegir.getStyleClass().add("secondary-button");
                elegir.setOnAction(evento -> {
                    FileChooser selector = new FileChooser();
                    selector.setTitle("Selecciona una imagen para la gama");
                    selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                            "Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
                    File elegida = selector.showOpenDialog(dialogo.getDialogPane().getScene().getWindow());
                    if (elegida != null) entrada.setText(elegida.getAbsolutePath());
                });
                Button quitar = new Button("Quitar");
                quitar.getStyleClass().add("secondary-button");
                quitar.setOnAction(evento -> entrada.clear());
                HBox filaImagen = new HBox(6, entrada, elegir, quitar);
                HBox.setHgrow(entrada, Priority.ALWAYS);
                control = filaImagen;
            }
            VBox grupo = new VBox(5, etiqueta, control);
            GridPane.setHgrow(grupo, Priority.ALWAYS);
            if (dato.archivo()) cuadricula.add(grupo, 0, (i + columnas - 1) / columnas, columnas, 1);
            else cuadricula.add(grupo, i % columnas, i / columnas);
        }
        Label indicacion = new Label("Los campos con * son obligatorios. Los códigos relacionados deben existir.");
        indicacion.getStyleClass().add("form-hint"); indicacion.setWrapText(true);
        ScrollPane desplazamiento = new ScrollPane(cuadricula);
        desplazamiento.getStyleClass().add("registro-scroll");
        desplazamiento.setFitToWidth(true);
        desplazamiento.setPrefViewportHeight(Math.min(430, ((campos.size() + columnas - 1) / columnas) * 95 + 20));
        desplazamiento.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        Label introduccion = new Label(editando ? "Actualiza la información y guarda los cambios." : "Completa la información para crear el registro.");
        introduccion.getStyleClass().add("form-intro");
        VBox cuerpo = new VBox(13, introduccion, desplazamiento, indicacion);
        dialogo.getDialogPane().setContent(cuerpo);
        ButtonType guardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getDialogPane().getButtonTypes().addAll(guardar, cancelar);
        Node boton = dialogo.getDialogPane().lookupButton(guardar);
        boton.getStyleClass().add("primary-button");
        dialogo.getDialogPane().lookupButton(cancelar).getStyleClass().add("secondary-button");
        boton.addEventFilter(ActionEvent.ACTION, evento -> {
            List<String> valores = entradas.stream().map(RegistroDialog::valor).toList();
            String error = null;
            for (int i = 0; i < campos.size(); i++) {
                Campo dato = campos.get(i); String valor = valores.get(i);
                if (dato.obligatorio() && valor.isEmpty()) { error = "Completa el campo «" + dato.nombre() + "»."; break; }
                if (dato.longitud() > 0 && valor.length() > dato.longitud()) { error = "«" + dato.nombre() + "» admite hasta " + dato.longitud() + " caracteres."; break; }
            }
            if (error == null) error = validar.apply(valores);
            if (error != null) {
                indicacion.setText(error);
                if (!indicacion.getStyleClass().contains("form-error")) indicacion.getStyleClass().add("form-error");
                evento.consume();
            }
        });
        dialogo.setResultConverter(tipo -> tipo == guardar ? entradas.stream().map(RegistroDialog::valor).toList() : null);
        return ModalFondo.mostrar(ventana, dialogo);
    }
    private static String valor(TextInputControl entrada) {
        return entrada instanceof TextArea ? entrada.getText() : entrada.getText().trim();
    }
}
