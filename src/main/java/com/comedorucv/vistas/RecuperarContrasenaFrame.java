package com.comedorucv.vistas;

import com.comedorucv.controladores.LoginController;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RecuperarContrasenaFrame extends JFrame {

    private JTextField txtCedula, txtCorreo;
    private LoginController controller = new LoginController();

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color ACCENT_COLOR = new Color(0, 70, 100);

    public RecuperarContrasenaFrame() {
        setTitle("Comedor UCV - Recuperar Contraseña");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JPanel logoContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        logoContainer.setOpaque(false);
        LogoPlaceholder logoImageComp = new LogoPlaceholder();
        logoContainer.add(logoImageComp);

        headerPanel.add(logoContainer, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("COMEDOR UCV", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

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
                public void mouseClicked(MouseEvent e) {
                    if (link.equals("Inicio")) {
                        new LoginFrame().setVisible(true);
                        dispose();
                    } else if (link.equals("Contacto")) {
                        JOptionPane.showMessageDialog(null, "Este es el número de contacto: 0212-6053954");
                    }
                }
            });
            navPanel.add(l);
        }

        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);
        navContainer.add(navPanel, BorderLayout.NORTH);

        RelojLabel dateLabel = new RelojLabel();
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        datePanel.setOpaque(false);
        datePanel.add(dateLabel);
        navContainer.add(datePanel, BorderLayout.CENTER);

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

        JLabel lblTitle = new JLabel("Recuperar Contraseña");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(lblTitle, gbc);

        JLabel lblInstruction = new JLabel(
                "<html><div style='text-align: center; width: 250px;'>Ingresa tu correo electrónico y te enviaremos las instrucciones de recuperación.</div></html>");
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblInstruction.setForeground(Color.GRAY);
        lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(lblInstruction, gbc);

        txtCedula = new JTextField();
        styleField(txtCedula);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 20, 10, 20);
        card.add(createFieldPanel("Cédula", txtCedula), gbc);

        txtCorreo = new JTextField();
        styleField(txtCorreo);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 20, 20, 20);
        card.add(createFieldPanel("Correo Electrónico", txtCorreo), gbc);

        JButton btnVerificar = new JButton("Verificar");
        btnVerificar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.setBackground(PRIMARY_COLOR);
        btnVerificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerificar.setUI(new BasicButtonUI() {
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
        btnVerificar.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btnVerificar.setContentAreaFilled(false);
        btnVerificar.setFocusPainted(false);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 20, 20, 20);
        card.add(btnVerificar, gbc);

        JLabel lblBack = new JLabel(
                "<html><span style='color: rgb(0, 51, 78); font-weight: bold;'>Volver al inicio de sesión</span></html>");
        lblBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBack.setHorizontalAlignment(SwingConstants.CENTER);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 20, 0, 20);
        card.add(lblBack, gbc);

        contentPanel.add(card);
        add(contentPanel, BorderLayout.CENTER);

        btnVerificar.addActionListener(e -> {
            String cedula = txtCedula.getText().trim();
            String correo = txtCorreo.getText().trim();
            if (controller.verificarUsuario(cedula, correo)) {
                new NuevaContrasenaFrame(cedula).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Datos incorrectos o usuario no encontrado");
            }
        });
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new MatteBorder(0, 0, 2, 0, Color.BLACK));
        field.setOpaque(false);
        field.setPreferredSize(new Dimension(300, 30));
    }

    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.BLACK);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
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
