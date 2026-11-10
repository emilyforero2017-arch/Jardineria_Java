package com.jardineria.application.usecase;

import com.jardineria.domain.entity.gamaproducto;
import com.jardineria.domain.repository.gamaproductoRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class gamaproductoUseCase {
    private final gamaproductoRepository repositorio;

    public gamaproductoUseCase(gamaproductoRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public void registrar(gamaproducto nuevaGamaProducto) {
        repositorio.guardar(nuevaGamaProducto);
    }

    public Optional<gamaproducto> buscarPorGama(String gama) {
        return repositorio.buscarPorGama(gama);
    }

    public List<gamaproducto> listar() {
        return repositorio.listar();
    }

    public boolean actualizar(gamaproducto gamaProducto) {
        return repositorio.actualizar(gamaProducto);
    }

    public boolean eliminar(String gama) {
        return repositorio.eliminar(gama);
    }

}
