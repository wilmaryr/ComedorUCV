package com.comedorucv.modelos;

public class Usuario {
    private String cedula;
    private String nombre;
    private String correo;
    private String password;
    private String rol;
    private String carrera;
    private String fechaRegistro;

    public Usuario() {
        this.fechaRegistro = "09/02/2026";
    }

    public Usuario(String cedula, String nombre, String correo, String password) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
    }

    public Usuario(String cedula, String nombre, String correo, String password, String rol) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.fechaRegistro = "09/02/2026";
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        if (cedula != null) {
            this.cedula = cedula.replaceAll("[^0-9]", "");
        } else {
            this.cedula = cedula;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo != null && !correo.contains("@")) {
            this.correo = correo + "@ucv.ve";
        } else {
            this.correo = correo;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
