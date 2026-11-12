package com.jardineria.infrastructure.ui.controller;

import com.jardineria.application.usecase.oficinaUseCase;
import com.jardineria.domain.entity.oficina;
import com.jardineria.infrastructure.ui.dialog.AvisoDialogo;
import com.jardineria.infrastructure.ui.dialog.OficinaDialog;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Conecta los controles de la pantalla con los casos de uso de oficinas. */
public class OficinasController {
    private final oficinaUseCase casoDeUso;
    private final ObservableList<oficina> oficinas = FXCollections.observableArrayList();
    private final FilteredList<oficina> filtradas = new FilteredList<>(oficinas, oficina -> true);
    private final TableView<oficina> tabla = new TableView<>();
    private final Label estado = new Label();
    private final Label contador = new Label();
    private final Button actualizar = new Button("Recargar lista");
    private final Button nueva = new Button("+  Nueva oficina");
    private final Button editar = new Button("Editar oficina");
    private final Button eliminar = new Button("Eliminar oficina");
    private final PauseTransition mensajeRecarga = new PauseTransition(Duration.seconds(2.5));
    private boolean ocupado;

    public OficinasController(oficinaUseCase casoDeUso) {
        this.casoDeUso = Objects.requireNonNull(casoDeUso);
    }

    public Parent crearVista() {
        Label ceja = new Label("ADMINISTRACIÓN  /  OFICINAS");
        ceja.getStyleClass().add("eyebrow");
        Label titulo = new Label("Gestión de oficinas");
        titulo.getStyleClass().add("page-title");
        Label descripcion = new Label("Consulta y organiza las sedes de tu empresa.");
        descripcion.getStyleClass().add("page-subtitle");
        VBox encabezado = new VBox(8, ceja, titulo, descripcion);

        TextField busqueda = new TextField();
        busqueda.setPromptText("Buscar por código, ciudad, país o código postal…");
        busqueda.getStyleClass().add("search-field");
        busqueda.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(busqueda, Priority.ALWAYS);
        busqueda.textProperty().addListener((observable, anterior, valor) -> {
            String filtro = valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
            filtradas.setPredicate(item -> filtro.isEmpty()
                    || contiene(item.getCodigoOficina(), filtro)
                    || contiene(item.getCiudad(), filtro)
                    || contiene(item.getPais(), filtro)
                    || contiene(item.getCodigoPostal(), filtro));
            actualizarContador();
        });
        actualizar.getStyleClass().add("secondary-button");
        nueva.getStyleClass().add("primary-button");
        editar.getStyleClass().add("secondary-button");
        eliminar.getStyleClass().add("danger-button");
        actualizar.setTooltip(new Tooltip("Vuelve a consultar las oficinas en la base de datos"));
        actualizar.setOnAction(evento -> cargarOficinas(true));
        mensajeRecarga.setOnFinished(evento -> actualizar.setText("Recargar lista"));
        nueva.setOnAction(evento -> registrar());
        editar.setOnAction(evento -> editar());
        eliminar.setOnAction(evento -> eliminar());
        HBox acciones = new HBox(10, busqueda, actualizar, nueva);
        acciones.setAlignment(Pos.CENTER_LEFT);
        acciones.getStyleClass().add("office-toolbar");

        tabla.getColumns().addAll(List.of(
                columna("CÓDIGO", oficina::getCodigoOficina, 105),
                columna("CIUDAD", oficina::getCiudad, 130),
                columna("PAÍS", oficina::getPais, 110),
                columna("REGIÓN", oficina::getRegion, 110),
                columna("C. POSTAL", oficina::getCodigoPostal, 100),
                columna("TELÉFONO", oficina::getTelefono, 135),
                columna("DIRECCIÓN", oficina::getLineaDireccion1, 200)
        ));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(new Label("No hay oficinas para mostrar."));
        SortedList<oficina> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(ordenadas);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antes, despues) -> actualizarBotones());
        VBox.setVgrow(tabla, Priority.ALWAYS);

        Label listaTitulo = new Label("Listado de oficinas");
        listaTitulo.getStyleClass().add("table-title");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        contador.getStyleClass().add("table-count");
        Label ayudaLista = new Label("Selecciona una oficina para editarla o eliminarla");
        ayudaLista.getStyleClass().add("table-hint");
        VBox encabezadoLista = new VBox(5, listaTitulo, ayudaLista);
        HBox tablaCabecera = new HBox(12, encabezadoLista, espacio, contador);
        tablaCabecera.setAlignment(Pos.CENTER_LEFT);
        tablaCabecera.getStyleClass().add("table-header");
        estado.getStyleClass().add("status-text");
        Region separador = new Region();
        HBox.setHgrow(separador, Priority.ALWAYS);
        HBox pie = new HBox(10, estado, separador, editar, eliminar);
        pie.setAlignment(Pos.CENTER_LEFT);
        pie.getStyleClass().add("table-footer");
        BarrasDesplazamiento.instalar(tabla, null);
        VBox tarjeta = new VBox(tablaCabecera, tabla, pie);
        tarjeta.getStyleClass().add("table-card");
        VBox.setVgrow(tarjeta, Priority.ALWAYS);
        VBox vista = new VBox(20, encabezado, acciones, tarjeta);
        vista.getStyleClass().add("page");
        actualizarBotones();
        actualizarContador();
        cargarOficinas();
        return vista;
    }

    private TableColumn<oficina, String> columna(String titulo, Function<oficina, String> valor, double ancho) {
        TableColumn<oficina, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(dato -> new ReadOnlyStringWrapper(valor.apply(dato.getValue())));
        columna.setPrefWidth(ancho);
        columna.setMinWidth(75);
        return columna;
    }

    private static boolean contiene(String valor, String filtro) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(filtro);
    }

    private void cargarOficinas() {
        cargarOficinas(false);
    }

    private void cargarOficinas(boolean recargaManual) {
        mensajeRecarga.stop();
        actualizar.setText("Recargar lista");
        ejecutar("Cargando oficinas…", casoDeUso::listar, resultado -> {
            oficinas.setAll(resultado);
            actualizarContador();
            estado.setText("Lista consultada a las " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            if (recargaManual) {
                actualizar.setText("Lista actualizada ✓");
                mensajeRecarga.playFromStart();
            }
        });
    }

    private void registrar() {
        OficinaDialog.mostrar(tabla.getScene().getWindow(), null).ifPresent(nuevaOficina ->
                ejecutar("Guardando oficina…", () -> {
                    casoDeUso.registrar(nuevaOficina);
                    return true;
                }, resultado -> {
                    aviso(Alert.AlertType.INFORMATION, "Oficina registrada", "La oficina se guardó correctamente.");
                    cargarOficinas();
                }));
    }

    private void editar() {
        oficina seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;
        OficinaDialog.mostrar(tabla.getScene().getWindow(), seleccionada).ifPresent(cambios ->
                ejecutar("Actualizando oficina…", () -> casoDeUso.actualizar(cambios), resultado -> {
                    if (resultado) aviso(Alert.AlertType.INFORMATION, "Oficina actualizada", "Los cambios se guardaron correctamente.");
                    else aviso(Alert.AlertType.WARNING, "Oficina no encontrada", "El registro ya no existe. Se actualizará la lista.");
                    cargarOficinas();
                }));
    }

    private void eliminar() {
        oficina seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) return;
        if (!AvisoDialogo.confirmarEliminacion(tabla.getScene().getWindow(),
                seleccionada.getCodigoOficina(), seleccionada.getCiudad())) return;
        ejecutar("Eliminando oficina…", () -> casoDeUso.eliminar(seleccionada.getCodigoOficina()), resultado -> {
            if (resultado) aviso(Alert.AlertType.INFORMATION, "Oficina eliminada", "La oficina se eliminó correctamente.");
            else aviso(Alert.AlertType.WARNING, "Oficina no encontrada", "El registro ya no existe. Se actualizará la lista.");
            cargarOficinas();
        });
    }

    private <T> void ejecutar(String mensaje, Callable<T> operacion, Consumer<T> alTerminar) {
        ocupado = true;
        actualizarBotones();
        estado.setText(mensaje);
        Task<T> tarea = new Task<>() {
            @Override protected T call() throws Exception { return operacion.call(); }
        };
        tarea.setOnSucceeded(evento -> {
            ocupado = false;
            actualizarBotones();
            alTerminar.accept(tarea.getValue());
        });
        tarea.setOnFailed(evento -> {
            ocupado = false;
            actualizarBotones();
            estado.setText("No se pudo completar la operación");
            aviso(Alert.AlertType.ERROR, "No se pudo completar", mensajeError(tarea.getException()));
        });
        Thread hilo = new Thread(tarea, "operacion-oficinas");
        hilo.setDaemon(true);
        hilo.start();
    }

    private String mensajeError(Throwable error) {
        Throwable causa = error;
        while (causa != null) {
            if (causa instanceof SQLException sql) {
                if (sql.getErrorCode() == 1451) return "No se puede eliminar la oficina porque tiene empleados asignados.";
                if (sql.getErrorCode() == 1062) return "Ya existe una oficina con ese código. Elige otro.";
            }
            causa = causa.getCause();
        }
        return "Revisa la información ingresada y la conexión con la base de datos.";
    }

    private void actualizarBotones() {
        boolean sinSeleccion = tabla.getSelectionModel().getSelectedItem() == null;
        actualizar.setDisable(ocupado);
        nueva.setDisable(ocupado);
        editar.setDisable(ocupado || sinSeleccion);
        eliminar.setDisable(ocupado || sinSeleccion);
    }

    private void actualizarContador() {
        contador.setText(filtradas.size() + " de " + oficinas.size() + " oficinas");
    }

    private void aviso(Alert.AlertType tipo, String titulo, String mensaje) {
        AvisoDialogo.mostrar(tabla.getScene().getWindow(), tipo, titulo, mensaje);
    }
}
