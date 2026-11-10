package com.jardineria.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;


public class pago {
    private int codigoCliente;
    private String formaPago;
    private String idTransaccion;
    private LocalDate fechaPago;
    private BigDecimal total;

    public pago() {
    }

    public pago(int codigoCliente, String formaPago, String idTransaccion,
                LocalDate fechaPago, BigDecimal total) {
        this.codigoCliente = codigoCliente;
        this.formaPago = formaPago;
        this.idTransaccion = idTransaccion;
        this.fechaPago = fechaPago;
        this.total = total;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }



}
