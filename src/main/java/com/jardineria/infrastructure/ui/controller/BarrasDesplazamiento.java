package com.jardineria.infrastructure.ui.controller;

import javafx.animation.PauseTransition;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/** Muestra temporalmente los indicadores cuando el usuario desplaza una tabla. */
final class BarrasDesplazamiento {
    private static final PseudoClass VERTICAL = PseudoClass.getPseudoClass("desplazando-vertical");
    private static final PseudoClass HORIZONTAL = PseudoClass.getPseudoClass("desplazando-horizontal");
    private final TableView<?> tabla;
    private final Node zonaHorizontal;
    private final PauseTransition ocultarVertical = new PauseTransition(Duration.seconds(1.5));
    private final PauseTransition ocultarHorizontal = new PauseTransition(Duration.seconds(1.5));

    private BarrasDesplazamiento(TableView<?> tabla, Node zonaHorizontal) {
        this.tabla = tabla;
        this.zonaHorizontal = zonaHorizontal;
        ocultarVertical.setOnFinished(e -> tabla.pseudoClassStateChanged(VERTICAL, false));
        ocultarHorizontal.setOnFinished(e -> activarHorizontal(false));
        tabla.addEventFilter(ScrollEvent.SCROLL, this::alDesplazar);
        tabla.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> alPresionar(e.getTarget()));
        tabla.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> alSoltar(e.getTarget()));
        if (zonaHorizontal != null) {
            zonaHorizontal.addEventFilter(ScrollEvent.SCROLL, this::alDesplazar);
            zonaHorizontal.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                mostrarHorizontal(); ocultarHorizontal.stop();
            });
            zonaHorizontal.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> mostrarHorizontal());
        }
    }

    static void instalar(TableView<?> tabla, Node zonaHorizontal) {
        new BarrasDesplazamiento(tabla, zonaHorizontal);
    }

    private void alDesplazar(ScrollEvent evento) {
        if (evento.getDeltaX() != 0 || (evento.isShiftDown() && evento.getDeltaY() != 0)) mostrarHorizontal();
        if (evento.getDeltaY() != 0 && !evento.isShiftDown()) mostrarVertical();
    }

    private void alPresionar(Object objetivo) {
        ScrollBar barra = buscarBarra(objetivo);
        if (barra == null) return;
        if (barra.getOrientation() == Orientation.VERTICAL) {
            mostrarVertical(); ocultarVertical.stop();
        } else {
            mostrarHorizontal(); ocultarHorizontal.stop();
        }
    }

    private void alSoltar(Object objetivo) {
        ScrollBar barra = buscarBarra(objetivo);
        if (barra == null) return;
        if (barra.getOrientation() == Orientation.VERTICAL) mostrarVertical();
        else mostrarHorizontal();
    }

    private static ScrollBar buscarBarra(Object objetivo) {
        Node nodo = objetivo instanceof Node ? (Node) objetivo : null;
        while (nodo != null) {
            if (nodo instanceof ScrollBar) return (ScrollBar) nodo;
            Parent padre = nodo.getParent();
            nodo = padre;
        }
        return null;
    }

    private void mostrarVertical() {
        tabla.pseudoClassStateChanged(VERTICAL, true);
        ocultarVertical.playFromStart();
    }

    private void mostrarHorizontal() {
        activarHorizontal(true);
        ocultarHorizontal.playFromStart();
    }

    private void activarHorizontal(boolean activo) {
        tabla.pseudoClassStateChanged(HORIZONTAL, activo);
        if (zonaHorizontal != null) zonaHorizontal.pseudoClassStateChanged(HORIZONTAL, activo);
    }
}
