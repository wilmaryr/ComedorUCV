package com.comedorucv.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.comedorucv.modelos.Plato;
import com.comedorucv.modelos.Usuario;
import com.comedorucv.utils.GestorSaldosYMenu;
import com.comedorucv.utils.GestorTurnos;

public class DialogoSeleccionMenu extends JDialog {

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;

    private Usuario usuario;
    private Plato plato;
    private String dia;
    private String turno;

    private List<JCheckBox> checks;
    private List<Double> precios;
    private JLabel lblTotal;

    private JFrame parentFrame;

    public DialogoSeleccionMenu(JFrame parent, Usuario usuario, Plato plato, String dia, String turno) {
        super(parent, "Seleccionar Ítems del Menú", true);
        this.parentFrame = parent;
        this.usuario = usuario;
        this.plato = plato;
        this.dia = dia;
        this.turno = turno;

        setSize(500, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        checks = new ArrayList<>();
        precios = new ArrayList<>();

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Personaliza tu menú de " + dia);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel info = new JLabel("Selecciona qué deseas comprar de este turno:");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        contentPanel.add(info);
        contentPanel.add(Box.createVerticalStrut(20));

        JPanel itemsPanel = new JPanel(new GridBagLayout());
        itemsPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        addItemRow(plato.getNombre(), itemsPanel, gbc);
        addItemRow(plato.getContorno1(), itemsPanel, gbc);
        addItemRow(plato.getContorno2(), itemsPanel, gbc);
        addItemRow(plato.getBebida(), itemsPanel, gbc);
        addItemRow(plato.getPostre(), itemsPanel, gbc);

        contentPanel.add(itemsPanel);
        contentPanel.add(Box.createVerticalStrut(25));

        lblTotal = new JLabel("Total a pagar: 0.00 bs");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(PRIMARY_COLOR);
        contentPanel.add(lblTotal);

        add(contentPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        RoundedButton btnConfirmar = new RoundedButton("Confirmar reserva...", new Color(40, 167, 69), Color.WHITE,
                20);
        RoundedButton btnCancel = new RoundedButton("Cancelar", Color.GRAY, Color.WHITE, 20);

        Dimension btnDim = new Dimension(180, 45);
        btnConfirmar.setPreferredSize(btnDim);
        btnCancel.setPreferredSize(new Dimension(120, 45));

        btnConfirmar.addActionListener(e -> procederAlPago());
        btnCancel.addActionListener(e -> dispose());

        bottomPanel.add(btnCancel);
        bottomPanel.add(btnConfirmar);

        add(bottomPanel, BorderLayout.SOUTH);
        recalcularTotal();
    }

    private void addItemRow(String item, JPanel parentPanel, GridBagConstraints gbc) {
        if (item != null && !item.trim().isEmpty()) {
            double p = GestorSaldosYMenu.consultarPrecioItem(item);
            JCheckBox cb = new JCheckBox(item + " (" + String.format(java.util.Locale.US, "%.2f", p) + " bs)", true);
            cb.setBackground(BACKGROUND_COLOR);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            cb.addActionListener(e -> recalcularTotal());

            checks.add(cb);
            precios.add(p);
            parentPanel.add(cb, gbc);
            gbc.gridy++;
        }
    }

    private void recalcularTotal() {
        double ccb = 0.0; // Costo Cubierto de la Bandeja (Subtotal real)
        for (int i = 0; i < checks.size(); i++) {
            if (checks.get(i).isSelected()) {
                ccb += precios.get(i);
            }
        }

        double tarifaPorcentaje = obtenerTarifaPorRol(usuario.getRol());
        double totalAPagar = ccb * (tarifaPorcentaje / 100.0);

        String detalle;
        if (tarifaPorcentaje < 100) {
            detalle = String.format(java.util.Locale.US, "(%d%% del CCB: %.2f)", (int) tarifaPorcentaje, ccb);
        } else if (tarifaPorcentaje > 100) {
            detalle = String.format(java.util.Locale.US, "(%d%% sobre el CCB: %.2f)", (int) tarifaPorcentaje, ccb);
        } else {
            detalle = String.format(java.util.Locale.US, "(100%% del CCB: %.2f)", ccb);
        }

        lblTotal.setText(String.format(java.util.Locale.US,
                "<html>Tarifa: <b>%.2f bs</b> <small>%s</small></html>",
                totalAPagar, detalle));
    }

    private double obtenerTarifaPorRol(String rol) {
        try {
            java.nio.file.Path path = com.comedorucv.utils.DataPathResolver.resolve("tarifas.txt");
            if (java.nio.file.Files.exists(path)) {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(path);
                for (String line : lines) {
                    String[] parts = line.split(":");
                    if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(rol)) {
                        return Double.parseDouble(parts[1].replace("%", "").trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 100.0; // Por defecto paga el 100% si no hay configuración
    }

    private void procederAlPago() {
        boolean selectedSomething = false;
        StringBuilder seleccionados = new StringBuilder();
        double ccb = 0.0;
        for (int i = 0; i < checks.size(); i++) {
            if (checks.get(i).isSelected()) {
                ccb += precios.get(i);
                selectedSomething = true;
                String text = checks.get(i).getText();
                seleccionados.append(text.substring(0, text.lastIndexOf("(")).trim()).append(", ");
            }
        }

        double tarifaPorcentaje = obtenerTarifaPorRol(usuario.getRol());
        double totalAPagar = ccb * (tarifaPorcentaje / 100.0);

        if (!selectedSomething) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un ítem para solicitar el menú.");
            return;
        }

        // Distribución de ingresos según CCB
        double gananciaConcesionario = totalAPagar * 0.30; // 30% para el concesionario
        double ingresosMejora = totalAPagar - gananciaConcesionario; // 70% para mejora del servicio

        com.comedorucv.utils.GestorSaldosYMenu.registrarIngresoCCB(usuario.getCedula(), totalAPagar,
                gananciaConcesionario, ingresosMejora, "Reserva de menú - " + dia + " (" + turno + ")");

        java.util.List<com.comedorucv.utils.GestorTurnos.TurnoPendiente> misTurnos = com.comedorucv.utils.GestorTurnos
                .obtenerTurnosPendientes(usuario);
        for (com.comedorucv.utils.GestorTurnos.TurnoPendiente t : misTurnos) {
            if (t.dia.equalsIgnoreCase(dia) && t.turno.equalsIgnoreCase(turno)) {
                JOptionPane.showMessageDialog(this, "No puedes pedir dos " + turno.toLowerCase() + "s para el día "
                        + dia + ". Ya tienes uno solicitado.", "Reserva duplicada", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (GestorSaldosYMenu.decrementarDisponibilidad(dia, turno)) {
            String cleanSeleccion = seleccionados.toString();
            if (cleanSeleccion.endsWith(", "))
                cleanSeleccion = cleanSeleccion.substring(0, cleanSeleccion.length() - 2);

            GestorTurnos.registrarTurnoPendiente(usuario, dia, turno, cleanSeleccion, totalAPagar);

            JOptionPane.showMessageDialog(this, "¡Reserva apartada con éxito!");

            dispose();
            parentFrame.dispose();
            new VentanaMisTurnos(usuario).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Lo sentimos, ya no hay disponibilidad para el menú general.");
        }
    }
}
