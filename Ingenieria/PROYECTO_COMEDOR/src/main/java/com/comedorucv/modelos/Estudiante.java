package com.comedorucv.modelos;

public class Estudiante extends Usuario {
    private String carrera;
    private String facultad;
    private double saldoMonedero;

    public Estudiante(String cedula, String nombre, String correo, String password, String carrera, String facultad) {
        super(cedula, nombre, correo, password, "Estudiante");
        this.carrera = carrera;
        this.facultad = facultad;
        this.saldoMonedero = 0.0;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public double getSaldoMonedero() {
        return saldoMonedero;
    }

    public void setSaldoMonedero(double saldoMonedero) {
        this.saldoMonedero = saldoMonedero;
    }
}
