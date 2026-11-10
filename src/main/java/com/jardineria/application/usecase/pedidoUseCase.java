package com.jardineria.application.usecase;

import com.jardineria.domain.entity.pedido;
import com.jardineria.domain.repository.pedidoRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class pedidoUseCase {
    private final pedidoRepository repositorio;

    public pedidoUseCase(pedidoRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public void registrar(pedido nuevoPedido) {
        repositorio.guardar(nuevoPedido);
    }

    public Optional<pedido> buscarPorCodigo(int codigoPedido) {
        return repositorio.buscarPorCodigo(codigoPedido);
    }

    public List<pedido> listar() {
        return repositorio.listar();
    }

    public boolean actualizar(pedido pedido) {
        return repositorio.actualizar(pedido);
    }

    public boolean eliminar(int codigoPedido) {
        return repositorio.eliminar(codigoPedido);
    }
}
