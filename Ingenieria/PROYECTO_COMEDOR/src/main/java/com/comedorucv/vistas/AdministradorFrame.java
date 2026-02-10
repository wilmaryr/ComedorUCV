package com.comedorucv.vistas;

import com.comedorucv.modelos.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class AdministradorFrame extends JFrame {

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color FIELD_BG = new Color(240, 240, 240);

    private Usuario usuario;

    public AdministradorFrame(Usuario u) {
        this.usuario = u;
        setTitle("Comedor UCV - Perfil Administrador");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 40));

        JPanel logoContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        logoContainer.setOpaque(false);
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/imagenes/Logo_Universidad_Central_de_Venezuela.png"));
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
        }
        logoContainer.add(logoLabel);
        headerPanel.add(logoContainer, BorderLayout.WEST);

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
        headerPanel.add(lblLogout, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel mainWrapper = new JPanel(new GridBagLayout());
        mainWrapper.setBackground(BACKGROUND_COLOR);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BACKGROUND_COLOR);
        sidebar.setPreferredSize(new Dimension(300, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 150;
                int x = (getWidth() - size) / 2;
                int y = 0;

                g2.setColor(PRIMARY_COLOR);
                g2.fill(new Ellipse2D.Double(x, y, size, size));

                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Double(x + 50, y + 30, 50, 50));
                g2.fillArc(x + 25, y + 85, 100, 100, 0, 180);

                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(200, 160));
        avatarPanel.setOpaque(false);
        sidebar.add(avatarPanel);

        JLabel lblRole = new JLabel("Administrador");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblRole.setForeground(PRIMARY_COLOR);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(lblRole);

        JLabel lblUserType = new JLabel("Gestión");
        lblUserType.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblUserType.setForeground(Color.GRAY);
        lblUserType.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblUserType);

        sidebar.add(Box.createVerticalStrut(40));

        RoundedPanel optionsPanel = new RoundedPanel(20, FIELD_BG);
        optionsPanel.setLayout(new GridLayout(0, 1, 0, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        optionsPanel.setMaximumSize(new Dimension(250, 180));

        optionsPanel.add(createOptionButton("Consultar insumos"));
        JButton btnGestionarMenu = createOptionButton("Gestionar menú");
        btnGestionarMenu.addActionListener(e -> {
            new VentanaPrincipal(usuario).setVisible(true);
            dispose();
        });
        optionsPanel.add(btnGestionarMenu);
        optionsPanel.add(createOptionButton("Generar reporte"));

        sidebar.add(optionsPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainWrapper.add(sidebar, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(60, 0, 60, 0);
        JPanel vLine = new JPanel();
        vLine.setBackground(Color.BLACK);
        vLine.setPreferredSize(new Dimension(2, 1));
        mainWrapper.add(vLine, gbc);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.weightx = 1.0;
        fgbc.insets = new Insets(10, 0, 10, 0);

        JLabel lblFormTitle = new JLabel("Datos del administrador");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblFormTitle.setForeground(PRIMARY_COLOR);
        fgbc.gridy = 1;
        fgbc.insets = new Insets(20, 0, 30, 0);
        formPanel.add(lblFormTitle, fgbc);

        JPanel row1 = new JPanel(new GridLayout(1, 2, 30, 0));
        row1.setOpaque(false);
        row1.add(createFieldItem("Nombres y apellidos", usuario.getNombre()));
        row1.add(createFieldItem("Puesto", "Administrador"));
        fgbc.gridy = 2;
        fgbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(row1, fgbc);

        fgbc.gridy = 3;
        formPanel.add(createFieldItem("Correo electrónico", usuario.getCorreo()), fgbc);

        fgbc.gridy = 4;
        formPanel.add(createFieldItem("Cedula de identidad", usuario.getCedula()), fgbc);

        gbc.gridx = 2;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainWrapper.add(formPanel, gbc);

        add(mainWrapper, BorderLayout.CENTER);
    }

    private JButton createOptionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(PRIMARY_COLOR);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, PRIMARY_COLOR));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(new Color(0, 100, 150));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(PRIMARY_COLOR);
            }
        });

        return btn;
    }

    private JPanel createFieldItem(String label, String value) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(PRIMARY_COLOR);
        p.add(lbl, BorderLayout.NORTH);

        JTextField field = new JTextField(value);
        field.setEditable(false);
        field.setBackground(FIELD_BG);
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        RoundedPanel fieldPanel = new RoundedPanel(20, FIELD_BG);
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.add(field);
        p.add(fieldPanel, BorderLayout.CENTER);

        return p;
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
