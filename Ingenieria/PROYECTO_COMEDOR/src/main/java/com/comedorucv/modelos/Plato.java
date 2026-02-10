package com.comedorucv.modelos;

public class Plato {
    private String nombre, contorno1, contorno2, bebida, postre;
    private int insumos, disponibilidad;

    public Plato(String nombre, String c1, String c2, String beb, String pos, int ins, int disp) {
        this.nombre = nombre;
        this.contorno1 = c1;
        this.contorno2 = c2;
        this.bebida = beb;
        this.postre = pos;
        this.insumos = ins;
        this.disponibilidad = disp;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContorno1() {
        return contorno1;
    }

    public String getContorno2() {
        return contorno2;
    }

    public String getBebida() {
        return bebida;
    }

    public String getPostre() {
        return postre;
    }

    public int getDisponibilidad() {
        return disponibilidad;
    }

    public int getInsumos() {
        return insumos;
    }
}
