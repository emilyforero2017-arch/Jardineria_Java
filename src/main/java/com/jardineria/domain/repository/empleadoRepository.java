package com.jardineria.domain.repository;

import com.jardineria.domain.entity.empleado;
import java.util.List;
import java.util.Optional;

public interface empleadoRepository {
    void guardar(empleado empleado);
    Optional<empleado> buscarPorCodigo(int codigoEmpleado);
    List<empleado> listar();
    boolean actualizar(empleado empleado);
    boolean eliminar(int codigoEmpleado);
}
