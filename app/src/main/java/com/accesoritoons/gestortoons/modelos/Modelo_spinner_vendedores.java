package com.accesoritoons.gestortoons.modelos;

public class Modelo_spinner_vendedores {
    String id, vendedor, id_vendedor, vista;

    public Modelo_spinner_vendedores() {
    }

    public Modelo_spinner_vendedores(String id, String vendedor, String id_vendedor) {
        this.id = id;
        this.vendedor = vendedor;
        this.id_vendedor = id_vendedor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(String id_vendedor) {
        this.id_vendedor = id_vendedor;
    }

    @Override
    public String toString(){
        vista=id+"    "+vendedor;
        return vista;
    }
}
