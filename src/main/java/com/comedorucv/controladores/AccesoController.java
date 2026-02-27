package com.comedorucv.controladores;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.comedorucv.modelos.Usuario;
import com.comedorucv.utils.DataPathResolver;
import com.comedorucv.utils.GestorSaldosYMenu;

public class AccesoController {

    private final ReconocimientoFacialController facialController = new ReconocimientoFacialController();

    /**
     * Procesa el acceso al comedor validando reconocimiento facial y saldo.
     * 
     * @param usuario             Usuario que intenta acceder.
     * @param archivoImagenSubida Archivo de imagen proporcionado para la
     *                            validación.
     * @return true si se concede el acceso, false en caso contrario.
     */
    public boolean procesarAcceso(Usuario usuario, File archivoImagenSubida) {
        // 1. Validar Reconocimiento Facial usando el nuevo controlador especializado
        if (!facialController.verificarIdentidad(usuario.getCedula(), archivoImagenSubida)) {
            JOptionPane.showMessageDialog(null,
                    "ERROR: Reconocimiento facial fallido.\nLa imagen no coincide con los registros de Secretaría.",
                    "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Buscar turno en la base de datos de TURNOS leyendo el nombre de los
        // archivos
        double costoServicio = 0.0;
        String platoReservado = "";
        String idReserva = "";
        boolean turnoEncontrado = false;
        File archivoTurno = null;

        // Obtener el día actual de la semana en español en mayúsculas (LUNES, MARTES,
        // etc.)
        java.time.DayOfWeek diaActualEn = java.time.LocalDate.now().getDayOfWeek();
        String diaActualEs = "";
        switch (diaActualEn) {
            case MONDAY:
                diaActualEs = "LUNES";
                break;
            case TUESDAY:
                diaActualEs = "MARTES";
                break;
            case WEDNESDAY:
                diaActualEs = "MIERCOLES";
                break;
            case THURSDAY:
                diaActualEs = "JUEVES";
                break;
            case FRIDAY:
                diaActualEs = "VIERNES";
                break;
            case SATURDAY:
                diaActualEs = "SABADO";
                break;
            case SUNDAY:
                diaActualEs = "DOMINGO";
                break;
        }

        // Obtener la hora actual para validar los horarios
        java.time.LocalTime horaActual = java.time.LocalTime.now();

        // ==========================================
        // CONFIGURACIÓN DE HORARIOS (Modificable)
        // ==========================================
        // Horario de Desayuno (Hora militar 0-23, Minutos 0-59)
        java.time.LocalTime inicioDesayuno = java.time.LocalTime.of(8, 0);
        java.time.LocalTime finDesayuno = java.time.LocalTime.of(22, 59);

        // Horario de Almuerzo (Hora militar 0-23, Minutos 0-59)
        java.time.LocalTime inicioAlmuerzo = java.time.LocalTime.of(12, 0);
        java.time.LocalTime finAlmuerzo = java.time.LocalTime.of(21, 40);
        // ==========================================

        String turnoActivo = "";

        // Validar si la hora actual está dentro del rango del Desayuno (inclusivo)
        if (!horaActual.isBefore(inicioDesayuno) && !horaActual.isAfter(finDesayuno)) {
            turnoActivo = "DESAYUNO";
        }
        // Validar si la hora actual está dentro del rango del Almuerzo (inclusivo)
        else if (!horaActual.isBefore(inicioAlmuerzo) && !horaActual.isAfter(finAlmuerzo)) {
            turnoActivo = "ALMUERZO";
        }

        if (turnoActivo.isEmpty()) {
            // Formatear las horas para mostrar en el mensaje de error de forma legible
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");

            JOptionPane.showMessageDialog(null,
                    "ERROR: El comedor se encuentra cerrado fuera del horario de servicio.\n\n" +
                            "Desayuno: " + inicioDesayuno.format(formatter) + " - " + finDesayuno.format(formatter)
                            + "\n" +
                            "Almuerzo: " + inicioAlmuerzo.format(formatter) + " - " + finAlmuerzo.format(formatter),
                    "Comedor Cerrado", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            File carpetaTurnos = DataPathResolver.resolve("TURNOS").toFile();
            if (carpetaTurnos.exists() && carpetaTurnos.isDirectory()) {
                File[] archivos = carpetaTurnos.listFiles();
                if (archivos != null) {
                    for (File f : archivos) {
                        // Buscar el archivo que contenga la cédula del usuario
                        if (f.getName().contains(usuario.getCedula())) {
                            List<String> lineas = Files.readAllLines(f.toPath());
                            for (String linea : lineas) {
                                if (linea.trim().isEmpty())
                                    continue;
                                String[] partes = linea.split("\t");
                                // Recordando el formato: ID_UNICO | DIA | TURNO | SELECCION | TOTAL
                                if (partes.length >= 5) {
                                    String diaReserva = partes[1].trim().toUpperCase();
                                    String turnoReserva = partes[2].trim().toUpperCase();

                                    // Validamos el día de hoy y el horario activo en el que estamos actualmente
                                    if (diaReserva.equals(diaActualEs) && turnoReserva.equals(turnoActivo)) {
                                        idReserva = partes[0];
                                        platoReservado = partes[3];
                                        costoServicio = Double.parseDouble(partes[4]);
                                        turnoEncontrado = true;
                                        archivoTurno = f;
                                        break; // Encontramos el turno correcto, salimos del bucle
                                    }
                                }
                            }
                            if (turnoEncontrado)
                                break; // Terminar si ya encontramos el turno del usuario para HOY y este HORARIO
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!turnoEncontrado) {
            JOptionPane.showMessageDialog(null,
                    "ERROR: No tienes un turno activo para consumir en el comedor en este momento.\n" +
                            "Verifica que hayas reservado el plato para el día de HOY (" + diaActualEs
                            + ") y en el turno actual (" + turnoActivo + ").",
                    "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 3. Validar Saldo
        double saldoActual = GestorSaldosYMenu.leerSaldo(usuario.getCedula());
        if (saldoActual < costoServicio) {
            JOptionPane.showMessageDialog(null,
                    "ERROR: Saldo insuficiente para el plato reservado.\nPlato: " + platoReservado + "\nCosto: "
                            + costoServicio + " bs\nSaldo actual: "
                            + saldoActual + " bs",
                    "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 4. Cobro Automático
        double nuevoSaldo = saldoActual - costoServicio;
        GestorSaldosYMenu.guardarSaldo(usuario.getCedula(), nuevoSaldo,
                "Pago del Servicio Comedor (" + platoReservado + ")");

        // Opcional: Remover el turno cobrado del archivo para que no se cobre dos veces
        if (archivoTurno != null) {
            try {
                List<String> lineas = Files.readAllLines(archivoTurno.toPath());
                List<String> lineasActualizadas = new ArrayList<>();
                for (String linea : lineas) {
                    if (linea.trim().isEmpty())
                        continue;
                    if (!linea.startsWith(idReserva + "\t")) {
                        lineasActualizadas.add(linea); // Solo guardamos los turnos no consumidos
                    }
                }
                Files.write(archivoTurno.toPath(), lineasActualizadas, java.nio.charset.StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JOptionPane.showMessageDialog(null,
                "¡ACCESO CONCEDIDO!\nReconocimiento facial exitoso.\nPlato a consumir: " + platoReservado
                        + "\nCobro realizado: " + costoServicio
                        + " bs\nSaldo restante: " + nuevoSaldo + " bs",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        return true;
    }
}
