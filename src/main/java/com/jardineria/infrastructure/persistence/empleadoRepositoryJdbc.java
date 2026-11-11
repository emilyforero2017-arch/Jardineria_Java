package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.entity.empleado;
import com.jardineria.domain.repository.empleadoRepository;
import com.jardineria.infrastructure.database.fabricaConexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class empleadoRepositoryJdbc implements empleadoRepository {
    private final fabricaConexion fabricaConexion;

    public empleadoRepositoryJdbc(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    @Override
    public void guardar(empleado nuevoEmpleado) {
        String sql = "INSERT INTO empleado "
                + "(codigo_empleado, nombre, apellido1, apellido2, extension, "
                + "email, codigo_oficina, codigo_jefe, puesto) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, nuevoEmpleado.getCodigoEmpleado());
            asignarDatos(sentencia, nuevoEmpleado, 2);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo guardar el empleado", e);
        }
    }

    @Override
    public Optional<empleado> buscarPorCodigo(int codigoEmpleado) {
        String sql = "SELECT * FROM empleado WHERE codigo_empleado = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoEmpleado);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerEmpleado(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el empleado", e);
        }
    }

    @Override
    public List<empleado> listar() {
        String sql = "SELECT * FROM empleado";
        List<empleado> empleados = new ArrayList<>();

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                empleados.add(leerEmpleado(resultado));
            }
            return empleados;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron listar los empleados", e);
        }
    }

    @Override
    public boolean actualizar(empleado empleadoActualizado) {
        String sql = "UPDATE empleado SET nombre = ?, apellido1 = ?, "
                + "apellido2 = ?, extension = ?, email = ?, "
                + "codigo_oficina = ?, codigo_jefe = ?, puesto = ? "
                + "WHERE codigo_empleado = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            asignarDatos(sentencia, empleadoActualizado, 1);
            sentencia.setInt(9, empleadoActualizado.getCodigoEmpleado());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo actualizar el empleado", e);
        }
    }

    @Override
    public boolean eliminar(int codigoEmpleado) {
        String sql = "DELETE FROM empleado WHERE codigo_empleado = ?";

        try (Connection conexion = fabricaConexion.crearConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, codigoEmpleado);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo eliminar el empleado", e);
        }
    }

    private void asignarDatos(
            PreparedStatement sentencia, empleado dato, int inicio)
            throws SQLException {

        sentencia.setString(inicio, dato.getNombre());
        sentencia.setString(inicio + 1, dato.getApellido1());
        sentencia.setString(inicio + 2, dato.getApellido2());
        sentencia.setString(inicio + 3, dato.getExtension());
        sentencia.setString(inicio + 4, dato.getEmail());
        sentencia.setString(inicio + 5, dato.getCodigoOficina());

        if (dato.getCodigoJefe() == null) {
            sentencia.setNull(inicio + 6, Types.INTEGER);
        } else {
            sentencia.setInt(inicio + 6, dato.getCodigoJefe());
        }

        sentencia.setString(inicio + 7, dato.getPuesto());
    }

    private empleado leerEmpleado(ResultSet resultado) throws SQLException {
        int jefeLeido = resultado.getInt("codigo_jefe");
        Integer codigoJefe = resultado.wasNull() ? null : jefeLeido;

        return new empleado(
                resultado.getInt("codigo_empleado"),
                resultado.getString("nombre"),
                resultado.getString("apellido1"),
                resultado.getString("apellido2"),
                resultado.getString("extension"),
                resultado.getString("email"),
                resultado.getString("codigo_oficina"),
                codigoJefe,
                resultado.getString("puesto"));
    }

}
