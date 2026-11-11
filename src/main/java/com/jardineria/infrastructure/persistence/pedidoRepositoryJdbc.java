package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.entity.pedido;
import com.jardineria.domain.repository.pedidoRepository;
import com.jardineria.infrastructure.database.fabricaConexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class pedidoRepositoryJdbc implements pedidoRepository{
    private final fabricaConexion fabricaConexion;

    public pedidoRepositoryJdbc(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    @Override
    public void guardar(pedido nuevoPedido) {
        String sql = "INSERT INTO pedido "
                + "(codigo_pedido, fecha_pedido, fecha_esperada, "
                + "fecha_entrega, estado, comentarios, codigo_cliente) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, nuevoPedido.getCodigoPedido());
            asignarDatos(sentencia, nuevoPedido, 2);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el pedido", e);
        }
    }

    @Override
    public Optional<pedido> buscarPorCodigo(int codigoPedido) {
        String sql = "SELECT * FROM pedido WHERE codigo_pedido = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoPedido);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerPedido(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el pedido", e);
        }
    }

    @Override
    public List<pedido> listar() {
        String sql = "SELECT * FROM pedido";
        List<pedido> pedidos = new ArrayList<>();

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                pedidos.add(leerPedido(resultado));
            }
            return pedidos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los pedidos", e);
        }
    }

    @Override
    public boolean actualizar(pedido pedidoActualizado) {
        String sql = "UPDATE pedido SET fecha_pedido = ?, "
                + "fecha_esperada = ?, fecha_entrega = ?, estado = ?, "
                + "comentarios = ?, codigo_cliente = ? WHERE codigo_pedido = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            asignarDatos(sentencia, pedidoActualizado, 1);
            sentencia.setInt(7, pedidoActualizado.getCodigoPedido());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el pedido", e);
        }
    }

    @Override
    public boolean eliminar(int codigoPedido) {
        String sql = "DELETE FROM pedido WHERE codigo_pedido = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoPedido);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar el pedido", e);
        }
    }

    private void asignarDatos(
            PreparedStatement sentencia, pedido dato, int inicio)
            throws SQLException {

        sentencia.setDate(
                inicio, Date.valueOf(dato.getFechaPedido()));
        sentencia.setDate(
                inicio + 1, Date.valueOf(dato.getFechaEsperada()));

        if (dato.getFechaEntrega() == null) {
            sentencia.setNull(inicio + 2, Types.DATE);
        } else {
            sentencia.setDate(
                    inicio + 2, Date.valueOf(dato.getFechaEntrega()));
        }

        sentencia.setString(inicio + 3, dato.getEstado());
        sentencia.setString(inicio + 4, dato.getComentarios());
        sentencia.setInt(inicio + 5, dato.getCodigoCliente());
    }

    private pedido leerPedido(ResultSet resultado) throws SQLException {
        Date entrega = resultado.getDate("fecha_entrega");

        return new pedido(
                resultado.getInt("codigo_pedido"),
                resultado.getDate("fecha_pedido").toLocalDate(),
                resultado.getDate("fecha_esperada").toLocalDate(),
                entrega == null ? null : entrega.toLocalDate(),
                resultado.getString("estado"),
                resultado.getString("comentarios"),
                resultado.getInt("codigo_cliente"));
    }
}
