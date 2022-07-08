package com.accesoritoons.gestortoons.modelos;

public class Modelo_pedido {
    String id_pedido, id_producto_pedido, referencia_producto, referencia_vendedor, fecha,cantidad, estado, usuario, nombre_vendedor;

    public Modelo_pedido() {
    }

    public Modelo_pedido(String id_pedido, String id_producto_pedido, String referencia_producto, String referencia_vendedor, String fecha, String cantidad, String estado, String usuario, String nombre_vendedor) {
        this.id_pedido = id_pedido;
        this.id_producto_pedido = id_producto_pedido;
        this.referencia_producto = referencia_producto;
        this.referencia_vendedor = referencia_vendedor;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.estado = estado;
        this.usuario = usuario;
        this.nombre_vendedor = nombre_vendedor;
    }

    public String getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(String id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getId_producto_pedido() {
        return id_producto_pedido;
    }

    public void setId_producto_pedido(String id_producto_pedido) {
        this.id_producto_pedido = id_producto_pedido;
    }

    public String getReferencia_producto() {
        return referencia_producto;
    }

    public void setReferencia_producto(String referencia_producto) {
        this.referencia_producto = referencia_producto;
    }

    public String getReferencia_vendedor() {
        return referencia_vendedor;
    }

    public void setReferencia_vendedor(String referencia_vendedor) {
        this.referencia_vendedor = referencia_vendedor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre_vendedor() {
        return nombre_vendedor;
    }

    public void setNombre_vendedor(String nombre_vendedor) {
        this.nombre_vendedor = nombre_vendedor;
    }
}
