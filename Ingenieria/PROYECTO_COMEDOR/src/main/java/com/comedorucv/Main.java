package com.comedorucv;

import javax.swing.SwingUtilities;

import com.comedorucv.vistas.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Forzar carpeta de datos del proyecto PROYECTO_COMEDOR/data si existe (o crearla)
        try {
            java.nio.file.Path projData = java.nio.file.Paths.get(System.getProperty("user.dir"), "Ingenieria", "PROYECTO_COMEDOR", "data");
            java.nio.file.Files.createDirectories(projData);
            System.setProperty("data.dir", projData.toAbsolutePath().toString());
        } catch (Exception ex) {
            String dataDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "data").toAbsolutePath().toString();
            System.setProperty("data.dir", dataDir);
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
