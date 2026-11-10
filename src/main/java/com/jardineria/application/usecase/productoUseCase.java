package com.jardineria.application.usecase;

import com.jardineria.domain.entity.producto;
import com.jardineria.domain.repository.productoRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class productoUseCase {
    private final productoRepository repositorio;

    public productoUseCase(productoRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public void registrar(producto nuevoProducto) {
        repositorio.guardar(nuevoProducto);
    }

    public Optional<producto> buscarPorCodigo(String codigoProducto) {
        return repositorio.buscarPorCodigo(codigoProducto);
    }

    public List<producto> listar() {
        return repositorio.listar();
    }

    public boolean actualizar(producto producto) {
        return repositorio.actualizar(producto);
    }

    public boolean eliminar(String codigoProducto) {
        return repositorio.eliminar(codigoProducto);
    }
}
