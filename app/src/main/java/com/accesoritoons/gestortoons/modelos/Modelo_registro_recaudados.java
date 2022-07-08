package com.accesoritoons.gestortoons.modelos;

public class Modelo_registro_recaudados {
    String fecha, ref, nombre, cantidad, valor_recaudo, subtotal, id_vendedor, nombre_vendedor, reacudado_por, id_administrador_recaudo;

    public Modelo_registro_recaudados() {
    }

    public Modelo_registro_recaudados(String fecha, String ref, String nombre, String cantidad, String valor_recaudo, String subtotal, String id_vendedor, String nombre_vendedor, String reacudado_por, String id_administrador_recaudo) {
        this.fecha = fecha;
        this.ref = ref;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.valor_recaudo = valor_recaudo;
        this.subtotal = subtotal;
        this.id_vendedor = id_vendedor;
        this.nombre_vendedor = nombre_vendedor;
        this.reacudado_por = reacudado_por;
        this.id_administrador_recaudo = id_administrador_recaudo;
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

    public String getValor_recaudo() {
        return valor_recaudo;
    }

    public void setValor_recaudo(String valor_recaudo) {
        this.valor_recaudo = valor_recaudo;
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

    public String getReacudado_por() {
        return reacudado_por;
    }

    public void setReacudado_por(String reacudado_por) {
        this.reacudado_por = reacudado_por;
    }

    public String getId_administrador_recaudo() {
        return id_administrador_recaudo;
    }

    public void setId_administrador_recaudo(String id_administrador_recaudo) {
        this.id_administrador_recaudo = id_administrador_recaudo;
    }
}
