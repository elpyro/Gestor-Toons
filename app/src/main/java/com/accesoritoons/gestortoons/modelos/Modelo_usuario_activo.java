package com.accesoritoons.gestortoons.modelos;

public class Modelo_usuario_activo {
    private String id, nombre, documento, perfil, correo, telefono, direccion, url_foto_usuario, url_documento1,  url_documento2, maximo_inventario,fecha_ultima_modificacion, fecha_y_hora_modificacion,usuario_ultima_modificacion;

    public Modelo_usuario_activo() {
    }

    public Modelo_usuario_activo(String id, String nombre, String documento, String perfil, String correo, String telefono, String direccion, String url_foto_usuario, String url_documento1, String url_documento2, String maximo_inventario, String fecha_ultima_modificacion, String fecha_y_hora_modificacion, String usuario_ultima_modificacion) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.perfil = perfil;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.url_foto_usuario = url_foto_usuario;
        this.url_documento1 = url_documento1;
        this.url_documento2 = url_documento2;
        this.maximo_inventario = maximo_inventario;
        this.fecha_ultima_modificacion = fecha_ultima_modificacion;
        this.fecha_y_hora_modificacion = fecha_y_hora_modificacion;
        this.usuario_ultima_modificacion = usuario_ultima_modificacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUrl_foto_usuario() {
        return url_foto_usuario;
    }

    public void setUrl_foto_usuario(String url_foto_usuario) {
        this.url_foto_usuario = url_foto_usuario;
    }

    public String getUrl_documento1() {
        return url_documento1;
    }

    public void setUrl_documento1(String url_documento1) {
        this.url_documento1 = url_documento1;
    }

    public String getUrl_documento2() {
        return url_documento2;
    }

    public void setUrl_documento2(String url_documento2) {
        this.url_documento2 = url_documento2;
    }

    public String getMaximo_inventario() {
        return maximo_inventario;
    }

    public void setMaximo_inventario(String maximo_inventario) {
        this.maximo_inventario = maximo_inventario;
    }

    public String getFecha_ultima_modificacion() {
        return fecha_ultima_modificacion;
    }

    public void setFecha_ultima_modificacion(String fecha_ultima_modificacion) {
        this.fecha_ultima_modificacion = fecha_ultima_modificacion;
    }

    public String getFecha_y_hora_modificacion() {
        return fecha_y_hora_modificacion;
    }

    public void setFecha_y_hora_modificacion(String fecha_y_hora_modificacion) {
        this.fecha_y_hora_modificacion = fecha_y_hora_modificacion;
    }

    public String getUsuario_ultima_modificacion() {
        return usuario_ultima_modificacion;
    }

    public void setUsuario_ultima_modificacion(String usuario_ultima_modificacion) {
        this.usuario_ultima_modificacion = usuario_ultima_modificacion;
    }
}
