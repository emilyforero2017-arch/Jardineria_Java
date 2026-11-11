package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.entity.gamaproducto;
import com.jardineria.domain.repository.gamaproductoRepository;
import com.jardineria.infrastructure.database.fabricaConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class gamaproductoRepositoryJdbc implements gamaproductoRepository{
    private final fabricaConexion fabricaConexion;

    public gamaproductoRepositoryJdbc(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    @Override
    public void guardar(gamaproducto nuevaGama) {
        String sql = "INSERT INTO gama_producto "
                + "(gama, descripcion_texto, descripcion_html, imagen) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nuevaGama.getGama());
            sentencia.setString(2, nuevaGama.getDescripcionTexto());
            sentencia.setString(3, nuevaGama.getDescripcionHtml());
            sentencia.setString(4, nuevaGama.getImagen());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar la gama", e);
        }
    }

    @Override
    public Optional<gamaproducto> buscarPorGama(String gama) {
        String sql = "SELECT * FROM gama_producto WHERE gama = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, gama);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerGama(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar la gama", e);
        }
    }

    @Override
    public List<gamaproducto> listar() {
        String sql = "SELECT * FROM gama_producto";
        List<gamaproducto> gamas = new ArrayList<>();

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                gamas.add(leerGama(resultado));
            }
            return gamas;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar las gamas", e);
        }
    }

    @Override
    public boolean actualizar(gamaproducto gamaActualizada) {
        String sql = "UPDATE gama_producto SET descripcion_texto = ?, "
                + "descripcion_html = ?, imagen = ? WHERE gama = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, gamaActualizada.getDescripcionTexto());
            sentencia.setString(2, gamaActualizada.getDescripcionHtml());
            sentencia.setString(3, gamaActualizada.getImagen());
            sentencia.setString(4, gamaActualizada.getGama());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar la gama", e);
        }
    }

    @Override
    public boolean eliminar(String gama) {
        String sql = "DELETE FROM gama_producto WHERE gama = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, gama);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar la gama", e);
        }
    }

    private gamaproducto leerGama(ResultSet resultado) throws SQLException {
        return new gamaproducto(
                resultado.getString("gama"),
                resultado.getString("descripcion_texto"),
                resultado.getString("descripcion_html"),
                resultado.getString("imagen"));
    }

}
