package com.accesoritoons.gestortoons.modelos;

public class Modelo_historial {
    String Usuario, Actividad, descripcion,fecha, fecha_y_hora, visible,id,referencia;

    public Modelo_historial() {
    }

    public Modelo_historial(String usuario, String actividad, String descripcion, String fecha, String fecha_y_hora, String visible, String id, String referencia) {
        Usuario = usuario;
        Actividad = actividad;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.fecha_y_hora = fecha_y_hora;
        this.visible = visible;
        this.id = id;
        this.referencia = referencia;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getActividad() {
        return Actividad;
    }

    public void setActividad(String actividad) {
        Actividad = actividad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha_y_hora() {
        return fecha_y_hora;
    }

    public void setFecha_y_hora(String fecha_y_hora) {
        this.fecha_y_hora = fecha_y_hora;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
