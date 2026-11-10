package com.jardineria.domain.repository;

import com.jardineria.domain.entity.pago;
import java.util.List;
import java.util.Optional;


public interface pagoRepository {
    void guardar(pago pago);
    Optional<pago> buscarPorClave(int codigoCliente, String idTransaccion);
    List<pago> listar();
    boolean actualizar(pago pago);
    boolean eliminar(int codigoCliente, String idTransaccion);
}
