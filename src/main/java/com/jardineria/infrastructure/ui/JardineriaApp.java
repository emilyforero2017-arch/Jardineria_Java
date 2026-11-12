package com.jardineria.infrastructure.ui;

import com.jardineria.infrastructure.database.fabricaConexionMysql;
import com.jardineria.infrastructure.persistence.fabricaRepositorios;
import com.jardineria.infrastructure.persistence.fabricarRepositoriosMysql;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JardineriaApp extends Application {
    private MediaPlayer sonidoEntrada;

    @Override
    public void start(Stage ventana) {
        prepararSonido();
        fabricaRepositorios repositorios =
                new fabricarRepositoriosMysql(new fabricaConexionMysql());
        PrincipalView principal = new PrincipalView(repositorios);

        Node logo = PrincipalView.crearLogoGrande();
        logo.setOpacity(0);
        logo.setTranslateY(12);
        HBox marca = new HBox(1);
        marca.setAlignment(Pos.CENTER);
        ParallelTransition entrada = new ParallelTransition();
        // El audio comienza un instante antes de que aparezca el logo.
        entrada.setDelay(Duration.millis(250));
        entrada.getChildren().add(entradaSuave(logo, 0, 900));
        String nombre = "JARDINERÍA";
        for (int i = 0; i < nombre.length(); i++) {
            Label letra = new Label(String.valueOf(nombre.charAt(i)));
            letra.getStyleClass().add("splash-title");
            letra.setOpacity(0);
            letra.setTranslateY(12);
            marca.getChildren().add(letra);
            entrada.getChildren().add(entradaSuave(letra, 350 + i * 85, 450));
        }
        Label detalle = new Label("CULTIVA CADA IDEA");
        detalle.getStyleClass().add("splash-subtitle");
        detalle.setOpacity(0);
        detalle.setTranslateY(12);
        entrada.getChildren().add(entradaSuave(detalle, 1450, 700));
        VBox presentacion = new VBox(15, logo, marca, detalle);
        presentacion.setAlignment(Pos.CENTER);

        StackPane portada = new StackPane(presentacion);
        portada.getStyleClass().add("splash");
        Scene escena = new Scene(portada, 1240, 760);
        escena.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/styles/app.css"),
                "No se encontró el archivo de estilos"
        ).toExternalForm());

        ventana.setTitle("Jardinería | Gestión");
        ventana.setMinWidth(930);
        ventana.setMinHeight(620);
        ventana.setScene(escena);
        reproducirSonido();
        ventana.show();

        FadeTransition desvanecer = new FadeTransition(Duration.millis(650), portada);
        desvanecer.setFromValue(1);
        desvanecer.setToValue(0);
        ParallelTransition salida = new ParallelTransition(desvanecer);
        if (sonidoEntrada != null) {
            salida.getChildren().add(new Timeline(new KeyFrame(Duration.millis(650),
                    new KeyValue(sonidoEntrada.volumeProperty(), 0))));
        }
        SequentialTransition animacion = new SequentialTransition(
                entrada, new PauseTransition(Duration.millis(1450)), salida
        );
        animacion.setOnFinished(evento -> {
            liberarSonido();
            var vista = principal.crearVista();
            vista.setOpacity(0);
            escena.setRoot(vista);
            FadeTransition mostrarInicio = new FadeTransition(Duration.millis(600), vista);
            mostrarInicio.setFromValue(0);
            mostrarInicio.setToValue(1);
            mostrarInicio.play();
        });
        animacion.play();
    }

    /** El sonido es opcional: la aplicación continúa si no puede reproducirse. */
    private void prepararSonido() {
        try {
            var recurso = getClass().getResource("/audio/intro-jardineria.mp3");
            if (recurso == null) return;
            sonidoEntrada = new MediaPlayer(new Media(recurso.toExternalForm()));
            sonidoEntrada.setVolume(0.25);
            sonidoEntrada.setOnError(this::liberarSonido);
        } catch (RuntimeException error) {
            liberarSonido();
        }
    }

    private void reproducirSonido() {
        try {
            if (sonidoEntrada != null) sonidoEntrada.play();
        } catch (RuntimeException error) {
            liberarSonido();
        }
    }

    private void liberarSonido() {
        if (sonidoEntrada == null) return;
        sonidoEntrada.dispose();
        sonidoEntrada = null;
    }

    @Override
    public void stop() {
        liberarSonido();
    }

    private ParallelTransition entradaSuave(Node nodo, int demora, int duracion) {
        FadeTransition aparecer = new FadeTransition(Duration.millis(duracion), nodo);
        aparecer.setFromValue(0);
        aparecer.setToValue(1);
        TranslateTransition subir = new TranslateTransition(Duration.millis(duracion), nodo);
        subir.setFromY(12);
        subir.setToY(0);
        ParallelTransition transicion = new ParallelTransition(aparecer, subir);
        transicion.setDelay(Duration.millis(demora));
        return transicion;
    }
}
