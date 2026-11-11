package com.jardineria.infrastructure.database;

import com.jardineria.config.configuracionBasedeDatos;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class fabricaConexionMysql implements fabricaConexion{
    private final configuracionBasedeDatos configuracion; //guarda una referencia al objeto que conoce la dirección, el usuario y la contraseña

    public fabricaConexionMysql() {
        this.configuracion = configuracionBasedeDatos.getInstancia();
    }

    @Override
    //crea la conexion o la solicita con drivermanager a Mysql, guarda datos pero crea la conexion.
    public Connection crearConexion() throws SQLException {
        return DriverManager.getConnection(
                configuracion.getUrl(),
                configuracion.getUsuario(),
                configuracion.getContrasena());
    }

}
