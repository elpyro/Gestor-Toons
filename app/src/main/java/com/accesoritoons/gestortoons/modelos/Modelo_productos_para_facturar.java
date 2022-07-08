package com.accesoritoons.gestortoons.modelos;

public class Modelo_productos_para_facturar {
    String nombre, cantidad, costo, total;

    public Modelo_productos_para_facturar(String nombre, String cantidad, String costo, String total) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.costo = costo;
        this.total = total;
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

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
