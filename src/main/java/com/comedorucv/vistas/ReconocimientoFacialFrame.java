package com.comedorucv.vistas;

import com.comedorucv.controladores.AccesoController;
import com.comedorucv.modelos.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ReconocimientoFacialFrame extends JFrame {

    private Usuario usuario;
    private AccesoController controller;
    private File archivoImagenSeleccionado;
    private JLabel lblPreview;
    private final Color PRIMARY_COLOR = new Color(0, 51, 78);

    public ReconocimientoFacialFrame(Usuario u) {
        this.usuario = u;
        this.controller = new AccesoController();

        setTitle("Reconocimiento Facial - Comedor UCV");
        setSize(1000, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // --- HEADER (Mismo estilo que Login) ---
        JPanel headerPanel = new JPanel() {
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        headerPanel.setLayout(new OverlayLayout(headerPanel));
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

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 25));
        navPanel.setOpaque(false);
        String[] links = { "Inicio", "Contacto" };
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
                    }
                }
            });
            navPanel.add(l);
        }

        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);
        navContainer.add(navPanel, BorderLayout.NORTH);

        RelojLabel dateLabel = new RelojLabel();
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 5));
        datePanel.setOpaque(false);
        datePanel.add(dateLabel);

        navContainer.add(datePanel, BorderLayout.CENTER);

        headerPanel.add(logoContainer, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(navContainer, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- CONTENIDO ---
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
        card.setPreferredSize(new Dimension(500, 550));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("Verificación Facial");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(lblTitle, gbc);

        // Preview Area
        JPanel previewContainer = new JPanel(new BorderLayout());
        previewContainer.setBackground(new Color(245, 245, 245));
        previewContainer.setBorder(BorderFactory.createDashedBorder(PRIMARY_COLOR, 2, 5, 2, true));
        previewContainer.setPreferredSize(new Dimension(300, 300));

        lblPreview = new JLabel("Clic para cargar foto.png", SwingConstants.CENTER);
        lblPreview.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblPreview.setForeground(Color.GRAY);
        previewContainer.add(lblPreview, BorderLayout.CENTER);

        previewContainer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        previewContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarImagen();
            }
        });

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 30, 0);
        card.add(previewContainer, gbc);

        // Botón de Verificación
        JButton btnVerificar = new JButton("VERIFICAR Y ACCEDER");
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
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        btnVerificar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        btnVerificar.setContentAreaFilled(false);
        btnVerificar.setFocusPainted(false);
        btnVerificar.addActionListener(e -> verificarAcceso());

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 0, 20);
        card.add(btnVerificar, gbc);

        contentPanel.add(card);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes PNG", "png"));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            archivoImagenSeleccionado = fileChooser.getSelectedFile();
            mostrarPreview(archivoImagenSeleccionado);
        }
    }

    private void mostrarPreview(File file) {
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        Image img = icon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);
        lblPreview.setIcon(new ImageIcon(img));
        lblPreview.setText("");
    }

    private void verificarAcceso() {
        if (archivoImagenSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, cargue una imagen para la verificación.", "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean exito = controller.procesarAcceso(usuario, archivoImagenSeleccionado);
        if (exito) {
            new VentanaMonedero(usuario).setVisible(true);
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
