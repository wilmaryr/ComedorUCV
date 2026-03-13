package com.comedorucv.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import com.comedorucv.modelos.PlatoCosto;

public class TarjetaCosto extends JPanel {
    private JLabel lblDia;
    private JTextArea areaDetalles;
    private JButton btnModificar;

    public TarjetaCosto(String dia) {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        setBackground(new Color(250, 250, 250));

        lblDia = new JLabel(dia, SwingConstants.CENTER);
        lblDia.setFont(new Font("Arial", Font.BOLD, 14));

        areaDetalles = new JTextArea("No definido");
        areaDetalles.setEditable(false);
        areaDetalles.setBackground(new Color(250, 250, 250));

        btnModificar = new JButton("Modificar");
        btnModificar.setBackground(new Color(0, 102, 51));
        btnModificar.setForeground(Color.WHITE);

        add(lblDia, BorderLayout.NORTH);
        add(new JScrollPane(areaDetalles), BorderLayout.CENTER);
        add(btnModificar, BorderLayout.SOUTH);
    }

    public void actualizarInfo(PlatoCosto p) {
        if (p == null) {
            areaDetalles.setText("No definido");
        } else {
            String texto = String.format("%s\nCosto: %.2f", p.getNombre(), p.getCosto());
            areaDetalles.setText(texto);
        }
    }

    public JButton getBtnModificar() {
        return btnModificar;
    }
}
