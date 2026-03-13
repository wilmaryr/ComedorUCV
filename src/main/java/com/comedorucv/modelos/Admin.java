package com.comedorucv.modelos;

public class Admin extends Usuario {

    public Admin(String cedula, String nombre, String correo, String password) {
        super(cedula, nombre, correo, password, "Administrador");
    }
}
