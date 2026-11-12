package com.jardineria.infrastructure.ui;

import com.jardineria.application.usecase.clienteUseCase;
import com.jardineria.application.usecase.detallepedidoUseCase;
import com.jardineria.application.usecase.pagoUseCase;
import com.jardineria.domain.entity.pedido;
import com.jardineria.domain.entity.producto;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import com.jardineria.application.usecase.oficinaUseCase;
import com.jardineria.application.usecase.pedidoUseCase;
import com.jardineria.application.usecase.productoUseCase;
import com.jardineria.infrastructure.persistence.fabricaRepositorios;
import com.jardineria.infrastructure.ui.controller.OficinasController;
import com.jardineria.infrastructure.ui.controller.PersonasController;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Circle;

/** Organiza la ventana principal y la navegación entre secciones. */
public class PrincipalView {
    private final fabricaRepositorios repositorios;
    private final oficinaUseCase oficinas;
    private final BorderPane raiz = new BorderPane();
    private final StackPane contenido = new StackPane();
    private final Label ubicacion = new Label();
    private final Map<String, Button> botones = new LinkedHashMap<>();

    public PrincipalView(fabricaRepositorios repositorios) {
        this.repositorios = repositorios;
        this.oficinas = new oficinaUseCase(repositorios.crearOficinaRepository());
    }

    public Parent crearVista() {
        raiz.getStyleClass().add("app-shell");
        raiz.setLeft(crearMenu());
        BorderPane derecha = new BorderPane();
        derecha.setTop(crearCabecera());
        derecha.setCenter(contenido);
        contenido.getStyleClass().add("content-area");
        raiz.setCenter(derecha);
        navegar("Inicio");
        return raiz;
    }

    private VBox crearMenu() {
        HBox marca = new HBox(11, crearLogo(), marcaTexto());
        marca.setAlignment(Pos.CENTER_LEFT);
        marca.getStyleClass().add("brand");
        Label seccion = new Label("ESPACIO DE TRABAJO");
        seccion.getStyleClass().add("sidebar-section");

        VBox lista = new VBox(5);
        agregarMenu(lista, "Inicio", "M3 11 L12 3 L21 11 M5 10 V21 H10 V15 H14 V21 H19 V10");
        agregarMenu(lista, "Oficinas", "M4 21 V5 L13 3 V21 M13 9 H20 V21 M2 21 H22 M7 8 H9 M7 12 H9 M7 16 H9 M16 12 H17 M16 16 H17");
        agregarMenu(lista, "Empleados", "M16 21 V19 C16 16 14 15 10 15 C6 15 4 16 4 19 V21 M10 12 A4 4 0 1 0 10 4 A4 4 0 1 0 10 12 M17 8 C20 8 21 10 21 12 M19 16 C21 17 22 18 22 21");
        agregarMenu(lista, "Clientes", "M16 21 V19 C16 16 14 15 10 15 C6 15 4 16 4 19 V21 M10 12 A4 4 0 1 0 10 4 A4 4 0 1 0 10 12 M19 8 V14 M16 11 H22");
        agregarMenu(lista, "Gamas de productos", "M3 7 H10 L12 9 H21 V20 H3 Z M3 7 V5 H10 L12 7");
        agregarMenu(lista, "Productos", "M12 2 L21 7 V17 L12 22 L3 17 V7 Z M3 7 L12 12 L21 7 M12 12 V22");
        agregarMenu(lista, "Pedidos", "M5 3 H15 L20 8 V21 H5 Z M15 3 V8 H20 M8 12 H17 M8 16 H17");
        agregarMenu(lista, "Detalles de pedidos", "M4 5 H20 M4 10 H20 M4 15 H20 M4 20 H15");
        agregarMenu(lista, "Pagos", "M3 6 H21 V19 H3 Z M3 10 H21 M7 15 H11");

        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);
        Label pie = new Label("GESTIÓN DE JARDINERÍA\nTu espacio de trabajo");
        pie.getStyleClass().add("sidebar-footer");
        VBox menu = new VBox(16, marca, seccion, lista, espacio, pie);
        menu.getStyleClass().add("sidebar");
        menu.setPrefWidth(240);
        menu.setMinWidth(225);
        return menu;
    }

    private void agregarMenu(VBox lista, String nombre, String dibujo) {
        Button boton = new Button(nombre, icono(dibujo));
        boton.getStyleClass().add("nav-button");
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setOnAction(evento -> navegar(nombre));
        botones.put(nombre, boton);
        lista.getChildren().add(boton);
    }

    private HBox crearCabecera() {
        Label ruta = new Label("ESPACIO DE TRABAJO  /  ");
        ruta.getStyleClass().add("header-muted");
        ubicacion.getStyleClass().add("header-current");
        HBox izquierda = new HBox(ruta, ubicacion);
        izquierda.setAlignment(Pos.CENTER_LEFT);
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label saludo = new Label("JARDINERÍA  ·  ESPACIO DE TRABAJO");
        saludo.getStyleClass().add("header-signature");
        HBox cabecera = new HBox(izquierda, espacio, saludo);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.getStyleClass().add("topbar");
        return cabecera;
    }

    private void navegar(String nombre) {
        botones.forEach((clave, boton) -> {
            boton.getStyleClass().remove("nav-active");
            if (clave.equals(nombre)) boton.getStyleClass().add("nav-active");
        });
        ubicacion.setText(nombre.toUpperCase());
        Parent vista;
        if ("Inicio".equals(nombre)) {
            vista = crearInicio();
        } else if ("Oficinas".equals(nombre)) {
            vista = new OficinasController(oficinas).crearVista();
        } else if ("Empleados".equals(nombre)) {
            vista = PersonasController.empleados(repositorios).crearVista();
        } else if ("Clientes".equals(nombre)) {
            vista = PersonasController.clientes(repositorios).crearVista();
        } else if ("Gamas de productos".equals(nombre)) {
            vista = PersonasController.gamas(repositorios).crearVista();
        } else if ("Productos".equals(nombre)) {
            vista = PersonasController.productos(repositorios).crearVista();
        } else if ("Pedidos".equals(nombre)) {
            vista = PersonasController.pedidos(repositorios).crearVista();
        } else if ("Detalles de pedidos".equals(nombre)) {
            vista = PersonasController.detallesPedido(repositorios).crearVista();
        } else if ("Pagos".equals(nombre)) {
            vista = PersonasController.pagos(repositorios).crearVista();
        } else {
            vista = crearProximaSeccion(nombre);
        }
        contenido.getChildren().setAll(vista);
    }

    private Parent crearInicio() {
        Label ceja = new Label("UN ESPACIO PARA CRECER");
        ceja.getStyleClass().add("hero-eyebrow");
        Label titulo = new Label("Bienvenida a\nJardinería.");
        titulo.getStyleClass().add("hero-title");
        Label subtitulo = new Label("Todo lo que necesitas para cuidar y organizar tu negocio, en un solo lugar.");
        subtitulo.getStyleClass().add("hero-copy");
        subtitulo.setWrapText(true);
        subtitulo.setMinHeight(Region.USE_PREF_SIZE);
        subtitulo.setMaxWidth(430);
        Label pieHero = new Label("GESTIÓN  /  NATURALEZA  /  CUIDADO");
        pieHero.getStyleClass().add("hero-caption");
        VBox heroTexto = new VBox(12, ceja, titulo, subtitulo, pieHero);
        heroTexto.setMaxHeight(Region.USE_PREF_SIZE);
        HBox.setHgrow(heroTexto, Priority.ALWAYS);
        HBox hero = new HBox(12, heroTexto, ilustracionBotanica());
        hero.setAlignment(Pos.CENTER_LEFT);
        hero.getStyleClass().add("hero-card");
        hero.setMaxHeight(275);
        VBox.setVgrow(hero, Priority.ALWAYS);

        HBox resumen = new HBox(1.15);
        resumen.setAlignment(Pos.BASELINE_LEFT);
        String tituloResumen = "Un vistazo a tu empresa";
        resumen.setAccessibleText(tituloResumen);
        for (char letra : tituloResumen.toCharArray()) {
            Label caracter = new Label(String.valueOf(letra));
            caracter.getStyleClass().add("summary-letter");
            resumen.getChildren().add(caracter);
        }
        Label resumenAyuda = new Label("Información actual de tu base de datos");
        resumenAyuda.getStyleClass().add("section-subtitle");
        HBox encabezadoResumen = new HBox(18, resumen, resumenAyuda);
        encabezadoResumen.setAlignment(Pos.BASELINE_LEFT);
        encabezadoResumen.getStyleClass().add("summary-heading");
        HBox tarjetas = new HBox(
                tarjeta("OFICINAS", "Sedes registradas", () -> oficinas.listar().size()),
                tarjeta("CLIENTES", "Clientes registrados", () -> new clienteUseCase(repositorios.crearClienteRepository()).listar().size()),
                tarjeta("PRODUCTOS", "Productos registrados", () -> new productoUseCase(repositorios.crearProductoRepository()).listar().size()),
                tarjeta("PEDIDOS", "Pedidos registrados", () -> new pedidoUseCase(repositorios.crearPedidoRepository()).listar().size())
        );
        tarjetas.getChildren().forEach(nodo -> HBox.setHgrow(nodo, Priority.ALWAYS));
        tarjetas.getChildren().get(tarjetas.getChildren().size() - 1).getStyleClass().add("metric-last");
        tarjetas.getStyleClass().add("metric-row");
        VBox resumenCompleto = new VBox(14, encabezadoResumen, tarjetas);
        VBox.setMargin(resumenCompleto, new Insets(8, 0, 0, 0));

        VBox inventario = crearInventario();
        VBox actividad = crearActividad();
        HBox informacion = new HBox(18, actividad, inventario);
        informacion.getStyleClass().add("dashboard-sections");
        HBox.setHgrow(actividad, Priority.ALWAYS);
        HBox.setHgrow(inventario, Priority.ALWAYS);
        informacion.setMaxHeight(300);
        VBox.setVgrow(informacion, Priority.ALWAYS);
        Region espacioInferior = new Region();
        espacioInferior.setMinHeight(0);
        VBox.setVgrow(espacioInferior, Priority.ALWAYS);
        Label detallePie = new Label("CULTIVAR  /  ORGANIZAR  /  CRECER");
        detallePie.getStyleClass().add("dashboard-footer-caption");
        Label frasePie = new Label("Cada día es una nueva oportunidad para crecer.");
        frasePie.getStyleClass().add("dashboard-footer-phrase");
        Region separacionPie = new Region();
        HBox.setHgrow(separacionPie, Priority.ALWAYS);
        HBox pie = new HBox(12, detallePie, separacionPie, frasePie);
        pie.setAlignment(Pos.CENTER_LEFT);
        pie.getStyleClass().add("dashboard-footer");

        VBox inicio = new VBox(16, hero, resumenCompleto, informacion, espacioInferior, pie);
        inicio.getStyleClass().addAll("page", "editorial-page");
        ScrollPane desplazamiento = new ScrollPane(inicio);
        desplazamiento.setFitToWidth(true);
        desplazamiento.setFitToHeight(true);
        desplazamiento.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        desplazamiento.getStyleClass().add("dashboard-scroll");
        return desplazamiento;
    }

    /** Presenta registros existentes sin modificar la información de la empresa. */
    private VBox crearActividad() {
        Label etiqueta = new Label("01  /  ACTIVIDAD DE LA EMPRESA");
        etiqueta.getStyleClass().add("editorial-eyebrow");
        Label titulo = new Label("Pedidos recientes");
        titulo.getStyleClass().add("editorial-title");
        VBox pedidosRecientes = new VBox(6, new Label("Consultando pedidos…"));
        pedidosRecientes.getStyleClass().add("activity-orders");
        pedidosRecientes.setMaxHeight(155);
        VBox.setVgrow(pedidosRecientes, Priority.ALWAYS);
        Task<List<pedido>> consulta = new Task<>() {
            @Override protected List<pedido> call() {
                return new pedidoUseCase(repositorios.crearPedidoRepository()).listar().stream()
                        .sorted(Comparator.comparing(pedido::getFechaPedido,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                                .thenComparing(pedido::getCodigoPedido, Comparator.reverseOrder()))
                        .limit(3).toList();
            }
        };
        consulta.setOnSucceeded(evento -> {
            pedidosRecientes.getChildren().clear();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("es", "CO"));
            for (pedido registro : consulta.getValue()) {
                String fecha = registro.getFechaPedido() == null ? "Sin fecha" : registro.getFechaPedido().format(formato);
                Label nombre = new Label("Pedido #" + registro.getCodigoPedido() + "  ·  " + fecha);
                nombre.getStyleClass().add("activity-order-name");
                Label estado = new Label(registro.getEstado() == null ? "Sin estado" : registro.getEstado());
                estado.getStyleClass().add("activity-order-status");
                Region espacio = new Region();
                HBox.setHgrow(espacio, Priority.ALWAYS);
                HBox fila = new HBox(8, nombre, espacio, estado);
                fila.setAlignment(Pos.CENTER_LEFT);
                fila.getStyleClass().add("activity-row");
                fila.setMaxHeight(48);
                VBox.setVgrow(fila, Priority.ALWAYS);
                pedidosRecientes.getChildren().add(fila);
            }
            if (consulta.getValue().isEmpty()) pedidosRecientes.getChildren().add(new Label("Aún no hay pedidos registrados."));
        });
        consulta.setOnFailed(evento -> pedidosRecientes.getChildren().setAll(new Label("No se pudieron consultar los pedidos.")));
        iniciarConsulta(consulta, "pedidos-recientes");
        HBox cifras = new HBox(24,
                indicadorActividad("pagos registrados", () -> new pagoUseCase(repositorios.crearPagoRepository()).listar().size()),
                indicadorActividad("detalles de pedido", () -> new detallepedidoUseCase(repositorios.crearDetallepedidoRepository()).listar().size()));
        HBox cabecera = cabeceraPanel(titulo, "Ver pedidos", "Pedidos");
        VBox actividad = new VBox(8, etiqueta, cabecera, pedidosRecientes, cifras);
        actividad.getStyleClass().add("dashboard-panel");
        actividad.setPrefWidth(450);
        actividad.setMinWidth(260);
        actividad.setMaxWidth(Double.MAX_VALUE);
        return actividad;
    }

    private VBox crearInventario() {
        Label etiqueta = new Label("02  /  INVENTARIO");
        etiqueta.getStyleClass().add("editorial-eyebrow");
        Label titulo = new Label("Inventario en un vistazo");
        titulo.getStyleClass().add("editorial-title");
        Label descripcion = new Label("Productos con menos unidades disponibles");
        descripcion.getStyleClass().add("panel-subtitle");
        VBox lista = new VBox(7);
        lista.setMaxHeight(155);
        VBox.setVgrow(lista, Priority.ALWAYS);
        Label carga = new Label("Consultando productos…");
        carga.getStyleClass().add("panel-empty");
        lista.getChildren().add(carga);
        Task<List<producto>> consulta = new Task<>() {
            @Override protected List<producto> call() {
                return new productoUseCase(repositorios.crearProductoRepository()).listar().stream()
                        .sorted(Comparator.comparingInt(producto::getCantidadEnStock)
                                .thenComparing(producto::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                        .limit(3).toList();
            }
        };
        consulta.setOnSucceeded(evento -> {
            lista.getChildren().clear();
            for (producto p : consulta.getValue()) {
                Label nombre = new Label(p.getNombre());
                nombre.getStyleClass().add("stock-product-name");
                nombre.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(nombre, Priority.ALWAYS);
                Label cantidad = new Label(p.getCantidadEnStock() + (p.getCantidadEnStock() == 1 ? " unidad" : " unidades"));
                cantidad.getStyleClass().add("stock-product-count");
                HBox fila = new HBox(10, nombre, cantidad);
                fila.setAlignment(Pos.CENTER_LEFT);
                fila.getStyleClass().add("stock-row");
                fila.setMaxHeight(48);
                VBox.setVgrow(fila, Priority.ALWAYS);
                lista.getChildren().add(fila);
            }
            if (consulta.getValue().isEmpty()) {
                Label vacio = new Label("Aún no hay productos registrados.");
                vacio.getStyleClass().add("panel-empty");
                lista.getChildren().add(vacio);
            }
        });
        consulta.setOnFailed(evento -> {
            Label error = new Label("No se pudo consultar el inventario.");
            error.getStyleClass().add("panel-empty");
            lista.getChildren().setAll(error);
        });
        iniciarConsulta(consulta, "inventario-resumen");
        HBox cabecera = cabeceraPanel(titulo, "Ver productos", "Productos");
        VBox panel = new VBox(8, etiqueta, cabecera, descripcion, lista);
        panel.getStyleClass().add("dashboard-panel");
        panel.setPrefWidth(450);
        panel.setMinWidth(260);
        panel.setMaxWidth(Double.MAX_VALUE);
        return panel;
    }

    private HBox cabeceraPanel(Label titulo, String texto, String seccion) {
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Button enlace = new Button(texto + "  →");
        enlace.getStyleClass().add("dashboard-link");
        enlace.setOnAction(evento -> navegar(seccion));
        HBox cabecera = new HBox(10, titulo, espacio, enlace);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        return cabecera;
    }

    private HBox indicadorActividad(String descripcion, Supplier<Integer> consulta) {
        Label cantidad = new Label("…");
        cantidad.getStyleClass().add("activity-count");
        Label nombre = new Label(descripcion);
        nombre.getStyleClass().add("activity-count-label");
        HBox indicador = new HBox(5, cantidad, nombre);
        indicador.setAlignment(Pos.BASELINE_LEFT);
        Task<Integer> tarea = new Task<>() {
            @Override protected Integer call() { return consulta.get(); }
        };
        tarea.setOnSucceeded(evento -> cantidad.setText(String.valueOf(tarea.getValue())));
        tarea.setOnFailed(evento -> {
            cantidad.setText("—");
            nombre.setText(descripcion + " · no disponible");
        });
        iniciarConsulta(tarea, "actividad");
        return indicador;
    }

    private void iniciarConsulta(Task<?> tarea, String nombre) {
        Thread hilo = new Thread(tarea, nombre);
        hilo.setDaemon(true);
        hilo.start();
    }

    /** Una rama lineal acompaña la bienvenida sin competir con el texto. */
    private Node ilustracionBotanica() {
        Pane dibujo = new Pane();
        dibujo.setMinSize(230, 200);
        dibujo.setPrefSize(230, 200);
        dibujo.setMaxSize(230, 200);
        dibujo.setScaleX(1.10);
        dibujo.setScaleY(1.10);
        Circle halo = new Circle(133, 99, 86);
        halo.getStyleClass().add("botanical-halo");
        SVGPath ramas = trazo("M62 196 C88 153 108 109 139 28 M86 154 C79 123 66 107 48 91 M103 119 C127 111 146 99 166 80 M118 85 C111 65 101 48 89 34 M128 59 C143 47 157 37 174 30", "botanical-branch");
        SVGPath hojas = trazo("M78 137 C58 132 48 120 48 105 C65 106 78 117 78 137 Z M96 121 C75 114 68 99 70 84 C86 88 98 102 96 121 Z M107 107 C126 92 143 91 155 96 C147 111 126 119 107 107 Z M118 82 C100 70 95 56 99 44 C114 49 123 65 118 82 Z M130 66 C133 49 145 38 160 34 C160 49 148 62 130 66 Z M139 44 C138 27 146 15 157 10 C163 24 155 37 139 44 Z", "botanical-leaves");
        SVGPath nervios = trazo("M51 108 L77 135 M73 87 L95 118 M111 107 L151 97 M101 47 L117 80 M133 64 L157 37 M141 42 L156 14", "botanical-veins");
        Label nota = new Label("CUIDAR LO QUE\nNOS HACE CRECER");
        nota.getStyleClass().add("botanical-note");
        nota.setLayoutX(118);
        nota.setLayoutY(163);
        dibujo.getChildren().addAll(halo, ramas, hojas, nervios, nota);
        return dibujo;
    }

    private SVGPath trazo(String contenido, String clase) {
        SVGPath forma = new SVGPath();
        forma.setContent(contenido);
        forma.getStyleClass().add(clase);
        return forma;
    }

    private VBox tarjeta(String titulo, String descripcion, Supplier<Integer> consulta) {
        Label categoria = new Label(titulo);
        categoria.getStyleClass().add("metric-label");
        Label valor = new Label("—");
        valor.getStyleClass().add("metric-value");
        Label detalle = new Label("Consultando…");
        detalle.getStyleClass().add("metric-detail");
        VBox tarjeta = new VBox(11, categoria, valor, detalle);
        tarjeta.getStyleClass().add("metric-card");
        tarjeta.setMaxWidth(Double.MAX_VALUE);
        tarjeta.setMinWidth(125);
        Task<Integer> tarea = new Task<>() {
            @Override protected Integer call() { return consulta.get(); }
        };
        tarea.setOnSucceeded(evento -> {
            valor.setText(String.valueOf(tarea.getValue()));
            detalle.setText(descripcion);
        });
        tarea.setOnFailed(evento -> detalle.setText("No disponible"));
        Thread hilo = new Thread(tarea, "resumen-" + titulo.toLowerCase());
        hilo.setDaemon(true);
        hilo.start();
        return tarjeta;
    }

    private Parent crearProximaSeccion(String nombre) {
        Label ceja = new Label("ESPACIO DE TRABAJO");
        ceja.getStyleClass().add("eyebrow");
        Label titulo = new Label(nombre);
        titulo.getStyleClass().add("page-title");
        Label descripcion = new Label("Esta sección está en preparación. Por ahora puedes gestionar tus oficinas.");
        descripcion.getStyleClass().add("page-subtitle");
        descripcion.setWrapText(true);
        Button ir = new Button("Ir a oficinas  →");
        ir.getStyleClass().add("primary-button");
        ir.setOnAction(evento -> navegar("Oficinas"));
        VBox tarjeta = new VBox(14, ceja, titulo, descripcion, ir);
        tarjeta.getStyleClass().add("quick-card");
        VBox pagina = new VBox(tarjeta);
        pagina.getStyleClass().add("page");
        return pagina;
    }

    private VBox marcaTexto() {
        Label titulo = new Label("JARDINERÍA");
        titulo.getStyleClass().add("brand-title");
        Label detalle = new Label("GESTIÓN & NATURALEZA");
        detalle.getStyleClass().add("brand-subtitle");
        return new VBox(3, titulo, detalle);
    }

    private static Node crearLogo() {
        SVGPath hoja = new SVGPath();
        hoja.setContent("M12 21 C12 15 11 8 19 3 C20 11 18 16 12 17 M12 18 C11 13 8 10 4 9 C4 15 7 18 12 19 M12 21 V16");
        hoja.getStyleClass().add("brand-icon");
        StackPane fondo = new StackPane(hoja);
        fondo.getStyleClass().add("brand-icon-box");
        return fondo;
    }

    public static Node crearLogoGrande() {
        Node logo = crearLogo();
        logo.setScaleX(1.55);
        logo.setScaleY(1.55);
        StackPane contenedor = new StackPane(logo);
        contenedor.setPadding(new Insets(15));
        return contenedor;
    }

    private Node icono(String dibujo) {
        SVGPath icono = new SVGPath();
        icono.setContent(dibujo);
        icono.getStyleClass().add("nav-icon");
        return icono;
    }
}
