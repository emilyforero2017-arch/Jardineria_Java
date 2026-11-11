package com.jardineria.infrastructure.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface fabricaConexion {
    Connection crearConexion() throws SQLException;
}
