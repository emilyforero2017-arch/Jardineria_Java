package com.jardineria.application.usecase;

import com.jardineria.domain.entity.empleado;
import com.jardineria.domain.repository.empleadoRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class empleadoUseCase {
    private final empleadoRepository repositorio;

    public empleadoUseCase(empleadoRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public void registrar(empleado nuevoEmpleado) {
        repositorio.guardar(nuevoEmpleado);
    }

    public Optional<empleado> buscarPorCodigo(int codigoEmpleado) {
        return repositorio.buscarPorCodigo(codigoEmpleado);
    }

    public List<empleado> listar() {
        return repositorio.listar();
    }

    public boolean actualizar(empleado empleado) {
        return repositorio.actualizar(empleado);
    }

    public boolean eliminar(int codigoEmpleado) {
        return repositorio.eliminar(codigoEmpleado);
    }

}
