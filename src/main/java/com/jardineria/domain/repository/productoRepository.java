package com.jardineria.domain.repository;

import com.jardineria.domain.entity.producto;
import java.util.List;
import java.util.Optional;


public interface productoRepository {
    void guardar(producto producto);
    Optional<producto> buscarPorCodigo(String codigoProducto);
    List<producto> listar();
    boolean actualizar(producto producto);
    boolean eliminar(String codigoProducto);

}
