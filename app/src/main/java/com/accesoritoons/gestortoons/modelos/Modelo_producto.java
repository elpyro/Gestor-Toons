package com.accesoritoons.gestortoons.modelos;

//https://www.youtube.com/watch?v=4ZUgK3hSYoY&t=118s
public class Modelo_producto {
    private String id, nombre, codigo,    p_compra, p_plantino,p_oro, p_diamante,  p_detal, cantidad,  descripcion, proveedor,imagen, fecha_ultima_modificacion,  usuario_ultima_modificacion, estado, url, seleccion, mis_productos, cliente_mis_productos;

    public Modelo_producto() {

    }


    public Modelo_producto(String id, String nombre, String codigo, String p_compra, String p_plantino, String p_oro, String p_diamante, String p_detal, String cantidad, String descripcion, String proveedor, String imagen, String fecha_ultima_modificacion, String usuario_ultima_modificacion, String estado, String url, String seleccion, String mis_productos, String cliente_mis_productos) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.p_compra = p_compra;
        this.p_plantino = p_plantino;
        this.p_oro = p_oro;
        this.p_diamante = p_diamante;
        this.p_detal = p_detal;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.proveedor = proveedor;
        this.imagen = imagen;
        this.fecha_ultima_modificacion = fecha_ultima_modificacion;
        this.usuario_ultima_modificacion = usuario_ultima_modificacion;
        this.estado = estado;
        this.url = url;
        this.seleccion = seleccion;
        this.mis_productos = mis_productos;
        this.cliente_mis_productos = cliente_mis_productos;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getP_compra() {
        return p_compra;
    }

    public void setP_compra(String p_compra) {
        this.p_compra = p_compra;
    }

    public String getP_plantino() {
        return p_plantino;
    }

    public void setP_plantino(String p_plantino) {
        this.p_plantino = p_plantino;
    }

    public String getP_oro() {
        return p_oro;
    }

    public void setP_oro(String p_oro) {
        this.p_oro = p_oro;
    }

    public String getP_diamante() {
        return p_diamante;
    }

    public void setP_diamante(String p_diamante) {
        this.p_diamante = p_diamante;
    }

    public String getP_detal() {
        return p_detal;
    }

    public void setP_detal(String p_detal) {
        this.p_detal = p_detal;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getFecha_ultima_modificacion() {
        return fecha_ultima_modificacion;
    }

    public void setFecha_ultima_modificacion(String fecha_ultima_modificacion) {
        this.fecha_ultima_modificacion = fecha_ultima_modificacion;
    }

    public String getUsuario_ultima_modificacion() {
        return usuario_ultima_modificacion;
    }

    public void setUsuario_ultima_modificacion(String usuario_ultima_modificacion) {
        this.usuario_ultima_modificacion = usuario_ultima_modificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSeleccion() {
        return seleccion;
    }

    public void setSeleccion(String seleccion) {
        this.seleccion = seleccion;
    }

    public String getMis_productos() {
        return mis_productos;
    }

    public void setMis_productos(String mis_productos) {
        this.mis_productos = mis_productos;
    }

    public String getCliente_mis_productos() {
        return cliente_mis_productos;
    }

    public void setCliente_mis_productos(String cliente_mis_productos) {
        this.cliente_mis_productos = cliente_mis_productos;
    }
}