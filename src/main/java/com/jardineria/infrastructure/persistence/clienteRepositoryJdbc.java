package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.entity.cliente;
import com.jardineria.domain.repository.clienteRepository;
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

public class clienteRepositoryJdbc implements clienteRepository{
    private final fabricaConexion fabricaConexion;

    public clienteRepositoryJdbc(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    @Override
    public void guardar(cliente nuevoCliente) {
        String sql = "INSERT INTO cliente "
                + "(codigo_cliente, nombre_cliente, nombre_contacto, "
                + "apellido_contacto, telefono, fax, linea_direccion1, "
                + "linea_direccion2, ciudad, region, pais, codigo_postal, "
                + "codigo_empleado_rep_ventas, limite_credito) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, nuevoCliente.getCodigoCliente());
            asignarDatos(sentencia, nuevoCliente, 2);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el cliente", e);
        }
    }

    @Override
    public Optional<cliente> buscarPorCodigo(int codigoCliente) {
        String sql = "SELECT * FROM cliente WHERE codigo_cliente = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoCliente);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerCliente(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el cliente", e);
        }
    }

    @Override
    public List<cliente> listar() {
        String sql = "SELECT * FROM cliente";
        List<cliente> clientes = new ArrayList<>();

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                clientes.add(leerCliente(resultado));
            }
            return clientes;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los clientes", e);
        }
    }

    @Override
    public boolean actualizar(cliente clienteActualizado) {
        String sql = "UPDATE cliente SET nombre_cliente = ?, "
                + "nombre_contacto = ?, apellido_contacto = ?, telefono = ?, "
                + "fax = ?, linea_direccion1 = ?, linea_direccion2 = ?, "
                + "ciudad = ?, region = ?, pais = ?, codigo_postal = ?, "
                + "codigo_empleado_rep_ventas = ?, limite_credito = ? "
                + "WHERE codigo_cliente = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            asignarDatos(sentencia, clienteActualizado, 1);
            sentencia.setInt(14, clienteActualizado.getCodigoCliente());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el cliente", e);
        }
    }

    @Override
    public boolean eliminar(int codigoCliente) {
        String sql = "DELETE FROM cliente WHERE codigo_cliente = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoCliente);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar el cliente", e);
        }
    }

    private void asignarDatos(
            PreparedStatement sentencia, cliente dato, int inicio)
            throws SQLException {

        sentencia.setString(inicio, dato.getNombreCliente());
        sentencia.setString(inicio + 1, dato.getNombreContacto());
        sentencia.setString(inicio + 2, dato.getApellidoContacto());
        sentencia.setString(inicio + 3, dato.getTelefono());
        sentencia.setString(inicio + 4, dato.getFax());
        sentencia.setString(inicio + 5, dato.getLineaDireccion1());
        sentencia.setString(inicio + 6, dato.getLineaDireccion2());
        sentencia.setString(inicio + 7, dato.getCiudad());
        sentencia.setString(inicio + 8, dato.getRegion());
        sentencia.setString(inicio + 9, dato.getPais());
        sentencia.setString(inicio + 10, dato.getCodigoPostal());

        if (dato.getCodigoEmpleadoRepVentas() == null) {
            sentencia.setNull(inicio + 11, Types.INTEGER);
        } else {
            sentencia.setInt(inicio + 11, dato.getCodigoEmpleadoRepVentas());
        }

        BigDecimal limite = dato.getLimiteCredito();
        if (limite == null) {
            sentencia.setNull(inicio + 12, Types.DECIMAL);
        } else {
            sentencia.setBigDecimal(inicio + 12, limite);
        }
    }

    private cliente leerCliente(ResultSet resultado) throws SQLException {
        int representanteLeido =
                resultado.getInt("codigo_empleado_rep_ventas");
        Integer codigoRepresentante =
                resultado.wasNull() ? null : representanteLeido;

        return new cliente(
                resultado.getInt("codigo_cliente"),
                resultado.getString("nombre_cliente"),
                resultado.getString("nombre_contacto"),
                resultado.getString("apellido_contacto"),
                resultado.getString("telefono"),
                resultado.getString("fax"),
                resultado.getString("linea_direccion1"),
                resultado.getString("linea_direccion2"),
                resultado.getString("ciudad"),
                resultado.getString("region"),
                resultado.getString("pais"),
                resultado.getString("codigo_postal"),
                codigoRepresentante,
                resultado.getBigDecimal("limite_credito"));
    }
}
