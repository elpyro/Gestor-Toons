package com.accesoritoons.gestortoons.modelos;

public class Modelo_reporte_ganancias_lista_pdf {
    String fecha, ref, nombre, cantidad,costo, precio_venta,ganancia, id_vendedor, nombre_vendedor;

    public Modelo_reporte_ganancias_lista_pdf() {
    }

    public Modelo_reporte_ganancias_lista_pdf(String fecha, String ref, String nombre, String cantidad, String costo, String precio_venta, String ganancia, String id_vendedor, String nombre_vendedor) {
        this.fecha = fecha;
        this.ref = ref;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.costo = costo;
        this.precio_venta = precio_venta;
        this.ganancia = ganancia;
        this.id_vendedor = id_vendedor;
        this.nombre_vendedor = nombre_vendedor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
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

    public String getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(String precio_venta) {
        this.precio_venta = precio_venta;
    }

    public String getGanancia() {
        return ganancia;
    }

    public void setGanancia(String ganancia) {
        this.ganancia = ganancia;
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
}
