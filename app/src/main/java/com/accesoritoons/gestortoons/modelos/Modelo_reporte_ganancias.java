package com.accesoritoons.gestortoons.modelos;

public class Modelo_reporte_ganancias {
    String fecha, referencia, nombre, cantidad, costo,venta, ganancia, estado,p_compra, recaudo;

    public Modelo_reporte_ganancias() {
    }

    public Modelo_reporte_ganancias(String fecha, String referencia, String nombre, String cantidad, String costo, String venta, String ganancia, String estado, String p_compra, String recaudo) {
        this.fecha = fecha;
        this.referencia = referencia;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.costo = costo;
        this.venta = venta;
        this.ganancia = ganancia;
        this.estado = estado;
        this.p_compra = p_compra;
        this.recaudo = recaudo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getVenta() {
        return venta;
    }

    public void setVenta(String venta) {
        this.venta = venta;
    }

    public String getGanancia() {
        return ganancia;
    }

    public void setGanancia(String ganancia) {
        this.ganancia = ganancia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getP_compra() {
        return p_compra;
    }

    public void setP_compra(String p_compra) {
        this.p_compra = p_compra;
    }

    public String getRecaudo() {
        return recaudo;
    }

    public void setRecaudo(String recaudo) {
        this.recaudo = recaudo;
    }
}
