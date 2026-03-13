package com.comedorucv.vistas;

import com.comedorucv.modelos.Usuario;
import com.comedorucv.utils.DataPathResolver;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VentanaTarifas extends JFrame {
    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color FIELD_BG = new Color(242, 242, 242);
    private final Color ACCENT_COLOR = new Color(0, 119, 181);

    private JTextField txtEstudiante, txtProfesor, txtEmpleado, txtBecario;
    private Usuario usuarioActual;

    public VentanaTarifas(Usuario u) {
        this.usuarioActual = u;
        setTitle("Configuración de Tarifas - Comedor UCV");
        setSize(1000, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        initUI();
        loadTarifas();
    }

    private void initUI() {
        // --- Header (Barra Azul) ---
        JPanel headerPanel = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        headerPanel.setLayout(new OverlayLayout(headerPanel));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(1000, 85));
        headerPanel.setMinimumSize(new Dimension(1000, 85));
        headerPanel.setMaximumSize(new Dimension(2000, 85));

        // Capa 1: Partes laterales (Logo e Inicio / Reloj y Cerrar Sesión)
        JPanel sidesPanel = new JPanel(new BorderLayout());
        sidesPanel.setOpaque(false);
        sidesPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Logo y "Inicio"
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftNav.setOpaque(false);
        leftNav.add(new LogoPlaceholder());

        JLabel lblInicio = new JLabel("Inicio");
        lblInicio.setForeground(Color.WHITE);
        lblInicio.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblInicio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblInicio.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new VentanaPrincipal(usuarioActual).setVisible(true);
                dispose();
            }
        });
        leftNav.add(Box.createHorizontalStrut(20));
        leftNav.add(lblInicio);
        sidesPanel.add(leftNav, BorderLayout.WEST);

        // Derecha: Cerrar sesión y Reloj
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 15));
        logoutPanel.setOpaque(false);
        JLabel lblLogout = new JLabel("Cerrar sesión");
        lblLogout.setForeground(Color.WHITE);
        lblLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (JOptionPane.showConfirmDialog(null, "¿Deseas cerrar sesión?", "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    dispose();
                }
            }
        });
        logoutPanel.add(lblLogout);
        rightPanel.add(logoutPanel);

        RelojLabel clock = new RelojLabel();
        JPanel clockPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        clockPanel.setOpaque(false);
        clockPanel.add(clock);
        rightPanel.add(clockPanel);
        sidesPanel.add(rightPanel, BorderLayout.EAST);

        // Capa 2: Título central "COMEDOR UCV"
        JPanel titleCenterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        titleCenterPanel.setOpaque(false);
        JLabel mainTitleLabel = new JLabel("COMEDOR UCV");
        mainTitleLabel.setForeground(Color.WHITE);
        mainTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleCenterPanel.add(mainTitleLabel);

        // Alinear componentes para OverlayLayout
        titleCenterPanel.setAlignmentX(0.5f);
        titleCenterPanel.setAlignmentY(0.5f);
        sidesPanel.setAlignmentX(0.5f);
        sidesPanel.setAlignmentY(0.5f);

        headerPanel.add(titleCenterPanel);
        headerPanel.add(sidesPanel);

        // --- Título debajo de la barra ---
        JLabel viewTitle = new JLabel("TARIFAS Y DESCUENTOS", SwingConstants.CENTER);
        viewTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        viewTitle.setForeground(PRIMARY_COLOR);
        viewTitle.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        viewTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel agrupador superior
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(BACKGROUND_COLOR);
        topContainer.add(headerPanel);
        topContainer.add(viewTitle);

        JLabel info = new JLabel(
                "<html><center>Configure el porcentaje del <b>Costo Cubierto de la Bandeja (CCB)</b> que pagará cada usuario.<br>"
                        +
                        "<small>(Ejemplo: 30 para cobrar el 30% del costo real)</small></center></html>",
                SwingConstants.CENTER);
        info.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        info.setForeground(Color.GRAY);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        topContainer.add(info);

        add(topContainer, BorderLayout.NORTH);

        // --- Main Content ---
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 50, 20, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        
        txtEstudiante = createTarifaField("Estudiante (%):", 1, content, gbc);
        txtBecario = createTarifaField("Estudiante Becario (%):", 2, content, gbc);
        txtProfesor = createTarifaField("Profesor (%):", 3, content, gbc);
        txtEmpleado = createTarifaField("Empleado (%):", 4, content, gbc);


        // Añadir una nota de ayuda con los rangos sugeridos por CCB
        JLabel rangesInfo = new JLabel("<html><font color='#888888'>Rangos establecidos:<br>" +
                "• Estudiantes: 20% - 30%<br>" +
                "• Becarios: 5% - 10%<br>" +
                "• Profesores: 70% - 90%<br>" +
                "• Empleados: 90% - 110%<br>" +
                "• Exonerados: 0% permanentemente</font></html>");
        rangesInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 0, 0);
        content.add(rangesInfo, gbc);

        RoundedButton btnSave = new RoundedButton("ESTABLECER TARIFAS", ACCENT_COLOR, Color.WHITE, 30);
        btnSave.setPreferredSize(new Dimension(200, 50));
        btnSave.addActionListener(e -> saveTarifas());
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 0, 0);
        content.add(btnSave, gbc);

        add(content, BorderLayout.CENTER);
    }

    private JTextField createTarifaField(String label, int gridy, JPanel container, GridBagConstraints gbc) {
        JPanel p = new JPanel(new BorderLayout(10, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(PRIMARY_COLOR);
        l.setPreferredSize(new Dimension(180, 20));
        p.add(l, BorderLayout.WEST);

        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tf.setBackground(FIELD_BG);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        p.add(tf, BorderLayout.CENTER);

        gbc.gridy = gridy;
        container.add(p, gbc);
        return tf;
    }

    private void loadTarifas() {
        try {
            Path path = DataPathResolver.resolve("tarifas.txt");
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                for (String line : lines) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        String type = parts[0].trim();
                        String value = parts[1].trim();

                        // Formatear para quitar el .0 si es un entero
                        if (value.endsWith(".0")) {
                            value = value.substring(0, value.length() - 2);
                        }

                        if (type.equalsIgnoreCase("ESTUDIANTE"))
                            txtEstudiante.setText(value);
                        else if (type.equalsIgnoreCase("PROFESOR"))
                            txtProfesor.setText(value);
                        else if (type.equalsIgnoreCase("EMPLEADO"))
                            txtEmpleado.setText(value);
                        else if (type.equalsIgnoreCase("ESTUDIANTE BECARIO"))
                            txtBecario.setText(value);
                        else if (type.equalsIgnoreCase("ESTUDIANTE EXONERADO")) {
                            // No hacer nada, se mantiene en 0 internamente
                        }
                    }
                }
            } else {
                txtEstudiante.setText("20");
                txtBecario.setText("5");
                txtProfesor.setText("70");
                txtEmpleado.setText("90");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTarifas() {
        if (txtEstudiante.getText().trim().isEmpty() ||
                txtBecario.getText().trim().isEmpty() ||
                txtProfesor.getText().trim().isEmpty() ||
                txtEmpleado.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos de porcentaje son obligatorios.",
                    "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double pEst = Double.parseDouble(txtEstudiante.getText().replace("%", "").trim());
            double pBec = Double.parseDouble(txtBecario.getText().replace("%", "").trim());
            double pProf = Double.parseDouble(txtProfesor.getText().replace("%", "").trim());
            double pEmp = Double.parseDouble(txtEmpleado.getText().replace("%", "").trim());
            double pExo = 0; // Siempre 0

            StringBuilder errores = new StringBuilder();
            if (pEst < 20 || pEst > 30) errores.append("- Estudiantes: entre 20% y 30%.\n");
            if (pBec < 0 || pBec > 10) errores.append("- Becarios: entre 0% y 10%.\n");
            if (pExo != 0) errores.append("- Exonerados: debe ser 0%.\n");
            if (pProf < 70 || pProf > 90) errores.append("- Profesores: entre 70% y 90%.\n");
            if (pEmp < 90 || pEmp > 110) errores.append("- Empleados: entre 90% y 110%.\n");

            if (errores.length() > 0) {
                JOptionPane.showMessageDialog(this,
                        "Porcentajes fuera de rango:\n\n" + errores.toString(),
                        "Rango de Tarifa Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<String> lines = new ArrayList<>();
            lines.add("ESTUDIANTE:" + (pEst == (int) pEst ? (int) pEst : pEst));
            lines.add("PROFESOR:" + (pProf == (int) pProf ? (int) pProf : pProf));
            lines.add("EMPLEADO:" + (pEmp == (int) pEmp ? (int) pEmp : pEmp));
            lines.add("ESTUDIANTE EXONERADO:" + (pExo == (int) pExo ? (int) pExo : pExo));
            lines.add("ESTUDIANTE BECARIO:" + (pBec == (int) pBec ? (int) pBec : pBec));

            Files.write(DataPathResolver.resolve("tarifas.txt"), lines);
            JOptionPane.showMessageDialog(this, "Tarifas actualizadas correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese solo números válidos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar las tarifas.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
