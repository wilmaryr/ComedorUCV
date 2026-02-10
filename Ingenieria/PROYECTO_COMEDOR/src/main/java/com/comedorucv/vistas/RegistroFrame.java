package com.comedorucv.vistas;

import com.comedorucv.controladores.RegistroController;
import com.comedorucv.modelos.Usuario;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistroFrame extends JFrame {

    private JTextField txtNombre, txtApellido, txtCorreo, txtCedula;
    private JPasswordField txtPassword, txtConfirmPassword;
    private RegistroController controller = new RegistroController();

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color ACCENT_COLOR = new Color(0, 70, 100);

    public RegistroFrame() {
        setTitle("Comedor UCV - Registro");
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

        JPanel logoContainer = new JPanel(new GridBagLayout());
        logoContainer.setOpaque(false);
        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.anchor = GridBagConstraints.CENTER;
        gbcLogo.insets = new Insets(5, 0, 0, 15);

        JComponent logoImageComp = new JComponent() {
            private Image img;
            {
                try {
                    ImageIcon icon = new ImageIcon(
                            getClass().getResource("/imagenes/Logo_Universidad_Central_de_Venezuela.png"));
                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        img = icon.getImage();
                    }
                } catch (Exception e) {
                }
                setPreferredSize(new Dimension(65, 65));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    double scale = Math.min((double) getWidth() / img.getWidth(null),
                            (double) getHeight() / img.getHeight(null));
                    int w = (int) (img.getWidth(null) * scale);
                    int h = (int) (img.getHeight(null) * scale);
                    int x = (getWidth() - w) / 2;
                    int y = (getHeight() - h) / 2;
                    g2.drawImage(img, x, y, w, h, null);
                    g2.dispose();
                }
            }
        };
        logoContainer.add(logoImageComp, gbcLogo);

        JLabel ucvTextLabel = new JLabel("UCV");
        ucvTextLabel.setForeground(Color.WHITE);
        ucvTextLabel.setFont(new Font("Serif", Font.BOLD, 30));
        gbcLogo.gridx = 1;
        gbcLogo.insets = new Insets(0, 0, 0, 0);
        logoContainer.add(ucvTextLabel, gbcLogo);
        headerPanel.add(logoContainer, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("COMEDOR UCV");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
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
        headerPanel.add(navPanel, BorderLayout.EAST);
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
        card.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblFormTitle = new JLabel("Regístrate");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblFormTitle.setForeground(PRIMARY_COLOR);
        lblFormTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(lblFormTitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 15, 20, 15);
        gbc.weightx = 0.5;

        txtNombre = new JTextField();
        styleField(txtNombre);
        gbc.gridx = 0;
        gbc.gridy = 1;
        card.add(createFieldPanel("Nombre", txtNombre), gbc);

        txtApellido = new JTextField();
        styleField(txtApellido);
        gbc.gridx = 1;
        gbc.gridy = 1;
        card.add(createFieldPanel("Apellido", txtApellido), gbc);

        txtCedula = new JTextField();
        styleField(txtCedula);
        gbc.gridx = 0;
        gbc.gridy = 2;
        card.add(createFieldPanel("Cédula", txtCedula), gbc);

        txtCorreo = new JTextField();
        styleField(txtCorreo);
        gbc.gridx = 1;
        gbc.gridy = 2;
        card.add(createFieldPanel("Correo electrónico", txtCorreo), gbc);

        txtPassword = new JPasswordField();
        styleField(txtPassword);
        gbc.gridx = 0;
        gbc.gridy = 3;
        card.add(createFieldPanel("Contraseña", txtPassword, true), gbc);

        txtConfirmPassword = new JPasswordField();
        styleField(txtConfirmPassword);
        gbc.gridx = 1;
        gbc.gridy = 3;
        card.add(createFieldPanel("Confirmar contraseña", txtConfirmPassword, true), gbc);

        JButton btnRegister = new JButton("Crear cuenta");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(PRIMARY_COLOR);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setUI(new BasicButtonUI() {
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
        btnRegister.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnRegister.setContentAreaFilled(false);
        btnRegister.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 20, 15, 20);
        card.add(btnRegister, gbc);

        JLabel lblLogin = new JLabel(
                "<html><div style='text-align: center;'>¿Ya tienes cuenta? <span style='color: rgb(0, 51, 78);'>Inicia sesión</span></div></html>");
        lblLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(lblLogin, gbc);

        contentPanel.add(card);
        add(contentPanel, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> registerAction());
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
        field.setOpaque(false);
        field.setPreferredSize(new Dimension(200, 30));
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
                    if (passField.getEchoChar() != 0)
                        passField.setEchoChar((char) 0);
                    else
                        passField.setEchoChar('•');
                }
            });
            p.add(icon, BorderLayout.EAST);
        }
        return p;
    }

    private void registerAction() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String cedula = txtCedula.getText().trim();
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        if (pass.isEmpty() || confirm.isEmpty() || nombre.isEmpty() || correo.isEmpty() || cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos obligatorios");
            return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden");
            return;
        }

        Usuario u = new Usuario();
        u.setCedula(cedula);
        u.setNombre(nombre + " " + apellido);
        u.setCorreo(correo);
        u.setPassword(pass);
        if (controller.registrar(u)) {
            new LoginFrame().setVisible(true);
            dispose();
        }
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
