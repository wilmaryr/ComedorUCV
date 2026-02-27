package com.comedorucv.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GestorSaldosYMenu {

    private static Path getMonederoPath(String cedula) {
        return DataPathResolver.resolve("MONEDEROS" + java.io.File.separator + "monedero_" + cedula + ".txt");
    }

    private static Path getHistoryPath(String cedula) {
        return DataPathResolver.resolve("MONEDEROS" + java.io.File.separator + "history_" + cedula + ".txt");
    }

    private static Path getCostosPath() {
        return DataPathResolver.resolve("costos_save.txt");
    }

    private static Path getMenuPath() {
        return DataPathResolver.resolve("menu_save.txt");
    }

    private static Path getCatalogoPath() {
        return DataPathResolver.resolve("catalogo_save.txt");
    }

    public static double leerSaldo(String cedula) {
        try {
            Path file = getMonederoPath(cedula);
            if (Files.exists(file)) {
                String content = Files.readString(file).trim();
                return Double.parseDouble(content);
            }
        } catch (Exception e) {
        }
        return 0.0;
    }

    public static void guardarSaldo(String cedula, double nuevoSaldo, String descripcionTransaccion) {
        if (cedula == null || cedula.trim().isEmpty() || nuevoSaldo < 0) {
            return;
        }
        try {
            Path file = getMonederoPath(cedula);
            if (!Files.exists(file.getParent()))
                Files.createDirectories(file.getParent());
            Files.writeString(file, String.format(Locale.US, "%.2f", nuevoSaldo));

            Path history = getHistoryPath(cedula);
            String line = "Transacción: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    + " | Detalle: " + descripcionTransaccion
                    + " | Nuevo saldo: " + String.format(Locale.US, "%.2f", nuevoSaldo) + " bs"
                    + System.lineSeparator();
            Files.writeString(history, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String consultarHistorial(String cedula) {
        try {
            Path history = getHistoryPath(cedula);
            if (Files.exists(history)) {
                return Files.readString(history);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void borrarHistorial(String cedula) {
        try {
            Files.deleteIfExists(getHistoryPath(cedula));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double consultarPrecio(String dia, String turno) {
        try {
            Path file = getCostosPath();
            if (Files.exists(file)) {
                List<String> lines = Files.readAllLines(file);
                for (String l : lines) {
                    String[] p = l.split("\t", -1);
                    if (p.length >= 3 && p[0].equals(dia) && p[1].equals(turno)) {
                        return Double.parseDouble(p[2].replace(",", "."));
                    }
                }
            }
        } catch (Exception e) {
        }
        return 0.0;
    }

    public static double consultarPrecioItem(String itemName) {
        if (itemName == null || itemName.trim().isEmpty())
            return 0.0;
        try {
            Path file = getCatalogoPath();
            if (Files.exists(file)) {
                List<String> lines = Files.readAllLines(file);
                for (String l : lines) {
                    String[] p = l.split("\t", -1);
                    if (p.length >= 7 && p[1].equalsIgnoreCase(itemName.trim())) {
                        return Double.parseDouble(p[6].replace(",", "."));
                    } else if (p.length >= 3 && p[1].equalsIgnoreCase(itemName.trim())) {
                        return Double.parseDouble(p[2].replace(",", "."));
                    }
                }
            }
        } catch (Exception e) {
        }
        return 0.0;
    }

    public static boolean decrementarDisponibilidad(String dia, String turno) {
        try {
            Path file = getMenuPath();
            if (!Files.exists(file))
                return false;

            List<String> lines = Files.readAllLines(file);
            List<String> updatedLines = new ArrayList<>();
            boolean modified = false;

            for (String l : lines) {
                if (l.trim().isEmpty()) {
                    updatedLines.add(l);
                    continue;
                }
                String[] p = l.split("\t", -1);
                if (p.length >= 9 && p[0].equals(dia) && p[1].equals(turno)) {
                    int disp = Integer.parseInt(p[8]);
                    if (disp > 0) {
                        p[8] = String.valueOf(disp - 1);
                        modified = true;
                    }
                }
                updatedLines.add(String.join("\t", p));
            }
            if (modified) {
                Files.write(file, updatedLines, java.nio.charset.StandardCharsets.UTF_8);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean incrementarDisponibilidad(String dia, String turno) {
        try {
            Path file = getMenuPath();
            if (!Files.exists(file))
                return false;

            List<String> lines = Files.readAllLines(file);
            List<String> updatedLines = new ArrayList<>();
            boolean modified = false;

            for (String l : lines) {
                if (l.trim().isEmpty()) {
                    updatedLines.add(l);
                    continue;
                }
                String[] p = l.split("\t", -1);
                if (p.length >= 9 && p[0].equals(dia) && p[1].equals(turno)) {
                    int disp = Integer.parseInt(p[8]);
                    p[8] = String.valueOf(disp + 1);
                    modified = true;
                }
                updatedLines.add(String.join("\t", p));
            }
            if (modified) {
                Files.write(file, updatedLines, java.nio.charset.StandardCharsets.UTF_8);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
