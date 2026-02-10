package com.comedorucv.vistas;

import com.comedorucv.controladores.LoginController;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NuevaContrasenaFrame extends JFrame {

    private String cedula;
    private JPasswordField txtNuevaPass, txtConfirmPass;
    private LoginController controller = new LoginController();

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color ACCENT_COLOR = new Color(0, 70, 100);

    public NuevaContrasenaFrame(String cedula) {
        this.cedula = cedula;
        setTitle("Comedor UCV - Nueva Contraseña");
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
                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE)
                        img = icon.getImage();
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
            if (link.equals("Inicio")) {
                l.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        new LoginFrame().setVisible(true);
                        dispose();
                    }
                });
            } else if (link.equals("Contacto")) {
                l.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JOptionPane.showMessageDialog(null, "Este es el número de contacto: 0212-6053954");
                    }
                });
            }
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
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Restablecer Contraseña");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(lblTitle, gbc);

        txtNuevaPass = new JPasswordField();
        styleField(txtNuevaPass);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 10, 20);
        card.add(createFieldPanel("Nueva Contraseña", txtNuevaPass), gbc);

        txtConfirmPass = new JPasswordField();
        styleField(txtConfirmPass);
        gbc.gridy = 2;
        card.add(createFieldPanel("Confirmar Nueva Contraseña", txtConfirmPass), gbc);

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setBackground(PRIMARY_COLOR);
        btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAceptar.setUI(new BasicButtonUI() {
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
        btnAceptar.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btnAceptar.setContentAreaFilled(false);
        btnAceptar.setFocusPainted(false);
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 20, 10, 20);
        card.add(btnAceptar, gbc);

        contentPanel.add(card);
        add(contentPanel, BorderLayout.CENTER);

        btnAceptar.addActionListener(e -> {
            String pass1 = new String(txtNuevaPass.getPassword());
            String pass2 = new String(txtConfirmPass.getPassword());
            if (pass1.isEmpty() || pass2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar ambos campos");
                return;
            }
            if (!pass1.equals(pass2)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden");
                return;
            }
            if (controller.actualizarContrasena(cedula, pass1)) {
                JOptionPane.showMessageDialog(this, "Contraseña actualizada correctamente");
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar la contraseña");
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
