package com.jardineria.domain.entity;

import java.math.BigDecimal;

public class detallepedido {
    private int codigoPedido;
    private String codigoProducto;
    private int cantidad;
    private BigDecimal precioUnidad;
    private short numeroLinea;

    public detallepedido() {
    }

    public detallepedido(int codigoPedido, String codigoProducto,
                         int cantidad, BigDecimal precioUnidad,
                         short numeroLinea) {
        this.codigoPedido = codigoPedido;
        this.codigoProducto = codigoProducto;
        this.cantidad = cantidad;
        this.precioUnidad = precioUnidad;
        this.numeroLinea = numeroLinea;
    }

    public int getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(int codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnidad() {
        return precioUnidad;
    }

    public void setPrecioUnidad(BigDecimal precioUnidad) {
        this.precioUnidad = precioUnidad;
    }

    public short getNumeroLinea() {
        return numeroLinea;
    }

    public void setNumeroLinea(short numeroLinea) {
        this.numeroLinea = numeroLinea;
    }



}
