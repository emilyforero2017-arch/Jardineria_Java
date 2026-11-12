package com.jardineria.infrastructure.ui.dialog;

import com.jardineria.domain.entity.oficina;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/** Formulario compartido para registrar y editar oficinas. */
public final class OficinaDialog {
    private OficinaDialog() { }

    public static Optional<oficina> mostrar(Window ventana, oficina actual) {
        boolean editando = actual != null;
        Dialog<oficina> dialogo = new Dialog<>();
        dialogo.initOwner(ventana);
        ModalFondo.redondear(dialogo);
        dialogo.setTitle(editando ? "Editar oficina" : "Nueva oficina");
        dialogo.setHeaderText(editando ? "Editar datos de la oficina" : "Registrar una nueva oficina");
        dialogo.setResizable(true);
        dialogo.getDialogPane().getStylesheets().addAll(ventana.getScene().getStylesheets());
        dialogo.getDialogPane().getStyleClass().addAll("brand-dialog", "office-dialog");

        TextField codigo = campo(actual == null ? null : actual.getCodigoOficina(), "Ej. BCN-ES");
        TextField ciudad = campo(actual == null ? null : actual.getCiudad(), "Ciudad de la oficina");
        TextField pais = campo(actual == null ? null : actual.getPais(), "País");
        TextField region = campo(actual == null ? null : actual.getRegion(), "Opcional");
        TextField postal = campo(actual == null ? null : actual.getCodigoPostal(), "Código postal");
        TextField telefono = campo(actual == null ? null : actual.getTelefono(), "Teléfono de contacto");
        TextField direccion1 = campo(actual == null ? null : actual.getLineaDireccion1(), "Dirección principal");
        TextField direccion2 = campo(actual == null ? null : actual.getLineaDireccion2(), "Opcional");
        codigo.setEditable(!editando);

        GridPane formulario = new GridPane();
        formulario.setHgap(16);
        formulario.setVgap(14);
        formulario.setPadding(new Insets(8, 0, 8, 0));
        formulario.add(campoConTitulo("Código *", codigo), 0, 0);
        formulario.add(campoConTitulo("Ciudad *", ciudad), 1, 0);
        formulario.add(campoConTitulo("País *", pais), 0, 1);
        formulario.add(campoConTitulo("Región", region), 1, 1);
        formulario.add(campoConTitulo("Código postal *", postal), 0, 2);
        formulario.add(campoConTitulo("Teléfono *", telefono), 1, 2);
        formulario.add(campoConTitulo("Dirección principal *", direccion1), 0, 3, 2, 1);
        formulario.add(campoConTitulo("Dirección adicional", direccion2), 0, 4, 2, 1);
        Label aviso = new Label("Los campos con * son obligatorios.");
        aviso.getStyleClass().add("form-hint");
        aviso.setWrapText(true);
        Label introduccion = new Label(editando
                ? "Actualiza la información de esta sede. El código se conserva."
                : "Completa los datos para agregar una sede a tu empresa.");
        introduccion.getStyleClass().add("form-intro");
        introduccion.setWrapText(true);
        VBox cuerpo = new VBox(14, introduccion, formulario, aviso);
        dialogo.getDialogPane().setContent(cuerpo);
        dialogo.getDialogPane().setPrefWidth(570);

        ButtonType guardar = new ButtonType("Guardar oficina", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getDialogPane().getButtonTypes().addAll(guardar, cancelar);
        Node botonGuardar = dialogo.getDialogPane().lookupButton(guardar);
        botonGuardar.getStyleClass().add("primary-button");
        dialogo.getDialogPane().lookupButton(cancelar).getStyleClass().add("secondary-button");
        botonGuardar.addEventFilter(ActionEvent.ACTION, evento -> {
            String problema = validar(codigo, ciudad, pais, region, postal, telefono, direccion1, direccion2);
            if (problema != null) {
                aviso.setText(problema);
                if (!aviso.getStyleClass().contains("form-error")) aviso.getStyleClass().add("form-error");
                evento.consume();
            }
        });
        dialogo.setResultConverter(boton -> boton == guardar ? new oficina(
                texto(codigo), texto(ciudad), texto(pais), opcional(region),
                texto(postal), texto(telefono), texto(direccion1), opcional(direccion2)
        ) : null);
        return ModalFondo.mostrar(ventana, dialogo);
    }

    private static TextField campo(String valor, String sugerencia) {
        TextField campo = new TextField(valor == null ? "" : valor);
        campo.setPromptText(sugerencia);
        campo.setMaxWidth(Double.MAX_VALUE);
        return campo;
    }

    private static VBox campoConTitulo(String titulo, TextField campo) {
        Label etiqueta = new Label(titulo);
        etiqueta.getStyleClass().add("form-label");
        VBox grupo = new VBox(6, etiqueta, campo);
        GridPane.setHgrow(grupo, Priority.ALWAYS);
        return grupo;
    }

    private static String validar(TextField codigo, TextField ciudad, TextField pais,
            TextField region, TextField postal, TextField telefono, TextField direccion1, TextField direccion2) {
        if (texto(codigo).isEmpty() || texto(ciudad).isEmpty() || texto(pais).isEmpty()
                || texto(postal).isEmpty() || texto(telefono).isEmpty() || texto(direccion1).isEmpty()) {
            return "Completa los campos obligatorios marcados con *.";
        }
        if (texto(codigo).length() > 10) return "El código admite hasta 10 caracteres.";
        if (texto(ciudad).length() > 30) return "La ciudad admite hasta 30 caracteres.";
        if (texto(pais).length() > 50 || texto(region).length() > 50) return "País y región admiten hasta 50 caracteres.";
        if (texto(postal).length() > 10) return "El código postal admite hasta 10 caracteres.";
        if (texto(telefono).length() > 20) return "El teléfono admite hasta 20 caracteres.";
        if (texto(direccion1).length() > 50 || texto(direccion2).length() > 50) return "Cada dirección admite hasta 50 caracteres.";
        return null;
    }

    private static String texto(TextField campo) { return campo.getText().trim(); }
    private static String opcional(TextField campo) {
        String valor = texto(campo);
        return valor.isEmpty() ? null : valor;
    }
}
