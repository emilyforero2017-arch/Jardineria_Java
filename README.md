# Jardinería | Gestión y naturaleza

Aplicación de escritorio para administrar la información de una empresa de jardinería. El proyecto está desarrollado en Java con una interfaz gráfica en JavaFX y utiliza MySQL para guardar y consultar los datos.

La aplicación organiza la información de oficinas, empleados, clientes, gamas de productos, productos, pedidos, detalles de pedidos y pagos. Su estructura toma como referencia los principios de la **arquitectura hexagonal**, separando el dominio, los casos de uso y los componentes técnicos.

## Contenido

- [Funcionalidades](#funcionalidades)
- [Tecnologías](#tecnologías)
- [Requisitos](#requisitos)
- [Configuración y ejecución](#configuración-y-ejecución)
- [Base de datos y relaciones](#base-de-datos-y-relaciones)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Flujo de una operación](#flujo-de-una-operación)
- [Estado y consideraciones](#estado-y-consideraciones)
- [Créditos](#créditos)

## Funcionalidades

### Inicio

Al abrir la aplicación se muestra una animación de bienvenida acompañada de un sonido. Después aparece el menú principal, desde el que se puede acceder a las distintas secciones.

La pantalla de inicio presenta un resumen consultado desde la base de datos: cantidad de oficinas, clientes, productos y pedidos; pedidos recientes; y algunos productos con menos unidades disponibles. También contiene accesos a las secciones de pedidos y productos.

### Módulos de gestión

| Sección | Información y acciones principales |
| --- | --- |
| **Oficinas** | Consultar, registrar, actualizar y eliminar oficinas. |
| **Empleados** | Gestionar empleados, incluyendo su oficina y, cuando corresponda, el código de su jefe. |
| **Clientes** | Gestionar clientes y su información de contacto, dirección y representante de ventas opcional. |
| **Gamas de productos** | Gestionar categorías y consultar los productos registrados en la gama seleccionada. |
| **Productos** | Gestionar productos, gama, proveedor, existencias y precios. |
| **Pedidos** | Gestionar pedidos asociados a clientes, con sus fechas, estado y comentarios. |
| **Detalles de pedidos** | Gestionar los productos incluidos en cada pedido, su cantidad, precio por unidad y número de línea. |
| **Pagos** | Gestionar pagos asociados a clientes, con identificador de transacción, fecha, forma de pago y total. |

Las pantallas incluyen un campo para filtrar los datos mostrados, una opción para volver a consultar la lista y formularios para crear y editar registros. Las acciones de edición y eliminación se habilitan al seleccionar una fila. La eliminación solicita confirmación.

Los formularios comprueban campos obligatorios y ciertos formatos antes de guardar. Si una operación no se puede completar, la interfaz muestra un mensaje; por ejemplo, cuando se utiliza un código relacionado que no existe o se intenta eliminar un registro con dependencias.

## Tecnologías

- **Java:** el proyecto está configurado para compilar con nivel de lenguaje 17; se ha trabajado con JDK 21.
- **JavaFX 21:** interfaz gráfica y reproducción del sonido de bienvenida mediante JavaFX Media.
- **Maven:** gestión de dependencias, compilación y ejecución.
- **MySQL:** almacenamiento de la información de la aplicación.
- **JDBC y MySQL Connector/J:** conexión y operaciones entre Java y MySQL.
- **CSS de JavaFX:** estilos de las ventanas, tablas, botones y formularios.

## Requisitos

Antes de ejecutar el proyecto se necesita:

1. Tener instalado un **JDK compatible**. Se recomienda JDK 21, versión utilizada durante el desarrollo.
2. Tener instalado **Apache Maven**.
3. Tener un servidor **MySQL** activo y acceso a una base de datos llamada `jardineria` con las tablas del modelo.
4. Disponer de un entorno gráfico de escritorio para visualizar JavaFX. También puede utilizarse Visual Studio Code con soporte para Java y Maven.

Para comprobar las herramientas desde una terminal:

```bash
java -version
mvn -version
```

Maven descarga las dependencias declaradas en `pom.xml`, incluidas las de JavaFX y el conector de MySQL.

## Configuración y ejecución

### 1. Obtener el proyecto

```bash
git clone https://github.com/emilyforero2017-arch/Jardineria_Java.git
cd Jardineria_Java
```

Si el proyecto ya está abierto en Visual Studio Code, abre la terminal en la carpeta que contiene `pom.xml`.

### 2. Preparar la base de datos

Crea la base de datos `jardineria` en MySQL e importa o ejecuta el script de creación de las ocho tablas y sus relaciones. Si se desean datos de ejemplo, también es necesario importarlos.

> **Importante:** actualmente este repositorio no incluye un archivo SQL con el esquema ni con los datos de ejemplo. La base de datos y los registros que existan en un computador no se copian automáticamente al clonar el proyecto; deben prepararse en la instalación de MySQL que se vaya a utilizar.

Las tablas esperadas son `oficina`, `empleado`, `cliente`, `gama_producto`, `producto`, `pedido`, `detalle_pedido` y `pago`.

### 3. Revisar la conexión

La configuración de conexión se lee desde `src/main/resources/config.properties`. Ajusta sus propiedades según tu instalación de MySQL:

```properties
db.url=jdbc:mysql://localhost:3307/jardineria
db.user=TU_USUARIO
db.password=TU_CONTRASENA
```

El puerto `3307` corresponde al entorno utilizado durante el desarrollo. Si tu servidor usa otro puerto, cambia ese valor. El usuario y la contraseña deben corresponder a tu instalación de MySQL y tener acceso a la base de datos.

### 4. Ejecutar la aplicación

Desde la carpeta del proyecto, ejecuta:

```bash
mvn javafx:run
```

Maven compila el proyecto y abre la interfaz. La clase de entrada configurada es `com.jardineria.Main`.

También puedes utilizar el panel de Maven de Visual Studio Code y ejecutar la tarea `javafx:run` del proyecto. Si la aplicación no encuentra la base de datos, comprueba que MySQL esté iniciado, que la base y sus tablas existan y que `config.properties` tenga la conexión correcta.

## Base de datos y relaciones

El modelo contiene ocho entidades:

- **Oficina:** sede de la empresa.
- **Empleado:** persona que trabaja en una oficina; puede tener otro empleado asignado como jefe.
- **Cliente:** persona o empresa que realiza pedidos; puede tener un empleado como representante de ventas.
- **Gama de producto:** categoría a la que pertenecen los productos.
- **Producto:** artículo con información de existencias y precios, asociado a una gama.
- **Pedido:** registro asociado a un cliente, con fechas y estado.
- **Detalle de pedido:** producto incluido en un pedido, con cantidad, precio por unidad y número de línea.
- **Pago:** registro de un pago asociado a un cliente.

Las relaciones principales son:

```text
Oficina ──< Empleado ──< Cliente ──< Pedido ──< Detalle de pedido >── Producto >── Gama
                 │                    
                 └── Jefe              Cliente ──< Pago
```

Un empleado puede ser jefe de otros empleados. Un cliente puede tener varios pedidos y varios pagos. Cada detalle une un pedido con un producto.

En el modelo actual:

- Un detalle se identifica por la combinación de **código de pedido y código de producto**.
- Un pago se identifica por la combinación de **código de cliente e ID de transacción**.
- Un pago está relacionado con un cliente, pero **no se relaciona directamente con un pedido**.

## Arquitectura

La organización sigue una separación basada en la arquitectura hexagonal:

1. **Dominio (`domain`):** contiene las entidades y las interfaces de repositorio. Las entidades representan la información del negocio; las interfaces declaran las operaciones disponibles para acceder a ella.
2. **Aplicación (`application`):** contiene los casos de uso. Estos reciben un repositorio y coordinan las operaciones que solicita la aplicación.
3. **Infraestructura (`infrastructure`):** contiene los componentes técnicos que conectan el proyecto con el exterior: la interfaz JavaFX, las conexiones y las implementaciones de repositorios mediante JDBC.
4. **Configuración (`config`):** lee las propiedades utilizadas para establecer la conexión con MySQL.

Las interfaces de repositorio permiten que los casos de uso dependan de un contrato. Las clases JDBC implementan ese contrato y ejecutan las consultas y modificaciones en MySQL. `fabricaConexionMysql` crea conexiones y `fabricarRepositoriosMysql` proporciona las implementaciones de repositorios que utiliza la aplicación.

## Estructura del proyecto

```text
jardineria_taller/
├── pom.xml
├── README.md
├── src/
│   └── main/
│       ├── java/com/jardineria/
│       │   ├── Main.java
│       │   ├── application/
│       │   │   └── usecase/          # Casos de uso
│       │   ├── config/               # Lectura de configuración
│       │   ├── domain/
│       │   │   ├── entity/           # Entidades del dominio
│       │   │   └── repository/       # Interfaces de repositorio
│       │   └── infrastructure/
│       │       ├── database/          # Creación de conexiones
│       │       ├── persistence/       # Repositorios JDBC y fábrica
│       │       └── ui/                # Ventanas, controladores y diálogos JavaFX
│       └── resources/
│           ├── config.properties      # Configuración de conexión
│           ├── audio/                 # Sonido y créditos de la entrada
│           └── styles/                # Estilos JavaFX
└── target/                            # Archivos generados por Maven
```

## Flujo de una operación

Por ejemplo, al registrar una oficina:

1. La persona completa y guarda el formulario en la interfaz JavaFX.
2. El controlador de la pantalla recibe los datos y llama al caso de uso de oficinas.
3. El caso de uso utiliza el contrato del repositorio para solicitar el guardado.
4. La implementación JDBC abre una conexión, prepara el `INSERT` y lo ejecuta en MySQL.
5. La interfaz informa el resultado y vuelve a consultar la lista.

El mismo principio de separación se aplica a las demás entidades. Las consultas y operaciones de las pantallas se realizan mediante los casos de uso y repositorios correspondientes.

## Estado y consideraciones

- La aplicación funciona como una herramienta de gestión interna y actualmente **no incluye inicio de sesión**.
- Para editar un detalle de pedido se conservan su código de pedido y su código de producto; para editar un pago se conservan el código del cliente y el ID de transacción.
- La fecha esperada y la fecha de entrega de un pedido no pueden ser anteriores a la fecha del pedido al guardarlo desde el formulario. La fecha de entrega es opcional.
- Registrar o modificar detalles de pedido **no descuenta automáticamente las unidades del inventario**. Ese comportamiento queda pendiente de definir e implementar.
- La asociación directa entre pagos y pedidos es una posible ampliación; actualmente los pagos se registran por cliente.
- La información visualizada depende de los datos que tenga la base de datos conectada. Una tabla sin registros puede mostrar un listado vacío.
- Los archivos de `target/` son resultados generados por Maven durante la compilación.

## Créditos

El proyecto toma como referencia la estructura general del repositorio [trainingLeader/hexagonal-appjava](https://github.com/trainingLeader/hexagonal-appjava).

El sonido de la entrada es **“Intro Logo”**, de Aleksandr Karabanov (Muzaproduction), obtenido de [Pixabay](https://pixabay.com/sound-effects/musical-intro-logo-13488/). La información del recurso también se encuentra en `src/main/resources/audio/CREDITOS.md`.
