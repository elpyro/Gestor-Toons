package com.accesoritoons.gestortoons.modelos;

public class Modelo_registro_inventario_productos {
    String nombre, cantidad, costo, subtotal,id_vendedor, nombre_vendedor, tipo_vendedor;

    public Modelo_registro_inventario_productos() {
    }

    public Modelo_registro_inventario_productos(String nombre, String cantidad, String costo, String subtotal, String id_vendedor, String nombre_vendedor, String tipo_vendedor) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.costo = costo;
        this.subtotal = subtotal;
        this.id_vendedor = id_vendedor;
        this.nombre_vendedor = nombre_vendedor;
        this.tipo_vendedor = tipo_vendedor;
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

    public String getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(String id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public String getNombre_vendedor() {
        return nombre_vendedor;
    }

    public void setNombre_vendedor(String nombre_vendedor) {
        this.nombre_vendedor = nombre_vendedor;
    }

    public String getTipo_vendedor() {
        return tipo_vendedor;
    }

    public void setTipo_vendedor(String tipo_vendedor) {
        this.tipo_vendedor = tipo_vendedor;
    }
}
