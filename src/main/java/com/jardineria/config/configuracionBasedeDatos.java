package com.jardineria.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class configuracionBasedeDatos {
    private static final configuracionBasedeDatos INSTANCIA =
            new configuracionBasedeDatos();

    //Prepara el objeto donde quedarán guardados los datos leídos de config.properties
    private final Properties propiedades = new Properties();

    //buscar → comprobar que existe los dtaso de la base de datos → leer → cerrar.
    private configuracionBasedeDatos() {
        try (InputStream archivo = configuracionBasedeDatos.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) //busca el archivo entre los recursos del proyecto, Si lo encuentra, entrega un InputStream
                {
            
            //si el archivo no se encontro detiene la creaicon de configuracion e informa el motivo 
            if (archivo == null) {
                throw new IllegalStateException(
                        "No se encontró config.properties");
            }

            propiedades.load(archivo); // lee el archivo y llena la “libreta” propiedades
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo leer config.properties", e);
        }
    }

    public static configuracionBasedeDatos getInstancia() {
        return INSTANCIA;
    }

    public String getUrl() {
        return obtener("db.url");
    }

    public String getUsuario() {
        return obtener("db.user");
    }

    public String getContrasena() {
        return obtener("db.password");
    }

    private String obtener(String clave) {
        String valor = propiedades.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Falta la propiedad: " + clave);
        }
        return valor;
    }

}
