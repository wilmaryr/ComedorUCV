package com.comedorucv.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataPathResolver {

    private static String dataFolder = null;

    public static synchronized String getDataFolder() {
        if (dataFolder != null)
            return dataFolder;

        // 1. Explicit variable/property (highest priority)
        String folder = System.getenv("DATA_DIR");
        if (folder == null || folder.isEmpty()) {
            folder = System.getProperty("data.dir");
        }

        if (folder != null && !folder.isEmpty()) {
            dataFolder = folder;
        } else {
            // 2. Foolproof discovery logic
            Path userDir = Paths.get(System.getProperty("user.dir"));

            // Buscamos hacia arriba (padres) hasta encontrar la carpeta 'data' que contenga
            // 'usuarios_sistema.txt'
            Path current = userDir;
            while (current != null) {
                Path candidateData = current.resolve("data");
                if (Files.exists(candidateData) && Files.exists(candidateData.resolve("usuarios_sistema.txt"))) {
                    dataFolder = candidateData.toAbsolutePath().toString();
                    break;
                }

                // Si la carpeta actual se llama PROYECTO_COMEDOR y tiene una subcarpeta data,
                // la tomamos
                if (current.getFileName() != null && current.getFileName().toString().equals("PROYECTO_COMEDOR")
                        && Files.exists(candidateData)) {
                    dataFolder = candidateData.toAbsolutePath().toString();
                    break;
                }

                current = current.getParent();
            }

            // Fallback 1: Buscar en subdirectorios directos de userDir si userDir es
            // "fusion" o similar
            if (dataFolder == null) {
                try {
                    dataFolder = Files.walk(userDir, 8)
                            .filter(p -> p.toFile().isDirectory() && p.getFileName().toString().equals("data"))
                            .filter(p -> Files.exists(p.resolve("usuarios_sistema.txt")))
                            .findFirst()
                            .map(Path::toString)
                            .orElse(null);
                } catch (Exception e) {
                }
            }

            // Fallback 2: Any data folder upwards
            if (dataFolder == null) {
                current = userDir;
                while (current != null) {
                    Path candidateData = current.resolve("data");
                    if (Files.exists(candidateData)) {
                        dataFolder = candidateData.toAbsolutePath().toString();
                        break;
                    }
                    current = current.getParent();
                }
            }

            // Final fallback: Use a generic data folder in userDir
            if (dataFolder == null) {
                Path hardCoded = userDir.resolve("Ingenieria (1)").resolve("Ingenieria")
                        .resolve("Ingenieria").resolve("PROYECTO_COMEDOR").resolve("data");
                if (Files.exists(hardCoded)) {
                    dataFolder = hardCoded.toAbsolutePath().toString();
                } else {
                    dataFolder = userDir.resolve("data").toString();
                }
            }
        }

        // No creamos directorios automáticamente para evitar conflictos y redundancias
        System.out.println("Data folder resolved to: " + dataFolder);
        return dataFolder;
    }

    public static Path resolve(String filename) {
        return Paths.get(getDataFolder(), filename);
    }
}
