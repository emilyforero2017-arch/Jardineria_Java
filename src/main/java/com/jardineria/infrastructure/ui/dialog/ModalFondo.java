package com.jardineria.infrastructure.ui.dialog;

import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.stage.Window;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;

/** Oscurece la ventana principal mientras un diálogo está abierto. */
final class ModalFondo {
    private ModalFondo() { }

    static void redondear(Dialog<?> dialogo) {
        dialogo.initStyle(StageStyle.TRANSPARENT);
        dialogo.getDialogPane().sceneProperty().addListener((observable, anterior, escena) -> {
            if (escena != null) escena.setFill(Color.TRANSPARENT);
        });
        if (dialogo.getDialogPane().getScene() != null) {
            dialogo.getDialogPane().getScene().setFill(Color.TRANSPARENT);
        }
    }

    static <T> Optional<T> mostrar(Window ventana, Dialog<T> dialogo) {
        Node raiz = ventana.getScene().getRoot();
        Effect efectoAnterior = raiz.getEffect();
        ColorAdjust sombra = new ColorAdjust();
        sombra.setBrightness(-0.38);
        sombra.setSaturation(-0.12);
        sombra.setInput(efectoAnterior);
        raiz.setEffect(sombra);
        try {
            return dialogo.showAndWait();
        } finally {
            raiz.setEffect(efectoAnterior);
        }
    }
}
