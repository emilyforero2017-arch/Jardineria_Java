package com.jardineria.infrastructure.ui.controller;

import com.jardineria.application.usecase.clienteUseCase;
import com.jardineria.application.usecase.pedidoUseCase;
import com.jardineria.application.usecase.detallepedidoUseCase;
import com.jardineria.domain.entity.pedido;
import com.jardineria.domain.entity.detallepedido;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import com.jardineria.application.usecase.gamaproductoUseCase;
import com.jardineria.application.usecase.productoUseCase;
import com.jardineria.domain.entity.gamaproducto;
import com.jardineria.domain.entity.producto;
import com.jardineria.application.usecase.empleadoUseCase;
import com.jardineria.domain.entity.cliente;
import com.jardineria.domain.entity.empleado;
import com.jardineria.infrastructure.persistence.fabricaRepositorios;
import com.jardineria.infrastructure.ui.dialog.AvisoDialogo;
import com.jardineria.infrastructure.ui.dialog.RegistroDialog;
import com.jardineria.infrastructure.ui.dialog.RegistroDialog.Campo;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/** Pantallas de administración de empleados y clientes. */
public final class PersonasController<T> {
    private record Columna<T>(String titulo, Function<T, String> valor, double ancho) { }
    private final String nombre, plural;
    private final Supplier<List<T>> listar;
    private final java.util.function.Consumer<T> guardar;
    private final Function<T, Boolean> actualizar;
    private final Function<T, Boolean> eliminar;
    private final Function<T, String> codigo;
    private final Function<T, String> descripcion;
    private final Function<T, List<Campo>> campos;
    private final Function<List<String>, T> construir;
    private final Function<List<String>, String> validar;
    private final List<Columna<T>> columnas;
    private final ObservableList<T> datos = FXCollections.observableArrayList();
    private final FilteredList<T> filtrados = new FilteredList<>(datos, x -> true);
    private final TableView<T> tabla = new TableView<>();
    private final Label estado = new Label(), contador = new Label();
    private final Button recargar = new Button("Recargar lista"), nuevo = new Button(), editar = new Button(), borrar = new Button();
    private boolean ocupado;
    private Supplier<List<producto>> productosPorGama;
    private long consultaGama;

    private PersonasController(String nombre, String plural, Supplier<List<T>> listar,
            java.util.function.Consumer<T> guardar, Function<T, Boolean> actualizar, Function<T, Boolean> eliminar,
            Function<T, String> codigo, Function<T, String> descripcion, Function<T, List<Campo>> campos,
            Function<List<String>, T> construir, Function<List<String>, String> validar, List<Columna<T>> columnas) {
        this.nombre = nombre; this.plural = plural; this.listar = listar; this.guardar = guardar;
        this.actualizar = actualizar; this.eliminar = eliminar; this.codigo = codigo; this.descripcion = descripcion;
        this.campos = campos; this.construir = construir; this.validar = validar; this.columnas = columnas;
    }

    public static PersonasController<empleado> empleados(fabricaRepositorios repositorios) {
        empleadoUseCase uso = new empleadoUseCase(repositorios.crearEmpleadoRepository());
        return new PersonasController<>("empleado", "empleados", uso::listar, uso::registrar, uso::actualizar,
            e -> uso.eliminar(e.getCodigoEmpleado()), e -> String.valueOf(e.getCodigoEmpleado()), e -> e.getNombre() + " " + e.getApellido1(),
            e -> List.of(campo("Código", e == null ? null : e.getCodigoEmpleado(), true, 0, "Número entero"),
                campo("Nombre", e == null ? null : e.getNombre(), true, 50, "Nombre"),
                campo("Primer apellido", e == null ? null : e.getApellido1(), true, 50, "Primer apellido"),
                campo("Segundo apellido", e == null ? null : e.getApellido2(), false, 50, "Opcional"),
                campo("Extensión", e == null ? null : e.getExtension(), true, 10, "Extensión telefónica"),
                campo("Correo electrónico", e == null ? null : e.getEmail(), true, 100, "Correo"),
                campo("Código de oficina", e == null ? null : e.getCodigoOficina(), true, 10, "Código de una oficina existente"),
                campo("Código del jefe", e == null ? null : e.getCodigoJefe(), false, 0, "Opcional: código de empleado"),
                campo("Puesto", e == null ? null : e.getPuesto(), false, 50, "Opcional")),
            v -> new empleado(entero(v.get(0)), v.get(1), v.get(2), opcional(v.get(3)), v.get(4), v.get(5), v.get(6), enteroOpcional(v.get(7)), opcional(v.get(8))),
            v -> { String error = validarEnteros(v, 0, 7); if (error != null) return error;
                if (!v.get(7).isEmpty() && v.get(0).equals(v.get(7))) return "El empleado no puede ser su propio jefe."; return null; },
            List.of(new Columna<>("CÓDIGO", e -> String.valueOf(e.getCodigoEmpleado()), 80),
                new Columna<>("NOMBRE", e -> e.getNombre() + " " + e.getApellido1(), 150),
                new Columna<>("CORREO", empleado::getEmail, 190), new Columna<>("EXTENSIÓN", empleado::getExtension, 85),
                new Columna<>("OFICINA", empleado::getCodigoOficina, 90), new Columna<>("JEFE", e -> cadena(e.getCodigoJefe()), 75),
                new Columna<>("PUESTO", empleado::getPuesto, 130)));
    }

    public static PersonasController<cliente> clientes(fabricaRepositorios repositorios) {
        clienteUseCase uso = new clienteUseCase(repositorios.crearClienteRepository());
        return new PersonasController<>("cliente", "clientes", uso::listar, uso::registrar, uso::actualizar,
            c -> uso.eliminar(c.getCodigoCliente()), c -> String.valueOf(c.getCodigoCliente()), cliente::getNombreCliente,
            c -> List.of(campo("Código", c == null ? null : c.getCodigoCliente(), true, 0, "Número entero"),
                campo("Nombre del cliente", c == null ? null : c.getNombreCliente(), true, 50, "Nombre o empresa"),
                campo("Nombre de contacto", c == null ? null : c.getNombreContacto(), false, 30, "Opcional"),
                campo("Apellido de contacto", c == null ? null : c.getApellidoContacto(), false, 30, "Opcional"),
                campo("Teléfono", c == null ? null : c.getTelefono(), true, 15, "Teléfono"),
                campo("Fax", c == null ? null : c.getFax(), true, 15, "Dato obligatorio en la base"),
                campo("Dirección principal", c == null ? null : c.getLineaDireccion1(), true, 50, "Dirección"),
                campo("Dirección adicional", c == null ? null : c.getLineaDireccion2(), false, 50, "Opcional"),
                campo("Ciudad", c == null ? null : c.getCiudad(), true, 50, "Ciudad"),
                campo("Región", c == null ? null : c.getRegion(), false, 50, "Opcional"),
                campo("País", c == null ? null : c.getPais(), false, 50, "Opcional"),
                campo("Código postal", c == null ? null : c.getCodigoPostal(), false, 10, "Opcional"),
                campo("Código representante", c == null ? null : c.getCodigoEmpleadoRepVentas(), false, 0, "Opcional: código de empleado"),
                campo("Límite de crédito", c == null ? null : c.getLimiteCredito(), false, 0, "Opcional: ej. 2500.00")),
            v -> new cliente(entero(v.get(0)), v.get(1), opcional(v.get(2)), opcional(v.get(3)), v.get(4), v.get(5), v.get(6), opcional(v.get(7)), v.get(8), opcional(v.get(9)), opcional(v.get(10)), opcional(v.get(11)), enteroOpcional(v.get(12)), v.get(13).isEmpty() ? null : new BigDecimal(v.get(13))),
            v -> { String error = validarEnteros(v, 0, 12); if (error != null) return error;
                if (!v.get(13).isEmpty()) try { BigDecimal n = new BigDecimal(v.get(13));
                    if (n.signum() < 0 || n.scale() > 2 || n.precision() - n.scale() > 13) return "El límite de crédito debe ser positivo y tener hasta 13 enteros y 2 decimales.";
                } catch (NumberFormatException ex) { return "Ingresa un límite válido, por ejemplo 2500.00."; } return null; },
            List.of(new Columna<>("CÓDIGO", c -> String.valueOf(c.getCodigoCliente()), 80),
                new Columna<>("CLIENTE", cliente::getNombreCliente, 180), new Columna<>("TELÉFONO", cliente::getTelefono, 130),
                new Columna<>("CIUDAD", cliente::getCiudad, 120), new Columna<>("PAÍS", cliente::getPais, 100),
                new Columna<>("REPRESENTANTE", c -> cadena(c.getCodigoEmpleadoRepVentas()), 110),
                new Columna<>("CRÉDITO", c -> cadena(c.getLimiteCredito()), 100)));
    }

    public static PersonasController<gamaproducto> gamas(fabricaRepositorios repositorios) {
        gamaproductoUseCase uso = new gamaproductoUseCase(repositorios.crearGamaproductoRepository());
        PersonasController<gamaproducto> controlador = new PersonasController<>("gama de productos", "gamas de productos", uso::listar, uso::registrar,
            uso::actualizar, g -> uso.eliminar(g.getGama()), gamaproducto::getGama, gamaproducto::getGama,
            g -> List.of(campo("Nombre de la gama", g == null ? null : g.getGama(), true, 50, "Ej. Ornamentales"),
                textoLargo("Descripción", g == null ? null : g.getDescripcionTexto(), "Opcional"),
                textoLargo("Descripción HTML", g == null ? null : g.getDescripcionHtml(), "Opcional"),
                new Campo("Imagen", g == null ? null : g.getImagen(), false, 256,
                    "Selecciona una imagen", false, true)),
            v -> new gamaproducto(v.get(0), opcional(v.get(1)), opcional(v.get(2)), opcional(v.get(3))),
            v -> null,
            List.of(new Columna<>("GAMA", gamaproducto::getGama, 160),
                new Columna<>("DESCRIPCIÓN", gamaproducto::getDescripcionTexto, 500)));
        controlador.productosPorGama = () -> new productoUseCase(repositorios.crearProductoRepository()).listar();
        return controlador;
    }

    public static PersonasController<producto> productos(fabricaRepositorios repositorios) {
        productoUseCase uso = new productoUseCase(repositorios.crearProductoRepository());
        return new PersonasController<>("producto", "productos", uso::listar, uso::registrar, uso::actualizar,
            p -> uso.eliminar(p.getCodigoProducto()), producto::getCodigoProducto, producto::getNombre,
            p -> List.of(campo("Código", p == null ? null : p.getCodigoProducto(), true, 15, "Código del producto"),
                campo("Nombre", p == null ? null : p.getNombre(), true, 70, "Nombre del producto"),
                campo("Gama", p == null ? null : p.getGama(), true, 50, "Nombre de una gama existente"),
                campo("Dimensiones", p == null ? null : p.getDimensiones(), false, 25, "Opcional"),
                campo("Proveedor", p == null ? null : p.getProveedor(), false, 50, "Opcional"),
                textoLargo("Descripción", p == null ? null : p.getDescripcion(), "Opcional"),
                campo("Cantidad en stock", p == null ? null : p.getCantidadEnStock(), true, 0, "Número entero desde 0"),
                campo("Precio de venta", p == null ? null : p.getPrecioVenta(), true, 0, "Ej. 2500.00"),
                campo("Precio del proveedor", p == null ? null : p.getPrecioProveedor(), false, 0, "Opcional: ej. 1800.00")),
            v -> new producto(v.get(0), v.get(1), v.get(2), opcional(v.get(3)), opcional(v.get(4)),
                    opcional(v.get(5)), Short.parseShort(v.get(6)), new BigDecimal(v.get(7)),
                    v.get(8).isEmpty() ? null : new BigDecimal(v.get(8))),
            v -> {
                try { int stock = Integer.parseInt(v.get(6)); if (stock < 0 || stock > Short.MAX_VALUE) throw new NumberFormatException(); }
                catch (NumberFormatException error) { return "La cantidad en stock debe ser un entero entre 0 y 32767."; }
                if (!dineroValido(v.get(7)) || (!v.get(8).isEmpty() && !dineroValido(v.get(8))))
                    return "Ingresa precios no negativos con hasta 13 enteros y 2 decimales, por ejemplo 2500.00.";
                return null;
            },
            List.of(new Columna<>("CÓDIGO", producto::getCodigoProducto, 105),
                new Columna<>("PRODUCTO", producto::getNombre, 170),
                new Columna<>("GAMA", producto::getGama, 130),
                new Columna<>("PROVEEDOR", producto::getProveedor, 135),
                new Columna<>("STOCK", p -> String.valueOf(p.getCantidadEnStock()), 85),
                new Columna<>("PRECIO VENTA", p -> cadena(p.getPrecioVenta()), 115),
                new Columna<>("PRECIO PROVEEDOR", p -> cadena(p.getPrecioProveedor()), 130)));
    }

    public static PersonasController<pedido> pedidos(fabricaRepositorios repositorios) {
        pedidoUseCase uso = new pedidoUseCase(repositorios.crearPedidoRepository());
        return new PersonasController<>("pedido", "pedidos", uso::listar, uso::registrar, uso::actualizar,
            p -> uso.eliminar(p.getCodigoPedido()), p -> String.valueOf(p.getCodigoPedido()),
            p -> "Cliente " + p.getCodigoCliente(),
            p -> List.of(campo("Código del pedido", p == null ? null : p.getCodigoPedido(), true, 0, "Número entero positivo"),
                campo("Código del cliente", p == null ? null : p.getCodigoCliente(), true, 0, "Código de un cliente existente"),
                campo("Estado", p == null ? null : p.getEstado(), true, 15, "Ej. Pendiente"),
                campo("Fecha del pedido", p == null ? null : p.getFechaPedido(), true, 10, "AAAA-MM-DD"),
                campo("Fecha esperada", p == null ? null : p.getFechaEsperada(), true, 10, "AAAA-MM-DD"),
                campo("Fecha de entrega", p == null ? null : p.getFechaEntrega(), false, 10, "Opcional: AAAA-MM-DD"),
                textoLargo("Comentarios", p == null ? null : p.getComentarios(), "Opcional")),
            v -> new pedido(entero(v.get(0)), LocalDate.parse(v.get(3)), LocalDate.parse(v.get(4)),
                v.get(5).isEmpty() ? null : LocalDate.parse(v.get(5)), v.get(2), opcional(v.get(6)), entero(v.get(1))),
            v -> { String error = validarEnteros(v, 0, 1); if (error != null) return error;
                try {
                    LocalDate creacion = LocalDate.parse(v.get(3));
                    LocalDate esperada = LocalDate.parse(v.get(4));
                    LocalDate entrega = v.get(5).isEmpty() ? null : LocalDate.parse(v.get(5));
                    if (esperada.isBefore(creacion)) return "La fecha esperada no puede ser anterior a la fecha del pedido.";
                    if (entrega != null && entrega.isBefore(creacion)) return "La fecha de entrega no puede ser anterior a la fecha del pedido.";
                } catch (DateTimeParseException ex) { return "Ingresa fechas válidas con formato AAAA-MM-DD, por ejemplo 2026-11-11."; }
                return null;
            },
            List.of(new Columna<>("PEDIDO", p -> String.valueOf(p.getCodigoPedido()), 80),
                new Columna<>("CLIENTE", p -> String.valueOf(p.getCodigoCliente()), 80),
                new Columna<>("FECHA PEDIDO", p -> cadena(p.getFechaPedido()), 110),
                new Columna<>("FECHA ESPERADA", p -> cadena(p.getFechaEsperada()), 120),
                new Columna<>("FECHA ENTREGA", p -> cadena(p.getFechaEntrega()), 120),
                new Columna<>("ESTADO", pedido::getEstado, 100),
                new Columna<>("COMENTARIOS", pedido::getComentarios, 180)));
    }

    public static PersonasController<detallepedido> detallesPedido(fabricaRepositorios repositorios) {
        detallepedidoUseCase uso = new detallepedidoUseCase(repositorios.crearDetallepedidoRepository());
        return new PersonasController<>("detalle de pedido", "detalles de pedidos", uso::listar, uso::registrar, uso::actualizar,
            d -> uso.eliminar(d.getCodigoPedido(), d.getCodigoProducto()),
            d -> String.valueOf(d.getCodigoPedido()), d -> "Producto " + d.getCodigoProducto(),
            d -> List.of(campo("Código del pedido", d == null ? null : d.getCodigoPedido(), true, 0, "Código de un pedido existente"),
                campo("Código del producto", d == null ? null : d.getCodigoProducto(), true, 15, "Código de un producto existente"),
                campo("Cantidad", d == null ? null : d.getCantidad(), true, 0, "Número entero positivo"),
                campo("Precio por unidad", d == null ? null : d.getPrecioUnidad(), true, 0, "Ej. 2500.00"),
                campo("Número de línea", d == null ? null : d.getNumeroLinea(), true, 0, "Número entero positivo")),
            v -> new detallepedido(entero(v.get(0)), v.get(1), entero(v.get(2)), new BigDecimal(v.get(3)), Short.parseShort(v.get(4))),
            v -> { String error = validarEnteros(v, 0, 2, 4); if (error != null) return error;
                try { if (Integer.parseInt(v.get(4)) > Short.MAX_VALUE) return "El número de línea debe estar entre 1 y 32767."; }
                catch (NumberFormatException ex) { return "El número de línea debe estar entre 1 y 32767."; }
                if (!dineroValido(v.get(3))) return "Ingresa un precio no negativo con hasta 13 enteros y 2 decimales.";
                return null;
            },
            List.of(new Columna<>("PEDIDO", d -> String.valueOf(d.getCodigoPedido()), 100),
                new Columna<>("PRODUCTO", detallepedido::getCodigoProducto, 160),
                new Columna<>("CANTIDAD", d -> String.valueOf(d.getCantidad()), 100),
                new Columna<>("PRECIO UNITARIO", d -> cadena(d.getPrecioUnidad()), 140),
                new Columna<>("LÍNEA", d -> String.valueOf(d.getNumeroLinea()), 90)));
    }

    private static Campo textoLargo(String nombre, String valor, String ayuda) {
        return new Campo(nombre, valor, false, 0, ayuda, true, false);
    }

    private static boolean dineroValido(String valor) {
        return valor.matches("[0-9]{1,13}(\\.[0-9]{1,2})?");
    }

    public Parent crearVista() {
        Label ceja = new Label("ADMINISTRACIÓN  /  " + plural.toUpperCase(Locale.ROOT)); ceja.getStyleClass().add("eyebrow");
        Label titulo = new Label("Gestión de " + plural); titulo.getStyleClass().add("page-title");
        Label subtitulo = new Label("Consulta y organiza la información de tus " + plural + "."); subtitulo.getStyleClass().add("page-subtitle");
        TextField buscar = new TextField(); buscar.setPromptText("Buscar por código, nombre o datos visibles…");
        buscar.getStyleClass().add("search-field"); HBox.setHgrow(buscar, Priority.ALWAYS); buscar.setMaxWidth(Double.MAX_VALUE);
        buscar.textProperty().addListener((obs, previo, valor) -> {
            String texto = valor.trim().toLowerCase(Locale.ROOT);
            filtrados.setPredicate(item -> texto.isEmpty() || columnas.stream().anyMatch(c -> cadena(c.valor().apply(item)).toLowerCase(Locale.ROOT).contains(texto)));
            actualizarContador();
        });
        nuevo.setText("+  " + (nombre.startsWith("gama") ? "Nueva " : "Nuevo ") + nombre); editar.setText("Actualizar " + nombre); borrar.setText("Eliminar " + nombre);
        recargar.getStyleClass().add("secondary-button"); nuevo.getStyleClass().add("primary-button");
        editar.getStyleClass().add("secondary-button"); borrar.getStyleClass().add("danger-button");
        recargar.setOnAction(e -> cargar()); nuevo.setOnAction(e -> formulario(null));
        editar.setOnAction(e -> { T item = tabla.getSelectionModel().getSelectedItem(); if (item != null) formulario(item); });
        borrar.setOnAction(e -> eliminarSeleccion());
        HBox acciones = new HBox(10, buscar, recargar, nuevo); acciones.setAlignment(Pos.CENTER_LEFT); acciones.getStyleClass().add("office-toolbar");
        for (Columna<T> dato : columnas) {
            TableColumn<T, String> columna = new TableColumn<>(dato.titulo()); columna.setPrefWidth(dato.ancho()); columna.setMinWidth(75);
            columna.setCellValueFactory(c -> new ReadOnlyStringWrapper(cadena(dato.valor().apply(c.getValue()))));
            if (("pedido".equals(nombre) && "COMENTARIOS".equals(dato.titulo()))
                    || ("gama de productos".equals(nombre) && "DESCRIPCIÓN".equals(dato.titulo()))) {
                columna.setCellFactory(c -> new TableCell<>() {
                    @Override protected void updateItem(String texto, boolean vacia) {
                        super.updateItem(texto, vacia);
                        setText(vacia ? null : texto);
                        if (vacia || texto == null || texto.isBlank()) setTooltip(null);
                        else {
                            Tooltip detalle = new Tooltip(texto);
                            detalle.setWrapText(true); detalle.setMaxWidth(500);
                            setTooltip(detalle);
                        }
                    }
                });
            }
            tabla.getColumns().add(columna);
        }
        if ("pedido".equals(nombre)) {
            tabla.getStyleClass().add("orders-table");
            tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            tabla.widthProperty().addListener((obs, antes, despues) -> ajustarColumnasPedidos());
        } else {
            tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        }
        tabla.setPlaceholder(new Label("No hay " + plural + " para mostrar."));
        SortedList<T> ordenados = new SortedList<>(filtrados); ordenados.comparatorProperty().bind(tabla.comparatorProperty()); tabla.setItems(ordenados);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> botones()); VBox.setVgrow(tabla, Priority.ALWAYS);
        Label lista = new Label("Listado de " + plural); lista.getStyleClass().add("table-title");
        Label ayuda = new Label("pedido".equals(nombre)
                ? "Desplázate con la barra inferior para leer los comentarios completos."
                : "Selecciona un registro para actualizarlo o eliminarlo"); ayuda.getStyleClass().add("table-hint");
        contador.getStyleClass().add("table-count"); Region separador = new Region(); HBox.setHgrow(separador, Priority.ALWAYS);
        HBox cabecera = new HBox(12, new VBox(5, lista, ayuda), separador, contador); cabecera.setAlignment(Pos.CENTER_LEFT); cabecera.getStyleClass().add("table-header");
        estado.getStyleClass().add("status-text"); Region espacio = new Region(); HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox pie = new HBox(10, estado, espacio, editar, borrar); pie.setAlignment(Pos.CENTER_LEFT); pie.getStyleClass().add("table-footer");
        VBox tarjeta = new VBox(cabecera, tabla);
        HBox zonaHorizontal = "pedido".equals(nombre) ? crearBarraHorizontal() : null;
        if (zonaHorizontal != null) tarjeta.getChildren().add(zonaHorizontal);
        BarrasDesplazamiento.instalar(tabla, zonaHorizontal);
        tarjeta.getChildren().add(pie);
        tarjeta.getStyleClass().add("table-card"); VBox.setVgrow(tarjeta, Priority.ALWAYS);
        Node areaContenido = tarjeta;
        if (productosPorGama != null) {
            VBox detalleGama = crearDetalleGama();
            HBox columnasGama = new HBox(16, tarjeta, detalleGama);
            HBox.setHgrow(tarjeta, Priority.ALWAYS);
            VBox.setVgrow(columnasGama, Priority.ALWAYS);
            areaContenido = columnasGama;
        }
        VBox vista = new VBox(20, new VBox(8, ceja, titulo, subtitulo), acciones, areaContenido); vista.getStyleClass().add("page");
        botones(); actualizarContador(); cargar(); return vista;
    }
    /** La barra exterior comparte su posición con la barra de la tabla. */
    private HBox crearBarraHorizontal() {
        ScrollBar barra = new ScrollBar();
        barra.setOrientation(Orientation.HORIZONTAL);
        barra.getStyleClass().add("orders-scrollbar");
        barra.setMinHeight(12);
        barra.setPrefHeight(12);
        barra.setMaxHeight(12);
        barra.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(barra, Priority.ALWAYS);
        HBox contenedor = new HBox(barra);
        contenedor.getStyleClass().add("orders-scrollbar-area");
        contenedor.setVisible(false);
        contenedor.managedProperty().bind(contenedor.visibleProperty());
        tabla.skinProperty().addListener((observable, anterior, nueva) -> {
            if (nueva == null) return;
            Platform.runLater(() -> {
                ScrollBar interna = tabla.lookupAll(".scroll-bar").stream()
                        .filter(n -> n instanceof ScrollBar)
                        .map(n -> (ScrollBar) n)
                        .filter(b -> b.getOrientation() == Orientation.HORIZONTAL)
                        .findFirst().orElse(null);
                if (interna == null) return;
                barra.minProperty().bind(interna.minProperty());
                barra.maxProperty().bind(interna.maxProperty());
                barra.visibleAmountProperty().bind(interna.visibleAmountProperty());
                barra.valueProperty().bindBidirectional(interna.valueProperty());
                contenedor.visibleProperty().bind(interna.visibleProperty());
            });
        });
        return contenedor;
    }

    /** Muestra los productos de la gama seleccionada sin bloquear la interfaz. */
    private VBox crearDetalleGama() {
        Label etiqueta = new Label("CATÁLOGO  /  PRODUCTOS");
        etiqueta.getStyleClass().add("editorial-eyebrow");
        Label titulo = new Label("Productos de esta gama");
        titulo.getStyleClass().add("detail-title");
        titulo.setWrapText(true);
        Label gamaActual = new Label("Selecciona una gama");
        gamaActual.getStyleClass().add("detail-category");
        gamaActual.setWrapText(true);
        Label cantidad = new Label("Elige una fila para ver sus productos.");
        cantidad.getStyleClass().add("panel-subtitle");
        cantidad.setWrapText(true);
        VBox lista = new VBox(8);
        lista.getStyleClass().add("category-product-list");
        ScrollPane desplazamiento = new ScrollPane(lista);
        desplazamiento.getStyleClass().add("category-products-scroll");
        desplazamiento.setFitToWidth(true);
        desplazamiento.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(desplazamiento, Priority.ALWAYS);
        VBox detalle = new VBox(12, etiqueta, titulo, gamaActual, cantidad, desplazamiento);
        detalle.getStyleClass().add("category-detail");
        detalle.setPrefWidth(300);
        detalle.setMinWidth(260);
        detalle.setMaxWidth(320);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            long solicitud = ++consultaGama;
            lista.getChildren().clear();
            if (seleccionado == null) {
                gamaActual.setText("Selecciona una gama");
                cantidad.setText("Elige una fila para ver sus productos.");
                return;
            }
            String gama = codigo.apply(seleccionado);
            gamaActual.setText(gama);
            cantidad.setText("Consultando productos…");
            Task<List<producto>> consulta = new Task<>() {
                @Override protected List<producto> call() {
                    return productosPorGama.get().stream()
                            .filter(p -> p.getGama() != null && p.getGama().equalsIgnoreCase(gama))
                            .sorted(java.util.Comparator.comparing(producto::getNombre,
                                    java.util.Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                            .toList();
                }
            };
            consulta.setOnSucceeded(evento -> {
                if (solicitud != consultaGama) return;
                List<producto> productos = consulta.getValue();
                cantidad.setText(productos.size() + (productos.size() == 1 ? " producto registrado" : " productos registrados"));
                if (productos.isEmpty()) {
                    Label vacio = new Label("Esta gama aún no tiene productos.");
                    vacio.getStyleClass().add("panel-empty"); vacio.setWrapText(true); lista.getChildren().add(vacio);
                }
                for (producto item : productos) {
                    Label nombreProducto = new Label(item.getNombre());
                    nombreProducto.getStyleClass().add("detail-product-name"); nombreProducto.setWrapText(true);
                    Label resumen = new Label(item.getCodigoProducto() + "  ·  " + item.getCantidadEnStock() + " unidades");
                    resumen.getStyleClass().add("detail-product-meta");
                    VBox fila = new VBox(4, nombreProducto, resumen); fila.getStyleClass().add("detail-product-row");
                    lista.getChildren().add(fila);
                }
            });
            consulta.setOnFailed(evento -> {
                if (solicitud == consultaGama) cantidad.setText("No se pudieron consultar los productos.");
            });
            Thread hilo = new Thread(consulta, "productos-de-gama"); hilo.setDaemon(true); hilo.start();
        });
        return detalle;
    }

    private void cargar() { ejecutar("Consultando " + plural + "…", listar::get, lista -> {
        datos.setAll(lista);
        if ("pedido".equals(nombre)) ajustarColumnasPedidos();
        actualizarContador(); estado.setText("Lista actualizada");
    }); }

    /** Distribuye las columnas visibles y reserva espacio para los comentarios completos. */
    private void ajustarColumnasPedidos() {
        if (tabla.getColumns().size() != 7 || tabla.getWidth() <= 0) return;
        double anchoVisible = Math.max(0, tabla.getWidth() - 20);
        double[] proporciones = {0.11, 0.11, 0.14, 0.15, 0.15, 0.12};
        for (int i = 0; i < proporciones.length; i++) {
            tabla.getColumns().get(i).setPrefWidth(Math.max(75, anchoVisible * proporciones[i]));
        }
        javafx.scene.text.Text medida = new javafx.scene.text.Text();
        medida.setFont(javafx.scene.text.Font.font("Arial", 13));
        double anchoComentario = Math.max(240, anchoVisible * 0.22 + 50);
        for (T registro : datos) {
            String texto = ((pedido) registro).getComentarios();
            if (texto == null) continue;
            for (String linea : texto.split("\\R", -1)) {
                medida.setText(linea);
                anchoComentario = Math.max(anchoComentario, medida.getLayoutBounds().getWidth() + 36);
            }
        }
        tabla.getColumns().get(6).setPrefWidth(Math.min(anchoComentario, 1600));
    }

    private void formulario(T actual) {
        RegistroDialog.mostrar(tabla.getScene().getWindow(), nombre, actual != null, campos.apply(actual), validar).ifPresent(valores -> {
            T registro = construir.apply(valores);
            ejecutar("Guardando " + nombre + "…", () -> { if (actual == null) { guardar.accept(registro); return true; } return actualizar.apply(registro); }, resultado -> {
                aviso(resultado ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING, resultado ? "Cambios guardados" : "Registro no encontrado", resultado ? "La información se guardó correctamente." : "El registro ya no existe."); cargar();
            });
        });
    }
    private void eliminarSeleccion() {
        T item = tabla.getSelectionModel().getSelectedItem(); if (item == null) return;
        if (!AvisoDialogo.confirmarEliminacionRegistro(tabla.getScene().getWindow(), nombre, codigo.apply(item) + " · " + descripcion.apply(item))) return;
        ejecutar("Eliminando " + nombre + "…", () -> eliminar.apply(item), resultado -> {
            aviso(resultado ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING, resultado ? "Registro eliminado" : "Registro no encontrado", resultado ? "Se eliminó correctamente." : "El registro ya no existe."); cargar();
        });
    }
    private <R> void ejecutar(String mensaje, Callable<R> operacion, java.util.function.Consumer<R> fin) {
        ocupado = true; botones(); estado.setText(mensaje);
        Task<R> tarea = new Task<>() { @Override protected R call() throws Exception { return operacion.call(); } };
        tarea.setOnSucceeded(e -> { ocupado = false; botones(); fin.accept(tarea.getValue()); });
        tarea.setOnFailed(e -> { ocupado = false; botones(); estado.setText("No se pudo completar la operación"); aviso(Alert.AlertType.ERROR, "No se pudo completar", mensajeError(tarea.getException())); });
        Thread hilo = new Thread(tarea, "operacion-" + plural); hilo.setDaemon(true); hilo.start();
    }
    private String mensajeError(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) if (causa instanceof SQLException sql) {
            if (sql.getErrorCode() == 1062) return "detalle de pedido".equals(nombre)
                    ? "Este producto ya está registrado en ese pedido. Selecciona el detalle existente para actualizarlo."
                    : "Ya existe un registro con ese código. Elige otro.";
            if (sql.getErrorCode() == 1452) return "El dato relacionado no existe. Comprueba los códigos indicados y que el registro relacionado esté creado.";
            if (sql.getErrorCode() == 1451) return "No se puede eliminar porque existen otros registros relacionados. Por ejemplo, un pedido puede tener detalles asociados.";
        }
        return "Revisa los datos ingresados y la conexión con la base de datos.";
    }
    private void aviso(Alert.AlertType tipo, String titulo, String detalle) { AvisoDialogo.mostrar(tabla.getScene().getWindow(), tipo, titulo, detalle); }
    private void botones() { boolean ninguno = tabla.getSelectionModel().getSelectedItem() == null; recargar.setDisable(ocupado); nuevo.setDisable(ocupado); editar.setDisable(ocupado || ninguno); borrar.setDisable(ocupado || ninguno); }
    private void actualizarContador() { contador.setText(filtrados.size() + " de " + datos.size() + " " + plural); }
    private static Campo campo(String nombre, Object valor, boolean requerido, int longitud, String ayuda) { return new Campo(nombre, valor == null ? null : valor.toString(), requerido, longitud, ayuda); }
    private static String cadena(Object valor) { return valor == null ? "" : valor.toString(); }
    private static String opcional(String valor) { return valor.isEmpty() ? null : valor; }
    private static int entero(String valor) { return Integer.parseInt(valor); }
    private static Integer enteroOpcional(String valor) { return valor.isEmpty() ? null : entero(valor); }
    private static String validarEnteros(List<String> valores, int... posiciones) {
        for (int posicion : posiciones) if (!valores.get(posicion).isEmpty()) try {
            if (entero(valores.get(posicion)) <= 0) return "Los códigos deben ser números enteros positivos.";
        } catch (NumberFormatException ex) { return "Los códigos deben ser números enteros positivos válidos."; }
        return null;
    }
}
