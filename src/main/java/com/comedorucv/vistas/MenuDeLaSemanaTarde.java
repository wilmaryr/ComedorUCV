package com.comedorucv.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;

import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.nio.file.Files;
import java.nio.file.Path;
import com.comedorucv.utils.DataPathResolver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.comedorucv.modelos.Plato;
import com.comedorucv.modelos.Usuario;
import com.comedorucv.utils.GestorSaldosYMenu;

public class MenuDeLaSemanaTarde extends JFrame {

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color SIDEBAR_BG = new Color(248, 249, 250);
    private final Color CARD_BG = new Color(245, 245, 245);

    private final Color BUTTON_RED = new Color(255, 0, 0);
    private final Color BUTTON_BLUE = new Color(0, 51, 90);
    private final Color BUTTON_GRAY = new Color(128, 128, 128);

    private Usuario usuario;

    public MenuDeLaSemanaTarde(Usuario u) {
        this.usuario = u;
        setTitle("Comedor UCV - Menú de la semana (Almuerzo)");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel() {
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        headerPanel.setLayout(new OverlayLayout(headerPanel));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 85));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JPanel logoContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        logoContainer.setOpaque(false);
        LogoPlaceholder logoLabel = new LogoPlaceholder();
        logoContainer.add(logoLabel);

        JLabel titleLabel = new JLabel("COMEDOR UCV", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        navPanel.setOpaque(false);

        JLabel lblLogout = new JLabel("Cerrar sesión");
        lblLogout.setForeground(Color.WHITE);
        lblLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        navPanel.add(lblLogout);

        navContainer.add(navPanel, BorderLayout.NORTH);

        RelojLabel dateLabel = new RelojLabel();
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 5));
        datePanel.setOpaque(false);
        datePanel.add(dateLabel);

        navContainer.add(datePanel, BorderLayout.CENTER);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);

        JPanel sidesPanel = new JPanel(new BorderLayout());
        sidesPanel.setOpaque(false);
        sidesPanel.add(logoContainer, BorderLayout.WEST);
        sidesPanel.add(navContainer, BorderLayout.EAST);

        headerPanel.add(titlePanel);
        headerPanel.add(sidesPanel);

        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BACKGROUND_COLOR);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        JLabel lblTitle = new JLabel("<html><div style='text-align: center;'>Menú de la<br>semana</div></html>");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTitle);
        sidebar.add(Box.createVerticalStrut(60));

        JLabel lblBreakfast = new JLabel(
                "<html><span style='font-size:16px'>Desayuno</span><br><span style='font-size:12px'>(7am/9am)</span></html>");
        lblBreakfast.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblBreakfast.setForeground(Color.GRAY);
        lblBreakfast.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel breakfastPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        breakfastPanel.setOpaque(false);
        breakfastPanel.setMaximumSize(new Dimension(220, 50));
        breakfastPanel.add(lblBreakfast);
        breakfastPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        breakfastPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MenuDeLaSemana(usuario).setVisible(true);
                dispose();
            }
        });
        sidebar.add(breakfastPanel);
        sidebar.add(Box.createVerticalStrut(30));

        JPanel lunchPanel = new JPanel(new BorderLayout());
        lunchPanel.setOpaque(false);
        lunchPanel.setMaximumSize(new Dimension(220, 50));

        JPanel activeBar = new JPanel();
        activeBar.setBackground(PRIMARY_COLOR);
        activeBar.setPreferredSize(new Dimension(4, 50));
        lunchPanel.add(activeBar, BorderLayout.WEST);

        JLabel lblLunch = new JLabel(
                "<html><span style='font-size:16px'>Almuerzo</span><br><span style='font-size:12px'>(12pm/2pm)</span></html>");
        lblLunch.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLunch.setForeground(PRIMARY_COLOR);
        lblLunch.setBorder(new EmptyBorder(0, 15, 0, 0));
        lunchPanel.add(lblLunch, BorderLayout.CENTER);

        sidebar.add(lunchPanel);
        sidebar.add(Box.createVerticalStrut(60));

        JLabel lblBack = new JLabel("← Volver al inicio");
        lblBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBack.setForeground(PRIMARY_COLOR);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ComensalFrame(usuario).setVisible(true);
                dispose();
            }
        });
        sidebar.add(lblBack);

        contentPane.add(sidebar, BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(BACKGROUND_COLOR);
        mainArea.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 60));

        JPanel sectionTitlePanel = new JPanel(new BorderLayout());
        sectionTitlePanel.setBackground(BACKGROUND_COLOR);
        sectionTitlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));

        JLabel lblSectionTitle = new JLabel("Almuerzo (12pm/2pm)");
        lblSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSectionTitle.setForeground(PRIMARY_COLOR);
        lblSectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        sectionTitlePanel.add(lblSectionTitle, BorderLayout.SOUTH);

        mainArea.add(sectionTitlePanel, BorderLayout.NORTH);

        JPanel daysListPanel = new JPanel();
        daysListPanel.setLayout(new BoxLayout(daysListPanel, BoxLayout.Y_AXIS));
        daysListPanel.setBackground(BACKGROUND_COLOR);
        daysListPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        Map<String, Plato> menuAlmuerzo = loadMenuForTurno("ALMUERZO");
        String[] dias = { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" };

        for (String d : dias) {
            Plato p = menuAlmuerzo.get(d);
            if (p != null) {
                String desc = String.format("%s - %s - %s - %s - %s",
                        p.getNombre(), p.getContorno1(), p.getContorno2(), p.getBebida(), p.getPostre());
                String estado = p.getDisponibilidad() > 0 ? "Disponible" : "Agotado";
                Color btnCol = p.getDisponibilidad() > 0 ? BUTTON_BLUE : BUTTON_GRAY;
                double tarifaPorcentaje = obtenerTarifaPorRol(usuario.getRol());
                double ccb = GestorSaldosYMenu.consultarPrecio(d, "ALMUERZO"); // CCB = Costo Bandeja
                double tarifaFinal = ccb * (tarifaPorcentaje / 100.0);

                String tarifaFormateada;
                if (tarifaPorcentaje < 100) {
                    tarifaFormateada = String.format(java.util.Locale.US, "%.2f bs (%d%% del CCB)", tarifaFinal,
                            (int) tarifaPorcentaje);
                } else if (tarifaPorcentaje > 100) {
                    tarifaFormateada = String.format(java.util.Locale.US, "%.2f bs (%d%% sobre CCB)", tarifaFinal,
                            (int) tarifaPorcentaje);
                } else {
                    tarifaFormateada = String.format(java.util.Locale.US, "%.2f bs", tarifaFinal);
                }

                daysListPanel.add(
                        createDayRow(d, desc + " | Tarifa: " + tarifaFormateada, estado, "Solicitar turno", btnCol));
            } else {
                daysListPanel.add(createDayRow(d, "No disponible", "No definido", "Solicitar turno", BUTTON_GRAY));
            }
            daysListPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(daysListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainArea.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(mainArea, BorderLayout.CENTER);
        add(contentPane, BorderLayout.CENTER);
    }

    private JPanel createDayRow(String day, String menuDesc, String status, String btnText, Color btnColor) {
        RoundedPanel row = new RoundedPanel(30, CARD_BG);
        row.setLayout(new BorderLayout());
        row.setPreferredSize(new Dimension(0, 80));
        row.setMaximumSize(new Dimension(2000, 80));
        row.setBorder(new EmptyBorder(10, 30, 10, 30));

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);
        JLabel lblDay = new JLabel(day);
        lblDay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDay.setForeground(Color.BLACK);
        JLabel lblDesc = new JLabel(menuDesc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.DARK_GRAY);
        leftPanel.add(lblDay);
        leftPanel.add(lblDesc);
        row.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        rightPanel.setOpaque(false);
        JLabel lblStatus = new JLabel(
                "<html><div style='text-align:right;'>Estado:<br><b>" + status + "</b></div></html>");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.BLACK);
        rightPanel.add(lblStatus);

        JButton actionBtn = new JButton(btnText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        actionBtn.setBackground(btnColor);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        actionBtn.setFocusPainted(false);
        actionBtn.setBorderPainted(false);
        actionBtn.setContentAreaFilled(false);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (btnColor == BUTTON_BLUE) {
            actionBtn.addActionListener(e -> {
                Map<String, Plato> menu = loadMenuForTurno("ALMUERZO");
                Plato p = menu.get(day);
                if (p != null) {
                    DialogoSeleccionMenu dialog = new DialogoSeleccionMenu(this, usuario, p, day, "ALMUERZO");
                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "El menú de este día no está disponible.");
                }
            });
        }

        rightPanel.add(actionBtn);
        row.add(rightPanel, BorderLayout.EAST);

        return row;
    }

    private Map<String, Plato> loadMenuForTurno(String turnoTarget) {
        Map<String, Plato> mapa = new HashMap<>();
        try {
            Path file = DataPathResolver.resolve("menu_save.txt");

            if (Files.exists(file)) {
                List<String> lines = Files.readAllLines(file);
                for (String l : lines) {
                    String[] parts = l.split("\t", -1);
                    if (parts.length >= 9 && parts[1].equals(turnoTarget)) {
                        Plato p = new Plato(parts[2], parts[3], parts[4], parts[5], parts[6],
                                Integer.parseInt(parts[7].isEmpty() ? "0" : parts[7]),
                                Integer.parseInt(parts[8].isEmpty() ? "0" : parts[8]));
                        mapa.put(parts[0], p);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mapa;
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
        return 100.0;
    }

    static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}
