package com.accesoritoons.gestortoons.modelos;

public class Modelo_registro_garantias {
    String fecha, ref, nombre, costo, id_vendedor, nombre_vendedor, reacudado_por, id_administrador_recaudo;

    public Modelo_registro_garantias() {
    }

    public Modelo_registro_garantias(String fecha, String ref, String nombre, String costo, String id_vendedor, String nombre_vendedor, String reacudado_por, String id_administrador_recaudo) {
        this.fecha = fecha;
        this.ref = ref;
        this.nombre = nombre;
        this.costo = costo;
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

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
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
