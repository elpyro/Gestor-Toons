package com.accesoritoons.gestortoons.modelos;

public class Modelo_graficos {
    int eje_x, eje_y;
    String Leyenda;

    public Modelo_graficos() {
    }

    public Modelo_graficos(int eje_x, int eje_y, String leyenda) {
        this.eje_x = eje_x;
        this.eje_y = eje_y;
        Leyenda = leyenda;
    }

    public int getEje_x() {
        return eje_x;
    }

    public void setEje_x(int eje_x) {
        this.eje_x = eje_x;
    }

    public int getEje_y() {
        return eje_y;
    }

    public void setEje_y(int eje_y) {
        this.eje_y = eje_y;
    }

    public String getLeyenda() {
        return Leyenda;
    }

    public void setLeyenda(String leyenda) {
        Leyenda = leyenda;
    }
}
