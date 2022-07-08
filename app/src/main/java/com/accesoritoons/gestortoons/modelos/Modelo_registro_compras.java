package com.accesoritoons.gestortoons.modelos;

public class Modelo_registro_compras {
    String fecha, id_pedido, nombre, cantidad, costo, subtotal,  id_referencia_vendedor,  nombre_vendedor;

    public Modelo_registro_compras() {
    }

    public Modelo_registro_compras(String fecha, String id_pedido, String nombre, String cantidad, String costo, String subtotal, String id_referencia_vendedor, String nombre_vendedor) {
        this.fecha = fecha;
        this.id_pedido = id_pedido;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.costo = costo;
        this.subtotal = subtotal;
        this.id_referencia_vendedor = id_referencia_vendedor;
        this.nombre_vendedor = nombre_vendedor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(String id_pedido) {
        this.id_pedido = id_pedido;
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

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getId_referencia_vendedor() {
        return id_referencia_vendedor;
    }

    public void setId_referencia_vendedor(String id_referencia_vendedor) {
        this.id_referencia_vendedor = id_referencia_vendedor;
    }

    public String getNombre_vendedor() {
        return nombre_vendedor;
    }

    public void setNombre_vendedor(String nombre_vendedor) {
        this.nombre_vendedor = nombre_vendedor;
    }
}
