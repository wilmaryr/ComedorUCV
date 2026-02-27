package com.comedorucv.modelos;

public class Empleado extends Usuario {
    private String puesto;

    public Empleado(String cedula, String nombre, String correo, String password, String puesto) {
        super(cedula, nombre, correo, password, "Empleado");
        this.puesto = puesto;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }
}
