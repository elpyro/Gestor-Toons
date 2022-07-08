package com.accesoritoons.gestortoons.modelos;

public class Modelo_factura_cliente {
    String id,nombre, telefono,documento,fecha, id_vendedor, tipo, direccion, vendedor, cliente_diamante;

    public Modelo_factura_cliente() {
    }

    public Modelo_factura_cliente(String id, String nombre, String telefono, String documento, String fecha, String id_vendedor, String tipo, String direccion, String vendedor, String cliente_diamante) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.documento = documento;
        this.fecha = fecha;
        this.id_vendedor = id_vendedor;
        this.tipo = tipo;
        this.direccion = direccion;
        this.vendedor = vendedor;
        this.cliente_diamante = cliente_diamante;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(String id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getCliente_diamante() {
        return cliente_diamante;
    }

    public void setCliente_diamante(String cliente_diamante) {
        this.cliente_diamante = cliente_diamante;
    }
}
