package com.comedorucv.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.comedorucv.modelos.Usuario;

public class GestorTurnos {

    private static Path getFilePath(String cedula) {
        return DataPathResolver.resolve("TURNOS" + File.separator + "turnos_" + cedula + ".txt");
    }

    public static void registrarTurnoPendiente(Usuario usuario, String dia, String turno, String seleccion,
            double totalAPagar) {
        try {
            Path file = getFilePath(usuario.getCedula());
            if (!Files.exists(file.getParent()))
                Files.createDirectories(file.getParent());

            // Format: ID_UNICO | DIA | TURNO | SELECCION | TOTAL
            String idUnico = System.currentTimeMillis() + "";
            String line = String.format(Locale.US, "%s\t%s\t%s\t%s\t%.2f%n", idUnico, dia, turno, seleccion,
                    totalAPagar);

            Files.writeString(file, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<TurnoPendiente> obtenerTurnosPendientes(Usuario usuario) {
        List<TurnoPendiente> turnos = new ArrayList<>();
        try {
            Path file = getFilePath(usuario.getCedula());
            if (Files.exists(file)) {
                List<String> lines = Files.readAllLines(file);
                for (String l : lines) {
                    if (l.trim().isEmpty())
                        continue;
                    String[] p = l.split("\t");
                    if (p.length >= 5) {
                        turnos.add(new TurnoPendiente(p[0], p[1], p[2], p[3], Double.parseDouble(p[4])));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return turnos;
    }

    public static boolean eliminarTurno(Usuario usuario, String idReserva) {
        try {
            Path file = getFilePath(usuario.getCedula());
            if (!Files.exists(file))
                return false;

            List<String> lines = Files.readAllLines(file);
            List<String> updated = new ArrayList<>();
            boolean removed = false;

            for (String l : lines) {
                if (l.trim().isEmpty())
                    continue;
                String[] p = l.split("\t");
                if (p.length >= 5 && p[0].equals(idReserva)) {
                    removed = true;
                    GestorSaldosYMenu.incrementarDisponibilidad(p[1], p[2]);
                } else {
                    updated.add(l);
                }
            }
            if (removed) {
                Files.write(file, updated, java.nio.charset.StandardCharsets.UTF_8);
            }
            return removed;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean pagarTurno(Usuario usuario, String idReserva) {
        try {
            Path file = getFilePath(usuario.getCedula());
            if (!Files.exists(file))
                return false;

            List<String> lines = Files.readAllLines(file);
            List<String> updated = new ArrayList<>();
            boolean paid = false;

            for (String l : lines) {
                if (l.trim().isEmpty())
                    continue;
                String[] p = l.split("\t");
                if (p.length >= 5 && p[0].equals(idReserva)) {
                    double costo = Double.parseDouble(p[4]);
                    double saldo = GestorSaldosYMenu.leerSaldo(usuario.getCedula());
                    if (saldo >= costo) {
                        GestorSaldosYMenu.guardarSaldo(usuario.getCedula(), saldo - costo,
                                "Pago de Reserva (" + p[3] + ") - " + p[1] + " - " + p[2]);
                        paid = true;
                    } else {
                        updated.add(l);
                    }
                } else {
                    updated.add(l);
                }
            }
            // Overwrite file with remaining un-paid turns
            Files.write(file, updated, java.nio.charset.StandardCharsets.UTF_8);

            return paid;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class TurnoPendiente {
        public String id;
        public String dia;
        public String turno;
        public String seleccion;
        public double costo;

        public TurnoPendiente(String id, String dia, String turno, String seleccion, double costo) {
            this.id = id;
            this.dia = dia;
            this.turno = turno;
            this.seleccion = seleccion;
            this.costo = costo;
        }
    }
}
