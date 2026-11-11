package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.repository.clienteRepository;
import com.jardineria.domain.repository.detallepedidoRepository;
import com.jardineria.domain.repository.empleadoRepository;
import com.jardineria.domain.repository.gamaproductoRepository;
import com.jardineria.domain.repository.oficinaRepository;
import com.jardineria.domain.repository.pagoRepository;
import com.jardineria.domain.repository.pedidoRepository;
import com.jardineria.domain.repository.productoRepository;


public interface fabricaRepositorios {
    oficinaRepository crearOficinaRepository();
    empleadoRepository crearEmpleadoRepository();
    gamaproductoRepository crearGamaproductoRepository();
    clienteRepository crearClienteRepository();
    pedidoRepository crearPedidoRepository();
    productoRepository crearProductoRepository();
    detallepedidoRepository crearDetallepedidoRepository();
    pagoRepository crearPagoRepository();
}


