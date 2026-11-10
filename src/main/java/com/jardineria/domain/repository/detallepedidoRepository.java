package com.jardineria.domain.repository;

import com.jardineria.domain.entity.detallepedido;
import java.util.List;
import java.util.Optional;

public interface detallepedidoRepository {
    void guardar(detallepedido detalle);
    Optional<detallepedido> buscarPorClave(int codigoPedido, String codigoProducto);
    List<detallepedido> listar();
    boolean actualizar(detallepedido detalle);
    boolean eliminar(int codigoPedido, String codigoProducto);

}
