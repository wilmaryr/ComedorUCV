package com.comedorucv.vistas;

import com.comedorucv.utils.DataPathResolver;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class VentanaHorarios extends JFrame {

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private JToggleButton btnEstado;
    private JLabel lblEstado;
    private JTextField txtIniDes, txtFinDes, txtIniAlm, txtFinAlm;

    public VentanaHorarios() {
        setTitle("Configuración de Horarios - Comedor UCV");
        setSize(500, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        initUI();
        cargarEstado();
    }

    private void initUI() {
        // Encabezado
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        JLabel title = new JLabel("CONFIGURACIÓN DE SERVICIO");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Panel Central
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Sección Estado Maestro
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel info = new JLabel("Estado actual del comedor (Acceso Facial):", SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        center.add(info, gbc);

        lblEstado = new JLabel("ABIERTO", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblEstado.setForeground(new Color(34, 139, 34));
        gbc.gridy = 1;
        center.add(lblEstado, gbc);

        btnEstado = new JToggleButton("ABRIR / CERRAR");
        btnEstado.setPreferredSize(new Dimension(200, 45));
        btnEstado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEstado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEstado.addActionListener(e -> togglerEstado());
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        center.add(btnEstado, gbc);

        // Línea divisoria
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(25, 0, 25, 0);
        JSeparator sep = new JSeparator();
        center.add(sep, gbc);

        // Sección Horarios
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridwidth = 1;

        // Desayuno
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblDes = new JLabel("Horario Desayuno:");
        lblDes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        center.add(lblDes, gbc);

        JPanel pnlDes = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlDes.setOpaque(false);
        txtIniDes = createTimeField("08:00");
        txtFinDes = createTimeField("08:59");
        pnlDes.add(txtIniDes);
        pnlDes.add(new JLabel("-"));
        pnlDes.add(txtFinDes);
        gbc.gridx = 1;
        center.add(pnlDes, gbc);

        // Almuerzo
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblAlm = new JLabel("Horario Almuerzo:");
        lblAlm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        center.add(lblAlm, gbc);

        JPanel pnlAlm = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlAlm.setOpaque(false);
        txtIniAlm = createTimeField("12:00");
        txtFinAlm = createTimeField("16:40");
        pnlAlm.add(txtIniAlm);
        pnlAlm.add(new JLabel("-"));
        pnlAlm.add(txtFinAlm);
        gbc.gridx = 1;
        center.add(pnlAlm, gbc);

        // Nota
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JLabel hint = new JLabel("(Formato 24h, ej: 16:40)", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(Color.GRAY);
        center.add(hint, gbc);

        add(center, BorderLayout.CENTER);

        // Botón Guardar
        JButton btnGuardar = new JButton("GUARDAR TODA LA CONFIGURACIÓN");
        btnGuardar.setPreferredSize(new Dimension(getWidth(), 55));
        btnGuardar.setBackground(PRIMARY_COLOR);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> guardarEstado());
        add(btnGuardar, BorderLayout.SOUTH);
    }

    private JTextField createTimeField(String text) {
        JTextField tf = new JTextField(text, 5);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return tf;
    }

    private void cargarEstado() {
        try {
            Path path = DataPathResolver.resolve("estado_comedor.txt");
            if (Files.exists(path)) {
                String estado = Files.readString(path).trim();
                if (estado.equalsIgnoreCase("CERRADO")) {
                    lblEstado.setText("CERRADO");
                    lblEstado.setForeground(new Color(200, 0, 0));
                    btnEstado.setSelected(true);
                } else {
                    lblEstado.setText("ABIERTO");
                    lblEstado.setForeground(new Color(34, 139, 34));
                    btnEstado.setSelected(false);
                }
            }

            Path pathHorarios = DataPathResolver.resolve("horarios.txt");
            if (Files.exists(pathHorarios)) {
                String[] datos = Files.readString(pathHorarios).split(",");
                if (datos.length >= 4) {
                    txtIniDes.setText(datos[0].trim());
                    txtFinDes.setText(datos[1].trim());
                    txtIniAlm.setText(datos[2].trim());
                    txtFinAlm.setText(datos[3].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void togglerEstado() {
        if (btnEstado.isSelected()) {
            lblEstado.setText("CERRADO");
            lblEstado.setForeground(new Color(200, 0, 0));
        } else {
            lblEstado.setText("ABIERTO");
            lblEstado.setForeground(new Color(34, 139, 34));
        }
    }

    private void guardarEstado() {
        try {
            // Validar formato básico HH:mm
            String regex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
            if (!txtIniDes.getText().matches(regex) || !txtFinDes.getText().matches(regex) ||
                !txtIniAlm.getText().matches(regex) || !txtFinAlm.getText().matches(regex)) {
                JOptionPane.showMessageDialog(this, "Por favor use el formato de hora HH:mm (ej: 08:30 o 14:00)");
                return;
            }

            Path path = DataPathResolver.resolve("estado_comedor.txt");
            String estado = btnEstado.isSelected() ? "CERRADO" : "ABIERTO";
            Files.writeString(path, estado);

            Path pathHorarios = DataPathResolver.resolve("horarios.txt");
            String horarios = String.join(",", 
                txtIniDes.getText().trim(),
                txtFinDes.getText().trim(),
                txtIniAlm.getText().trim(),
                txtFinAlm.getText().trim()
            );
            Files.writeString(pathHorarios, horarios);

            JOptionPane.showMessageDialog(this, "¡Configuración guardada correctamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar la configuración.");
        }
    }
}
