package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.entity.producto;
import com.jardineria.domain.repository.productoRepository;
import com.jardineria.infrastructure.database.fabricaConexion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class productoRepositoryJdbc implements productoRepository {
     private final fabricaConexion fabricaConexion;

    public productoRepositoryJdbc(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    @Override
    public void guardar(producto nuevoProducto) {
        String sql = "INSERT INTO producto "
                + "(codigo_producto, nombre, gama, dimensiones, proveedor, "
                + "descripcion, cantidad_en_stock, precio_venta, "
                + "precio_proveedor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nuevoProducto.getCodigoProducto());
            asignarDatos(sentencia, nuevoProducto, 2);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el producto", e);
        }
    }

    @Override
    public Optional<producto> buscarPorCodigo(String codigoProducto) {
        String sql = "SELECT * FROM producto WHERE codigo_producto = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, codigoProducto);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerProducto(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el producto", e);
        }
    }

    @Override
    public List<producto> listar() {
        String sql = "SELECT * FROM producto";
        List<producto> productos = new ArrayList<>();

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                productos.add(leerProducto(resultado));
            }
            return productos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los productos", e);
        }
    }

    @Override
    public boolean actualizar(producto productoActualizado) {
        String sql = "UPDATE producto SET nombre = ?, gama = ?, "
                + "dimensiones = ?, proveedor = ?, descripcion = ?, "
                + "cantidad_en_stock = ?, precio_venta = ?, "
                + "precio_proveedor = ? WHERE codigo_producto = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            asignarDatos(sentencia, productoActualizado, 1);
            sentencia.setString(9, productoActualizado.getCodigoProducto());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el producto", e);
        }
    }

    @Override
    public boolean eliminar(String codigoProducto) {
        String sql = "DELETE FROM producto WHERE codigo_producto = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, codigoProducto);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar el producto", e);
        }
    }

    private void asignarDatos(
            PreparedStatement sentencia, producto dato, int inicio)
            throws SQLException {

        sentencia.setString(inicio, dato.getNombre());
        sentencia.setString(inicio + 1, dato.getGama());
        sentencia.setString(inicio + 2, dato.getDimensiones());
        sentencia.setString(inicio + 3, dato.getProveedor());
        sentencia.setString(inicio + 4, dato.getDescripcion());
        sentencia.setShort(inicio + 5, dato.getCantidadEnStock());
        sentencia.setBigDecimal(inicio + 6, dato.getPrecioVenta());

        BigDecimal precioProveedor = dato.getPrecioProveedor();
        if (precioProveedor == null) {
            sentencia.setNull(inicio + 7, Types.DECIMAL);
        } else {
            sentencia.setBigDecimal(inicio + 7, precioProveedor);
        }
    }

    private producto leerProducto(ResultSet resultado) throws SQLException {
        return new producto(
                resultado.getString("codigo_producto"),
                resultado.getString("nombre"),
                resultado.getString("gama"),
                resultado.getString("dimensiones"),
                resultado.getString("proveedor"),
                resultado.getString("descripcion"),
                resultado.getShort("cantidad_en_stock"),
                resultado.getBigDecimal("precio_venta"),
                resultado.getBigDecimal("precio_proveedor"));
    }

}
