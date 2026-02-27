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
import com.comedorucv.utils.GestorSaldosYMenu;
import java.util.Locale;

import javax.swing.OverlayLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.comedorucv.modelos.Usuario;

public class VentanaMonedero extends JFrame {
    private double saldo = 0.0;
    private Usuario usuarioActual;

    public VentanaMonedero(Usuario u) {
        this.usuarioActual = u;
        setTitle("Gestionar monedero");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(8, 10, 12, 10));

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

        saldo = GestorSaldosYMenu.leerSaldo(usuarioActual.getCedula());

        JLabel lblMonedero = new JLabel(String.format(Locale.US, "Monedero: %.2f", saldo));
        lblMonedero.setName("lblMonedero");
        lblMonedero.setForeground(Color.WHITE);
        lblMonedero.setFont(new Font("Arial", Font.BOLD, 14));

        topBar.add(new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30)) {
            {
                setOpaque(false);
                JLabel titleLabel = new JLabel("COMEDOR UCV", SwingConstants.CENTER);
                titleLabel.setForeground(Color.WHITE);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                add(titleLabel);
            }
        });
        // Left part will be in sidesPanel

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

        JPanel monederoHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        monederoHolder.setOpaque(false);
        monederoHolder.add(lblMonedero);
        navContainer.add(monederoHolder, BorderLayout.CENTER);

        JPanel sidesPanel = new JPanel(new BorderLayout());
        sidesPanel.setOpaque(false);
        sidesPanel.setBorder(new EmptyBorder(0, 40, 0, 40));
        sidesPanel.add(left, BorderLayout.WEST);
        sidesPanel.add(navContainer, BorderLayout.EAST);

        topBar.add(sidesPanel);

        add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        JLabel titulo = new JLabel("Gestionar monedero", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(new Color(0, 51, 78));
        titulo.setBorder(new EmptyBorder(12, 0, 18, 0));
        center.add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(200, 220, 255));
        contenido.setLayout(new GridBagLayout());
        contenido.setBorder(new RoundedBorder(24));
        contenido.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        JPanel pagoInfo = new JPanel();
        pagoInfo.setBackground(Color.WHITE);
        pagoInfo.setLayout(new GridBagLayout());
        GridBagConstraints pgb = new GridBagConstraints();
        pgb.gridx = 0;
        pgb.gridy = 0;
        pgb.insets = new Insets(2, 2, 2, 2);
        JLabel li1 = new JLabel("C.I: 31866942");
        li1.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel li2 = new JLabel("Teléfono: 0412-5303918");
        li2.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel li3 = new JLabel("Bancamiga (Pago móvil)");
        li3.setFont(new Font("Arial", Font.BOLD, 14));
        pagoInfo.add(li1, pgb);
        pgb.gridy++;
        pagoInfo.add(li2, pgb);
        pgb.gridy++;
        pagoInfo.add(li3, pgb);
        contenido.add(pagoInfo, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        contenido.add(new JLabel("Monto a recargar (bs):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        Integer[] opciones = new Integer[] { 5, 10, 50, 100, 200, 500, 1000 };
        JComboBox<Integer> comboRecarga = new JComboBox<>(opciones);
        comboRecarga.setPreferredSize(new Dimension(120, 26));
        comboRecarga.setBorder(new RoundedBorder(12));
        contenido.add(comboRecarga, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        RoundedButton btnRecargar = new RoundedButton("Recargar", new Color(0, 51, 78), Color.WHITE, 22);
        contenido.add(btnRecargar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        RoundedButton btnHistorial = new RoundedButton("Ver historial", new Color(80, 80, 80), Color.WHITE, 18);
        contenido.add(btnHistorial, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        RoundedButton btnBorrarHist = new RoundedButton("Borrar historial", new Color(180, 24, 24), Color.WHITE, 18);
        contenido.add(btnBorrarHist, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        JLabel notaRef = new JLabel("Nota: Debe reportar su referencia de 6 dígitos");
        notaRef.setFont(new Font("Arial", Font.PLAIN, 12));
        notaRef.setForeground(Color.RED);
        contenido.add(notaRef, gbc);

        center.add(contenido, BorderLayout.CENTER);

        btnRecargar.addActionListener(e -> {
            Integer seleccionado = (Integer) comboRecarga.getSelectedItem();
            if (seleccionado == null)
                return;
            String ref = JOptionPane.showInputDialog(this, "Ingrese el número de referencia (6 dígitos):", "Recargar",
                    JOptionPane.PLAIN_MESSAGE);
            if (ref != null && ref.trim().matches("\\d{6}")) {
                saldo += seleccionado.doubleValue();
                lblMonedero.setText(String.format(Locale.US, "Monedero: %.2f", saldo));
                GestorSaldosYMenu.guardarSaldo(usuarioActual.getCedula(), saldo, "Recarga vía VentanaMonedero");
                JOptionPane.showMessageDialog(this, "Recarga registrada con éxito. Referencia: " + ref);
            } else if (ref != null) {
                JOptionPane.showMessageDialog(this, "La referencia debe tener exactamente 6 dígitos numéricos.");
            }
        });
        btnHistorial.addActionListener(e -> {
            String hist = GestorSaldosYMenu.consultarHistorial(usuarioActual.getCedula());
            if (hist.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay historial disponible.");
            } else {
                JTextArea area = new JTextArea(hist);
                area.setEditable(false);
                area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JOptionPane.showMessageDialog(this, new JScrollPane(area), "Historial de Recargas",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        btnBorrarHist.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "¿Seguro que desea borrar el historial?", "Confirmar",
                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                GestorSaldosYMenu.borrarHistorial(usuarioActual.getCedula());
                JOptionPane.showMessageDialog(this, "Historial borrado correctamente.");
            }
        });

        add(center, BorderLayout.CENTER);
        JPanel blueStrip = new JPanel();
        blueStrip.setBackground(new Color(0, 51, 78));
        blueStrip.setPreferredSize(new Dimension(0, 12));
        add(blueStrip, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    // Local I/O methods removed in favor of GestorSaldosYMenu
}
