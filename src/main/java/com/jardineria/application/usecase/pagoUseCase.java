package com.jardineria.application.usecase;

import com.jardineria.domain.entity.pago;
import com.jardineria.domain.repository.pagoRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class pagoUseCase {
    private final pagoRepository repositorio;

    public pagoUseCase(pagoRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public void registrar(pago nuevoPago) {
        repositorio.guardar(nuevoPago);
    }

    public Optional<pago> buscarPorClave(
            int codigoCliente, String idTransaccion) {
        return repositorio.buscarPorClave(codigoCliente, idTransaccion);
    }

    public List<pago> listar() {
        return repositorio.listar();
    }

    public boolean actualizar(pago pago) {
        return repositorio.actualizar(pago);
    }

    public boolean eliminar(int codigoCliente, String idTransaccion) {
        return repositorio.eliminar(codigoCliente, idTransaccion);
    }
}
