package com.jardineria.application.usecase;

import com.jardineria.domain.entity.detallepedido;
import com.jardineria.domain.repository.detallepedidoRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class detallepedidoUseCase {
    private final detallepedidoRepository repositorio;

    public detallepedidoUseCase(detallepedidoRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public void registrar(detallepedido nuevoDetalle) {
        repositorio.guardar(nuevoDetalle);
    }

    public Optional<detallepedido> buscarPorClave(
            int codigoPedido, String codigoProducto) {
        return repositorio.buscarPorClave(codigoPedido, codigoProducto);
    }

    public List<detallepedido> listar() {
        return repositorio.listar();
    }

    public boolean actualizar(detallepedido detalle) {
        return repositorio.actualizar(detalle);
    }

    public boolean eliminar(int codigoPedido, String codigoProducto) {
        return repositorio.eliminar(codigoPedido, codigoProducto);
    }
}
