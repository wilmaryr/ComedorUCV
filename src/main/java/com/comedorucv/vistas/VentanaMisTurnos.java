package com.comedorucv.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;

import javax.swing.OverlayLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.comedorucv.modelos.Usuario;
import com.comedorucv.utils.GestorTurnos;
import com.comedorucv.utils.GestorTurnos.TurnoPendiente;

public class VentanaMisTurnos extends JFrame {

    private Usuario usuarioActual;
    private JPanel listaPanel;

    public VentanaMisTurnos(Usuario u) {
        this.usuarioActual = u;
        setTitle("Mis Turnos Reservados");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel() {
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        topBar.setLayout(new OverlayLayout(topBar));
        topBar.setBackground(new Color(0, 51, 78));
        topBar.setPreferredSize(new Dimension(getWidth(), 85));

        LogoPlaceholder logo = new LogoPlaceholder();
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        left.setOpaque(false);
        left.add(logo);

        JLabel inicio = new JLabel("<html><u>Inicio</u></html>");
        inicio.setForeground(Color.WHITE);
        inicio.setFont(new Font("Arial", Font.BOLD, 14));
        inicio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inicio.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ComensalFrame(usuarioActual).setVisible(true);
                dispose();
            }
        });
        JPanel inicioHolder = new JPanel(new GridBagLayout());
        inicioHolder.setOpaque(false);
        inicioHolder.setPreferredSize(new Dimension(120, 88));
        inicioHolder.add(inicio);
        left.add(inicioHolder);
        topBar.add(new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30)) {
            {
                setOpaque(false);
                JLabel titleLabel = new JLabel("COMEDOR UCV", SwingConstants.CENTER);
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                add(titleLabel);
            }
        });

        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);

        JLabel cerrar = new JLabel("<html><u>Cerrar sesion</u></html>");
        cerrar.setForeground(Color.WHITE);
        cerrar.setFont(new Font("Arial", Font.BOLD, 14));
        cerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (JOptionPane.showConfirmDialog(null, "¿Deseas cerrar sesión?", "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    dispose();
                }
            }
        });
        JPanel rightHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 15));
        rightHolder.setOpaque(false);
        rightHolder.setPreferredSize(new Dimension(160, 44));
        rightHolder.add(cerrar);

        navContainer.add(rightHolder, BorderLayout.NORTH);

        RelojLabel dateLabel = new RelojLabel();
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        datePanel.setOpaque(false);
        datePanel.add(dateLabel);

        navContainer.add(datePanel, BorderLayout.CENTER);

        JPanel sidesPanel = new JPanel(new BorderLayout());
        sidesPanel.setOpaque(false);
        sidesPanel.setBorder(new EmptyBorder(0, 40, 0, 40));
        sidesPanel.add(left, BorderLayout.WEST);
        sidesPanel.add(navContainer, BorderLayout.EAST);

        topBar.add(sidesPanel);

        add(topBar, BorderLayout.NORTH);

        // Center Area
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);

        JLabel titulo = new JLabel("Mis Turnos", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(new Color(0, 51, 78));
        titulo.setBorder(new EmptyBorder(12, 0, 18, 0));
        center.add(titulo, BorderLayout.NORTH);

        listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(listaPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        center.add(scroll, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // Bottom Strip
        JPanel blueStrip = new JPanel();
        blueStrip.setBackground(new Color(0, 51, 78));
        blueStrip.setPreferredSize(new Dimension(0, 12));
        add(blueStrip, BorderLayout.SOUTH);

        setLocationRelativeTo(null);

        cargarTurnos();
    }

    private void cargarTurnos() {
        listaPanel.removeAll();

        List<TurnoPendiente> turnos = GestorTurnos.obtenerTurnosPendientes(usuarioActual);
        if (turnos.isEmpty()) {
            JLabel lblVacio = new JLabel("No tienes reservaciones.");
            lblVacio.setFont(new Font("Arial", Font.ITALIC, 16));
            lblVacio.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            listaPanel.add(Box.createVerticalStrut(50));
            listaPanel.add(lblVacio);
        } else {
            for (TurnoPendiente t : turnos) {
                listaPanel.add(crearFila(t));
                listaPanel.add(Box.createVerticalStrut(10));
            }
        }

        listaPanel.revalidate();
        listaPanel.repaint();
    }

    private JPanel crearFila(TurnoPendiente t) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout());
        row.setBackground(new Color(245, 245, 245));
        row.setBorder(new RoundedBorder(15));
        row.setMaximumSize(new Dimension(800, 100));
        row.setPreferredSize(new Dimension(800, 100));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 15, 10, 10));

        JLabel lblDiaTurno = new JLabel(t.dia + " - " + t.turno);
        lblDiaTurno.setFont(new Font("Arial", Font.BOLD, 16));
        lblDiaTurno.setForeground(new Color(0, 51, 78));

        JLabel lblItems = new JLabel("Ítems: " + t.seleccion);
        lblItems.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblCosto = new JLabel("A cobrar: " + String.format(Locale.US, "%.2f", t.costo) + " bs");
        lblCosto.setFont(new Font("Arial", Font.BOLD, 14));
        lblCosto.setForeground(new Color(200, 50, 50));

        infoPanel.add(lblDiaTurno);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblItems);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblCosto);
        row.add(infoPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 25));
        btnPanel.setOpaque(false);

        RoundedButton btnAnular = new RoundedButton("Anular", new Color(180, 50, 50), Color.WHITE, 15);
        btnAnular.setPreferredSize(new Dimension(100, 35));

        btnAnular.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres anular y devolver el turno?",
                    "Anular", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                GestorTurnos.eliminarTurno(usuarioActual, t.id);
                JOptionPane.showMessageDialog(this, "Turno anulado.");
                cargarTurnos();
            }
        });

        btnPanel.add(btnAnular);

        row.add(btnPanel, BorderLayout.EAST);

        return row;
    }
}
