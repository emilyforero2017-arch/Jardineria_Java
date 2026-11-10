package com.jardineria.domain.repository;

import com.jardineria.domain.entity.cliente;
import java.util.List;
import java.util.Optional;

public interface clienteRepository {
    void guardar(cliente cliente);
    Optional<cliente> buscarPorCodigo(int codigoCliente);
    List<cliente> listar();
    boolean actualizar(cliente cliente);
    boolean eliminar(int codigoCliente);
}
