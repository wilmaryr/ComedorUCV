package com.comedorucv.vistas;

import com.comedorucv.controladores.LoginController;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPass;
    private LoginController controller = new LoginController();

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color ACCENT_COLOR = new Color(0, 70, 100);

    public LoginFrame() {
        setTitle("Comedor UCV - Login");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JPanel logoContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        logoContainer.setOpaque(false);
        logoContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        LogoPlaceholder logoImageComp = new LogoPlaceholder();
        logoContainer.add(logoImageComp);

        JLabel titleLabel = new JLabel("COMEDOR UCV", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0));

        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        navPanel.setOpaque(false);

        String[] links = { "Inicio", "Ubicación", "Contacto" };
        for (String link : links) {
            JLabel l = new JLabel(link);
            l.setForeground(Color.WHITE);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            l.setCursor(new Cursor(Cursor.HAND_CURSOR));

            l.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    l.setForeground(new Color(180, 220, 255));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    l.setForeground(Color.WHITE);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (link.equals("Inicio")) {
                        new LoginFrame().setVisible(true);
                        dispose();
                    } else if (link.equals("Contacto")) {
                        JOptionPane.showMessageDialog(null, "Este es el número de contacto: 0212-6053954");
                    }
                }
            });

            l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            navPanel.add(l);
        }

        navContainer.add(navPanel, BorderLayout.NORTH);

        RelojLabel dateLabel = new RelojLabel();
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        datePanel.setOpaque(false);
        datePanel.add(dateLabel);

        navContainer.add(datePanel, BorderLayout.CENTER);

        JPanel sidesPanel = new JPanel(new BorderLayout());
        sidesPanel.setBackground(PRIMARY_COLOR);
        sidesPanel.add(logoContainer, BorderLayout.WEST);
        sidesPanel.add(navContainer, BorderLayout.EAST);
        sidesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        headerPanel.add(logoContainer, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(navContainer, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(
                        new GradientPaint(0, 0, new Color(220, 230, 235), 0, getHeight(), new Color(200, 210, 215)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        RoundedPanel card = new RoundedPanel(30, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("¡Bienvenido!");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        card.add(lblTitle, gbc);

        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.weightx = 1.0;

        txtCorreo = new JTextField();
        styleField(txtCorreo);
        gbc.gridy = 1;
        card.add(createFieldPanel("Correo Electrónico", txtCorreo), gbc);

        txtPass = new JPasswordField();
        styleField(txtPass);
        gbc.gridy = 2;
        card.add(createFieldPanel("Contraseña", txtPass, true), gbc);

        JPanel extrasPanel = new JPanel(new BorderLayout());
        extrasPanel.setOpaque(false);

        JLabel lblForgot = new JLabel("<html><u>¿Olvidaste la<br>contraseña?</u></html>");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblForgot.setForeground(PRIMARY_COLOR);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RecuperarContrasenaFrame().setVisible(true);
                dispose();
            }
        });
        extrasPanel.add(lblForgot, BorderLayout.EAST);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 20, 30, 20);
        card.add(extrasPanel, gbc);

        JButton btnLogin = new JButton("iniciar sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        btnLogin.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setFocusPainted(false);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 20, 20);
        card.add(btnLogin, gbc);

        JLabel lblRegister = new JLabel(
                "<html><div style='text-align: center;'>¿Aún no tienes cuenta? <span style='color: rgb(0, 51, 78); font-weight: bold;'>Regístrate</span></div></html>");
        lblRegister.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRegister.setHorizontalAlignment(SwingConstants.CENTER);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegistroFrame().setVisible(true);
                dispose();
            }
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 20, 10, 20);
        card.add(lblRegister, gbc);

        JLabel lblFacial = new JLabel(
                "<html><div style='text-align: center;'><span style='color: rgb(0, 119, 181); font-weight: bold;'>Acceder con Reconocimiento Facial</span></div></html>");
        lblFacial.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblFacial.setHorizontalAlignment(SwingConstants.CENTER);
        lblFacial.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblFacial.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String cedulaStr = JOptionPane.showInputDialog(null, "Ingrese su cédula para buscar el usuario:");
                if (cedulaStr != null && !cedulaStr.trim().isEmpty()) {
                    // Search for dummy user, as you need a user object for
                    // ReconocimientoFacialFrame. In production, grab user from file/db
                    com.comedorucv.modelos.Usuario u = new com.comedorucv.modelos.UsuarioDAO()
                            .buscarPorCedula(cedulaStr.trim());
                    if (u != null) {
                        new ReconocimientoFacialFrame(u).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Usuario con esa cédula no encontrado.", "Error de Acceso",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 20, 20, 20);
        card.add(lblFacial, gbc);

        contentPanel.add(card);
        add(contentPanel, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> {
            String correo = txtCorreo.getText().trim();
            String pass = new String(txtPass.getPassword());
            controller.login(correo, pass, this);
        });
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
        field.setOpaque(false);
        field.setPreferredSize(new Dimension(300, 30));
    }

    private JPanel createFieldPanel(String label, JComponent field) {
        return createFieldPanel(label, field, false);
    }

    private JPanel createFieldPanel(String label, JComponent field, boolean isPassword) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.BLACK);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);

        if (isPassword) {
            JLabel icon = new JLabel("👁");
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            icon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            icon.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JPasswordField passField = (JPasswordField) field;
                    if (passField.getEchoChar() != 0) {
                        passField.setEchoChar((char) 0);
                    } else {
                        passField.setEchoChar('•');
                    }
                }
            });
            p.add(icon, BorderLayout.EAST);
        }
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
