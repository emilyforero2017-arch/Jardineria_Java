package com.jardineria.application.usecase;

import com.jardineria.domain.entity.cliente;
import com.jardineria.domain.repository.clienteRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class clienteUseCase {
    private final clienteRepository repositorio;

    public clienteUseCase(clienteRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public void registrar(cliente nuevoCliente) {
        repositorio.guardar(nuevoCliente);
    }

    public Optional<cliente> buscarPorCodigo(int codigoCliente) {
        return repositorio.buscarPorCodigo(codigoCliente);
    }

    public List<cliente> listar() {
        return repositorio.listar();
    }

    public boolean actualizar(cliente cliente) {
        return repositorio.actualizar(cliente);
    }

    public boolean eliminar(int codigoCliente) {
        return repositorio.eliminar(codigoCliente);
    }
}
