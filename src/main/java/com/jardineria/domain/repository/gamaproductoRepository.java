package com.jardineria.domain.repository;

import com.jardineria.domain.entity.gamaproducto;
import java.util.List;
import java.util.Optional;

public interface gamaproductoRepository {
    void guardar(gamaproducto gamaProducto);
    Optional<gamaproducto> buscarPorGama(String gama);
    List<gamaproducto> listar();
    boolean actualizar(gamaproducto gamaProducto);
    boolean eliminar(String gama);

}
