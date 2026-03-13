package com.comedorucv.controladores;

import com.comedorucv.modelos.ReconocimientoFacial;
import java.io.File;

public class ReconocimientoFacialController {

    private ReconocimientoFacial modelo;

    public ReconocimientoFacialController() {
        this.modelo = new ReconocimientoFacial();
    }

    public boolean verificarIdentidad(String cedula, File archivoImagen) {
        if (archivoImagen == null || !archivoImagen.exists()) {
            return false;
        }

        // Validar que el archivo sea estrictamente .png
        String nombreArchivo = archivoImagen.getName().toLowerCase();
        if (!nombreArchivo.endsWith(".png")) {
            System.err.println("Error: El formato del archivo debe ser .png");
            return false;
        }

        return modelo.validarIdentidad(cedula, archivoImagen);
    }
}
