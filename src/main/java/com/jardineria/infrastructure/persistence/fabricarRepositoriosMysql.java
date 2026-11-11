package com.jardineria.infrastructure.persistence;

import com.jardineria.domain.repository.clienteRepository;
import com.jardineria.domain.repository.detallepedidoRepository;
import com.jardineria.domain.repository.empleadoRepository;
import com.jardineria.domain.repository.gamaproductoRepository;
import com.jardineria.domain.repository.oficinaRepository;
import com.jardineria.domain.repository.pagoRepository;
import com.jardineria.domain.repository.pedidoRepository;
import com.jardineria.domain.repository.productoRepository;
import com.jardineria.infrastructure.database.fabricaConexion;
import java.util.Objects;

public class fabricarRepositoriosMysql implements fabricaRepositorios{
        private final fabricaConexion fabricaConexion;

    public fabricarRepositoriosMysql(fabricaConexion fabricaConexion) {
        this.fabricaConexion = Objects.requireNonNull(fabricaConexion);
    }

    @Override
    public oficinaRepository crearOficinaRepository() {
        return new oficinaRepositoryJdbc(fabricaConexion);
    }

    @Override
    public empleadoRepository crearEmpleadoRepository() {
        return new empleadoRepositoryJdbc(fabricaConexion);
    }

    @Override
    public gamaproductoRepository crearGamaproductoRepository() {
        return new gamaproductoRepositoryJdbc(fabricaConexion);
    }

    @Override
    public clienteRepository crearClienteRepository() {
        return new clienteRepositoryJdbc(fabricaConexion);
    }

    @Override
    public pedidoRepository crearPedidoRepository() {
        return new pedidoRepositoryJdbc(fabricaConexion);
    }

    @Override
    public productoRepository crearProductoRepository() {
        return new productoRepositoryJdbc(fabricaConexion);
    }

    @Override
    public detallepedidoRepository crearDetallepedidoRepository() {
        return new detallepedidoRepositoryJdbc(fabricaConexion);
    }

    @Override
    public pagoRepository crearPagoRepository() {
        return new pagoRepositoryJdbc(fabricaConexion);
    }
}
