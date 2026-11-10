package com.jardineria.domain.repository;

import com.jardineria.domain.entity.pedido;
import java.util.List;
import java.util.Optional;

public interface pedidoRepository {
    void guardar(pedido pedido);
    Optional<pedido> buscarPorCodigo(int codigoPedido);
    List<pedido> listar();
    boolean actualizar(pedido pedido);
    boolean eliminar(int codigoPedido);

}
