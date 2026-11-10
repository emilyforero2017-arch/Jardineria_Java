package com.jardineria.application.usecase;

import com.jardineria.domain.entity.oficina;
import com.jardineria.domain.repository.oficinaRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

//Un caso de uso representa una acción del programa, se colcoan los metodos completos. 

public class oficinaUseCase {
    //recibe el repositorio y lo guarda 
    private final oficinaRepository repositorio;

    //Objects.requireNonNull impide crear el caso de uso sin repositorio.
    public oficinaUseCase(oficinaRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public void registrar(oficina nuevaOficina) {
        repositorio.guardar(nuevaOficina);
    }

    public Optional<oficina> buscarPorCodigo(String codigoOficina) {
        return repositorio.buscarPorCodigo(codigoOficina);
    }

    public List<oficina> listar() {
        return repositorio.listar();
    }

    public boolean actualizar(oficina oficina) {
        return repositorio.actualizar(oficina);
    }

    public boolean eliminar(String codigoOficina) {
        return repositorio.eliminar(codigoOficina);
    }
}
