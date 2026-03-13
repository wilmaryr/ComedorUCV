package com.comedorucv.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.comedorucv.modelos.Plato;

public class TarjetaDia extends JPanel {
    private final Color CARD_BG = new Color(242, 242, 242);
    private final Color TEXT_COLOR = new Color(50, 50, 50);
    private final Color BTN_RED = new Color(230, 0, 0);
    private final Color BTN_GRAY = new Color(120, 120, 120);

    private JLabel lblDia;
    private JLabel lblPlato, lblC1, lblC2, lblBebida, lblPostre, lblCantidad, lblCosto;
    private RoundedButton btnEliminar;
    private String dia;

    public TarjetaDia(String dia) {
        this.dia = dia;
        setLayout(new BorderLayout(10, 10));
        setBackground(CARD_BG);
        setBorder(new RoundedBorder(30));
        setPreferredSize(new Dimension(180, 280));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        lblDia = new JLabel(dia);
        lblDia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDia.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDia.setForeground(TEXT_COLOR);
        content.add(lblDia);
        content.add(Box.createVerticalStrut(10));

        lblPlato = createDetailLabel("Plato principal");
        lblC1 = createDetailLabel("Contorno 1");
        lblC2 = createDetailLabel("Contorno 2");
        lblBebida = createDetailLabel("Bebida");
        lblPostre = createDetailLabel("Postre");
        lblCantidad = createDetailLabel("Cantidad");
        lblCosto = createDetailLabel("Precio: 0.0 bs");
        lblCosto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCosto.setForeground(new Color(0, 100, 0));

        content.add(lblPlato);
        content.add(lblC1);
        content.add(lblC2);
        content.add(lblBebida);
        content.add(lblPostre);
        content.add(lblCantidad);
        content.add(Box.createVerticalStrut(5));
        content.add(lblCosto);

        add(content, BorderLayout.CENTER);

        btnEliminar = new RoundedButton("Eliminar", BTN_GRAY, Color.WHITE, 40);
        btnEliminar.setPreferredSize(new Dimension(140, 45));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(btnEliminar);
        add(btnPanel, BorderLayout.SOUTH);

        actualizarInfo(null);
    }

    private JLabel createDetailLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setForeground(TEXT_COLOR);
        return lbl;
    }

    public void actualizarInfo(Plato p) {
        if (p == null) {
            lblPlato.setText("No definido");
            lblC1.setVisible(false);
            lblC2.setVisible(false);
            lblBebida.setVisible(false);
            lblPostre.setVisible(false);
            lblCantidad.setVisible(false);
            btnEliminar.setBackground(BTN_GRAY);
        } else {
            lblPlato.setText(p.getNombre());
            lblC1.setText(p.getContorno1());
            lblC1.setVisible(true);
            lblC2.setText(p.getContorno2());
            lblC2.setVisible(true);
            lblBebida.setText(p.getBebida());
            lblBebida.setVisible(true);
            lblPostre.setText(p.getPostre());
            lblPostre.setVisible(true);
            lblCantidad.setText("Cantidad: " + p.getDisponibilidad());
            lblCantidad.setVisible(true);
            lblCosto.setVisible(true);
            btnEliminar.setBackground(BTN_RED);
        }
        revalidate();
        repaint();
    }

    public void actualizarCosto(Double costo) {
        if (costo == null || costo == 0.0) {
            lblCosto.setVisible(false);
        } else {
            lblCosto.setText("Precio: " + String.format(java.util.Locale.US, "%.2f", costo) + " bs");
            lblCosto.setVisible(true);
        }
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public String getDia() {
        return dia;
    }
}
