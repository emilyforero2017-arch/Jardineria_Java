package com.jardineria.domain.repository;

import com.jardineria.domain.entity.oficina;
import java.util.List;
import java.util.Optional;

public interface oficinaRepository {
    void guardar(oficina oficina);
    Optional<oficina> buscarPorCodigo(String codigoOficina);
    List<oficina> listar();
    boolean actualizar(oficina oficina);
    boolean eliminar(String codigoOficina);

}
