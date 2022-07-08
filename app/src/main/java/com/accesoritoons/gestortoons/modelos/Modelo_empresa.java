package com.accesoritoons.gestortoons.modelos;

public class Modelo_empresa {
    String id, nombre, documento, correo, dominio, telefono1, telefono2, direccion, url, garantia;

    public Modelo_empresa() {
    }

    public Modelo_empresa(String id, String nombre, String documento, String correo, String dominio, String telefono1, String telefono2, String direccion, String url, String garantia) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.correo = correo;
        this.dominio = dominio;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
        this.direccion = direccion;
        this.url = url;
        this.garantia = garantia;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }
}
