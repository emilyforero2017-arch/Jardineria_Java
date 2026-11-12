package com.jardineria.infrastructure.ui.dialog;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/** Crea mensajes y confirmaciones con la misma identidad visual de la aplicación. */
public final class AvisoDialogo {
    private AvisoDialogo() { }

    public static boolean confirmarEliminacion(Window ventana, String codigo, String ciudad) {
        Dialog<ButtonType> dialogo = crear(ventana, "ELIMINAR OFICINA", "¿Eliminar esta oficina?",
                "Vas a eliminar " + codigo + " · " + ciudad + ". Esta acción no se puede deshacer.", "warning");
        return confirmar(dialogo);
    }

    public static boolean confirmarEliminacionRegistro(Window ventana, String entidad, String descripcion) {
        Dialog<ButtonType> dialogo = crear(ventana, "CONFIRMAR ELIMINACIÓN", "¿Eliminar " + entidad + "?",
                "Vas a eliminar " + descripcion + ". Esta acción no se puede deshacer.", "warning");
        return confirmar(dialogo);
    }

    private static boolean confirmar(Dialog<ButtonType> dialogo) {
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType eliminar = new ButtonType("Sí, eliminar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(cancelar, eliminar);
        estilo(dialogo, cancelar, "secondary-button");
        estilo(dialogo, eliminar, "danger-confirm-button");
        Optional<ButtonType> respuesta = ModalFondo.mostrar(dialogo.getOwner(), dialogo);
        return respuesta.isPresent() && respuesta.get() == eliminar;
    }

    public static void mostrar(Window ventana, Alert.AlertType tipo, String titulo, String mensaje) {
        String clase = tipo == Alert.AlertType.ERROR ? "error" :
                tipo == Alert.AlertType.WARNING ? "warning" : "success";
        String etiqueta = tipo == Alert.AlertType.ERROR ? "ALGO NO SALIÓ BIEN" :
                tipo == Alert.AlertType.WARNING ? "TEN EN CUENTA" : "TODO LISTO";
        Dialog<ButtonType> dialogo = crear(ventana, etiqueta, titulo, mensaje, clase);
        ButtonType aceptar = new ButtonType("Entendido", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().add(aceptar);
        estilo(dialogo, aceptar, "primary-button");
        ModalFondo.mostrar(ventana, dialogo);
    }

    private static Dialog<ButtonType> crear(Window ventana, String etiqueta, String titulo,
            String mensaje, String tipo) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.initOwner(ventana);
        ModalFondo.redondear(dialogo);
        dialogo.setTitle(titulo);
        dialogo.getDialogPane().getStylesheets().addAll(ventana.getScene().getStylesheets());
        dialogo.getDialogPane().getStyleClass().addAll("brand-dialog", "notice-" + tipo);
        dialogo.getDialogPane().setPrefWidth(440);
        Label ceja = new Label(etiqueta);
        ceja.getStyleClass().add("dialog-eyebrow");
        Label encabezado = new Label(titulo);
        encabezado.getStyleClass().add("dialog-title");
        encabezado.setWrapText(true);
        Label detalle = new Label(mensaje);
        detalle.getStyleClass().add("dialog-message");
        detalle.setWrapText(true);
        VBox contenido = new VBox(12, ceja, encabezado, detalle);
        contenido.getStyleClass().add("notice-content");
        dialogo.getDialogPane().setContent(contenido);
        return dialogo;
    }

    private static void estilo(Dialog<ButtonType> dialogo, ButtonType tipo, String clase) {
        ((Button) dialogo.getDialogPane().lookupButton(tipo)).getStyleClass().add(clase);
    }
}
