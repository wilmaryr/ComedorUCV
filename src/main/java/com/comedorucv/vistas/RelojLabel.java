package com.comedorucv.vistas;

import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class RelojLabel extends JLabel {
    public RelojLabel() {
        setForeground(new Color(200, 230, 255));
        setFont(new Font("Segoe UI", Font.BOLD, 13));

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarHora();
            }
        });
        timer.start();
        actualizarHora();
    }

    private void actualizarHora() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy - hh:mm:ss a",
                new Locale("es", "ES"));
        String fechaActualStr = ahora.format(formatter);
        fechaActualStr = fechaActualStr.substring(0, 1).toUpperCase() + fechaActualStr.substring(1);
        setText(fechaActualStr);
    }
}
