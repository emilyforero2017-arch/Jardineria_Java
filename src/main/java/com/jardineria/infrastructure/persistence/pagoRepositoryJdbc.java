package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.entity.pago;
import com.jardineria.domain.repository.pagoRepository;
import com.jardineria.infrastructure.database.fabricaConexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class pagoRepositoryJdbc implements pagoRepository{
    private final fabricaConexion fabricaConexion;

    public pagoRepositoryJdbc(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    @Override
    public void guardar(pago nuevoPago) {
        String sql = "INSERT INTO pago "
                + "(codigo_cliente, forma_pago, id_transaccion, "
                + "fecha_pago, total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, nuevoPago.getCodigoCliente());
            sentencia.setString(2, nuevoPago.getFormaPago());
            sentencia.setString(3, nuevoPago.getIdTransaccion());
            sentencia.setDate(4, Date.valueOf(nuevoPago.getFechaPago()));
            sentencia.setBigDecimal(5, nuevoPago.getTotal());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el pago", e);
        }
    }

    @Override
    public Optional<pago> buscarPorClave(
            int codigoCliente, String idTransaccion) {

        String sql = "SELECT * FROM pago "
                + "WHERE codigo_cliente = ? AND id_transaccion = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoCliente);
            sentencia.setString(2, idTransaccion);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerPago(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el pago", e);
        }
    }

    @Override
    public List<pago> listar() {
        String sql = "SELECT * FROM pago";
        List<pago> pagos = new ArrayList<>();

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                pagos.add(leerPago(resultado));
            }
            return pagos;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los pagos", e);
        }
    }

    @Override
    public boolean actualizar(pago pagoActualizado) {
        String sql = "UPDATE pago SET forma_pago = ?, fecha_pago = ?, "
                + "total = ? WHERE codigo_cliente = ? AND id_transaccion = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, pagoActualizado.getFormaPago());
            sentencia.setDate(
                    2, Date.valueOf(pagoActualizado.getFechaPago()));
            sentencia.setBigDecimal(3, pagoActualizado.getTotal());
            sentencia.setInt(4, pagoActualizado.getCodigoCliente());
            sentencia.setString(5, pagoActualizado.getIdTransaccion());

            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el pago", e);
        }
    }

    @Override
    public boolean eliminar(int codigoCliente, String idTransaccion) {
        String sql = "DELETE FROM pago "
                + "WHERE codigo_cliente = ? AND id_transaccion = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoCliente);
            sentencia.setString(2, idTransaccion);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar el pago", e);
        }
    }

    private pago leerPago(ResultSet resultado) throws SQLException {
        return new pago(
                resultado.getInt("codigo_cliente"),
                resultado.getString("forma_pago"),
                resultado.getString("id_transaccion"),
                resultado.getDate("fecha_pago").toLocalDate(),
                resultado.getBigDecimal("total"));
    }
}
