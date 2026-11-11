package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.entity.oficina;
import com.jardineria.domain.repository.oficinaRepository;
import com.jardineria.infrastructure.database.fabricaConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class oficinaRepositoryJdbc implements oficinaRepository{
    // contiene la fabrica que realizara la conexion con mysql
    private final fabricaConexion fabricaConexion;

    // contrscutor que verifica que la conexion no sea nula
    public oficinaRepositoryJdbc(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    // metodo que inserta los datos de la oficina
    @Override
    public void guardar(oficina nuevaOficina) {
        String sql = "INSERT INTO oficina "
                + "(codigo_oficina, ciudad, pais, region, codigo_postal, "
                + "telefono, linea_direccion1, linea_direccion2) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = fabricaConexion.crearConexion(); // se crea la conexion y se guarda en la variable
             PreparedStatement sentencia = conexion.prepareStatement(sql)) { //se usa la conexion para preparar el sql

            //contiene los datos ingresados por el usuario
            sentencia.setString(1, nuevaOficina.getCodigoOficina());
            sentencia.setString(2, nuevaOficina.getCiudad());
            sentencia.setString(3, nuevaOficina.getPais());
            sentencia.setString(4, nuevaOficina.getRegion());
            sentencia.setString(5, nuevaOficina.getCodigoPostal());
            sentencia.setString(6, nuevaOficina.getTelefono());
            sentencia.setString(7, nuevaOficina.getLineaDireccion1());
            sentencia.setString(8, nuevaOficina.getLineaDireccion2());


            sentencia.executeUpdate(); // inserta los datos a mysql 
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar la oficina", e);
        }
    }

    @Override
    public Optional<oficina> buscarPorCodigo(String codigoOficina) {
        String sql = "SELECT * FROM oficina WHERE codigo_oficina = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, codigoOficina);

            // devuelve el valor ingresado en setencia para detallar si el codigo de la oficina existe 
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerOficina(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar la oficina", e);
        }
    }

    @Override
    public List<oficina> listar() {
        String sql = "SELECT * FROM oficina";
        List<oficina> oficinas = new ArrayList<>(); // creacion de la lista donde se insertaran objetos tipo oficina 

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) { // permite dar el resultado de las filas que recorrio mysql 

            while (resultado.next()) {
                oficinas.add(leerOficina(resultado)); // agrega el resultado a la lista 
            }
            return oficinas; // devuelve la lista o muestra la lista 
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar las oficinas", e);
        }
    }

    @Override
    public boolean actualizar(oficina oficinaActualizada) {
        String sql = "UPDATE oficina SET ciudad = ?, pais = ?, region = ?, "
                + "codigo_postal = ?, telefono = ?, linea_direccion1 = ?, "
                + "linea_direccion2 = ? WHERE codigo_oficina = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, oficinaActualizada.getCiudad());
            sentencia.setString(2, oficinaActualizada.getPais());
            sentencia.setString(3, oficinaActualizada.getRegion());
            sentencia.setString(4, oficinaActualizada.getCodigoPostal());
            sentencia.setString(5, oficinaActualizada.getTelefono());
            sentencia.setString(6, oficinaActualizada.getLineaDireccion1());
            sentencia.setString(7, oficinaActualizada.getLineaDireccion2());
            sentencia.setString(8, oficinaActualizada.getCodigoOficina());

            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar la oficina", e);
        }
    }

    @Override
    public boolean eliminar(String codigoOficina) {
        String sql = "DELETE FROM oficina WHERE codigo_oficina = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, codigoOficina);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar la oficina", e);
        }
    }

    private oficina leerOficina(ResultSet resultado) throws SQLException {
        return new oficina(
                resultado.getString("codigo_oficina"),
                resultado.getString("ciudad"),
                resultado.getString("pais"),
                resultado.getString("region"),
                resultado.getString("codigo_postal"),
                resultado.getString("telefono"),
                resultado.getString("linea_direccion1"),
                resultado.getString("linea_direccion2"));
    }
}
