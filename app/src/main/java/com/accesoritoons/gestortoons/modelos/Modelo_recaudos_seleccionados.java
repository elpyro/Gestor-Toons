package com.accesoritoons.gestortoons.modelos;

public class Modelo_recaudos_seleccionados {
    String id, precio, cantidad;

    public Modelo_recaudos_seleccionados() {
    }

    public Modelo_recaudos_seleccionados(String id, String precio, String cantidad) {
        this.id = id;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }
}
