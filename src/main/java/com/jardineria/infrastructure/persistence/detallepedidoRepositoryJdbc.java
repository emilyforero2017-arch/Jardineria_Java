package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.entity.detallepedido;
import com.jardineria.domain.repository.detallepedidoRepository;
import com.jardineria.infrastructure.database.fabricaConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class detallepedidoRepositoryJdbc implements detallepedidoRepository{
    private final fabricaConexion fabricaConexion;

    public detallepedidoRepositoryJdbc(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    @Override
    public void guardar(detallepedido nuevoDetalle) {
        String sql = "INSERT INTO detalle_pedido "
                + "(codigo_pedido, codigo_producto, cantidad, "
                + "precio_unidad, numero_linea) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, nuevoDetalle.getCodigoPedido());
            sentencia.setString(2, nuevoDetalle.getCodigoProducto());
            sentencia.setInt(3, nuevoDetalle.getCantidad());
            sentencia.setBigDecimal(4, nuevoDetalle.getPrecioUnidad());
            sentencia.setShort(5, nuevoDetalle.getNumeroLinea());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo guardar el detalle del pedido", e);
        }
    }

    @Override
    public Optional<detallepedido> buscarPorClave(
            int codigoPedido, String codigoProducto) {

        String sql = "SELECT * FROM detalle_pedido "
                + "WHERE codigo_pedido = ? AND codigo_producto = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoPedido);
            sentencia.setString(2, codigoProducto);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerDetalle(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo buscar el detalle del pedido", e);
        }
    }

    @Override
    public List<detallepedido> listar() {
        String sql = "SELECT * FROM detalle_pedido";
        List<detallepedido> detalles = new ArrayList<>();

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                detalles.add(leerDetalle(resultado));
            }
            return detalles;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudieron listar los detalles de pedido", e);
        }
    }

    @Override
    public boolean actualizar(detallepedido detalleActualizado) {
        String sql = "UPDATE detalle_pedido SET cantidad = ?, "
                + "precio_unidad = ?, numero_linea = ? "
                + "WHERE codigo_pedido = ? AND codigo_producto = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, detalleActualizado.getCantidad());
            sentencia.setBigDecimal(2, detalleActualizado.getPrecioUnidad());
            sentencia.setShort(3, detalleActualizado.getNumeroLinea());
            sentencia.setInt(4, detalleActualizado.getCodigoPedido());
            sentencia.setString(5, detalleActualizado.getCodigoProducto());

            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo actualizar el detalle del pedido", e);
        }
    }

    @Override
    public boolean eliminar(int codigoPedido, String codigoProducto) {
        String sql = "DELETE FROM detalle_pedido "
                + "WHERE codigo_pedido = ? AND codigo_producto = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoPedido);
            sentencia.setString(2, codigoProducto);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo eliminar el detalle del pedido", e);
        }
    }

    private detallepedido leerDetalle(ResultSet resultado)
            throws SQLException {

        return new detallepedido(
                resultado.getInt("codigo_pedido"),
                resultado.getString("codigo_producto"),
                resultado.getInt("cantidad"),
                resultado.getBigDecimal("precio_unidad"),
                resultado.getShort("numero_linea"));
    }

}
