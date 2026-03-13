package com.comedorucv.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.OverlayLayout;
import javax.swing.border.EmptyBorder;

import com.comedorucv.modelos.Usuario;
import com.comedorucv.modelos.UsuarioDAO;
import com.comedorucv.utils.GestorSaldosYMenu;

public class DialogoSaldoPana extends JDialog {

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BUTTON_BLUE = new Color(0, 82, 126); // Un azul un poco más brillante para el botón
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color FIELD_BG = new Color(248, 250, 253);

    private Usuario usuarioActual;
    private JTextField txtCedula;
    private JComboBox<Integer> comboMonto;
    private JLabel lblMiSaldo;

    public DialogoSaldoPana(JFrame parent, Usuario usuario) {
        super(parent, "Saldo Pana - Apoyar Amigo", true);
        this.usuarioActual = usuario;

        setSize(1000, 700);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // Barra Azul Estándar (como Mis Turnos / Monedero)
        JPanel topBar = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        topBar.setLayout(new OverlayLayout(topBar));
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setPreferredSize(new Dimension(getWidth(), 85));

        // Lado Izquierdo: Logo e Inicio
        LogoPlaceholder logo = new LogoPlaceholder();
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        left.setOpaque(false);
        left.add(logo);

        JLabel inicio = new JLabel("<html><u>Inicio</u></html>");
        inicio.setForeground(Color.WHITE);
        inicio.setFont(new Font("Arial", Font.BOLD, 14));
        inicio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        inicio.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        });
        JPanel inicioHolder = new JPanel(new GridBagLayout());
        inicioHolder.setOpaque(false);
        inicioHolder.setPreferredSize(new Dimension(120, 88));
        inicioHolder.add(inicio);
        left.add(inicioHolder);

        // Centro: COMEDOR UCV
        JPanel titleUCVPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        titleUCVPanel.setOpaque(false);
        JLabel titleUCV = new JLabel("COMEDOR UCV", SwingConstants.CENTER);
        titleUCV.setForeground(Color.WHITE);
        titleUCV.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleUCVPanel.add(titleUCV);

        // Lado Derecho: Cerrar Sesión y Reloj
        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);

        JLabel cerrar = new JLabel("<html><u>Cerrar sesion</u></html>");
        cerrar.setForeground(Color.WHITE);
        cerrar.setFont(new Font("Arial", Font.BOLD, 14));
        cerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (JOptionPane.showConfirmDialog(null, "¿Deseas cerrar sesión?", "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    Window owner = getOwner();
                    if (owner != null) owner.dispose();
                    new LoginFrame().setVisible(true);
                    dispose();
                }
            }
        });
        JPanel rightHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 15));
        rightHolder.setOpaque(false);
        rightHolder.setPreferredSize(new Dimension(160, 44));
        rightHolder.add(cerrar);

        navContainer.add(rightHolder, BorderLayout.NORTH);

        RelojLabel dateLabel = new RelojLabel();
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        datePanel.setOpaque(false);
        datePanel.add(dateLabel);
        navContainer.add(datePanel, BorderLayout.CENTER);

        // Ensamblar lados
        JPanel sidesPanel = new JPanel(new BorderLayout());
        sidesPanel.setOpaque(false);
        sidesPanel.setBorder(new EmptyBorder(0, 40, 0, 40));
        sidesPanel.add(left, BorderLayout.WEST);
        sidesPanel.add(navContainer, BorderLayout.EAST);

        // Alinear para OverlayLayout
        titleUCVPanel.setAlignmentX(0.5f);
        titleUCVPanel.setAlignmentY(0.5f);
        sidesPanel.setAlignmentX(0.5f);
        sidesPanel.setAlignmentY(0.5f);

        topBar.add(titleUCVPanel);
        topBar.add(sidesPanel);

        // Panel para el título externo
        JPanel northMainPanel = new JPanel(new BorderLayout());
        northMainPanel.setBackground(BACKGROUND_COLOR);
        northMainPanel.add(topBar, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Transferencia Saldo Pana", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        northMainPanel.add(lblTitle, BorderLayout.SOUTH);

        add(northMainPanel, BorderLayout.NORTH);

        // Contenido Principal con Tarjeta Central
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));

        // TARJETA DE TRANSFERENCIA
        RoundedPanel transferCard = new RoundedPanel(30, Color.WHITE);
        transferCard.setLayout(new BoxLayout(transferCard, BoxLayout.Y_AXIS));
        transferCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 245), 2),
            BorderFactory.createEmptyBorder(30, 60, 30, 60)
        ));
        transferCard.setPreferredSize(new Dimension(750, 420));
        transferCard.setMaximumSize(new Dimension(750, 420));

        // 1. Origen: Tu Saldo
        JLabel sub1 = new JLabel("Cuenta de Origen", SwingConstants.LEFT);
        sub1.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        sub1.setForeground(new Color(100, 120, 140));
        sub1.setAlignmentX(Component.CENTER_ALIGNMENT);
        transferCard.add(sub1);
        transferCard.add(Box.createVerticalStrut(10));

        JPanel saldoContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saldoContainer.setOpaque(false);
        double saldoUser = GestorSaldosYMenu.leerSaldo(usuarioActual.getCedula());
        lblMiSaldo = new JLabel(String.format(Locale.US, "%.2f BS", saldoUser));
        lblMiSaldo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblMiSaldo.setForeground(PRIMARY_COLOR);
        saldoContainer.add(lblMiSaldo);
        transferCard.add(saldoContainer);

        transferCard.add(Box.createVerticalStrut(30));
        
        // Separador sutil
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));
        sep.setMaximumSize(new Dimension(500, 1));
        transferCard.add(sep);
        transferCard.add(Box.createVerticalStrut(30));

        // 2. Datos del Beneficiario
        JLabel lblCed = new JLabel("Cédula del Beneficiario", SwingConstants.CENTER);
        lblCed.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        lblCed.setForeground(new Color(60, 70, 80));
        lblCed.setAlignmentX(Component.CENTER_ALIGNMENT);
        transferCard.add(lblCed);
        transferCard.add(Box.createVerticalStrut(10));

        txtCedula = new JTextField();
        txtCedula.setPreferredSize(new Dimension(300, 45));
        txtCedula.setMaximumSize(new Dimension(300, 45));
        txtCedula.setBackground(FIELD_BG);
        txtCedula.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtCedula.setHorizontalAlignment(JTextField.CENTER);
        txtCedula.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 225, 240), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        transferCard.add(txtCedula);
        
        transferCard.add(Box.createVerticalStrut(25));

        // 3. Monto
        JLabel lblMon = new JLabel("Monto a Apoyar", SwingConstants.CENTER);
        lblMon.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        lblMon.setForeground(new Color(60, 70, 80));
        lblMon.setAlignmentX(Component.CENTER_ALIGNMENT);
        transferCard.add(lblMon);
        transferCard.add(Box.createVerticalStrut(10));

        Integer[] montosPredefinidos = {5, 10, 20, 50, 100, 200, 500};
        comboMonto = new JComboBox<>(montosPredefinidos);
        comboMonto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        comboMonto.setBackground(FIELD_BG);
        comboMonto.setPreferredSize(new Dimension(300, 45));
        comboMonto.setMaximumSize(new Dimension(300, 45));
        ((JLabel)comboMonto.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        transferCard.add(comboMonto);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainContent.add(transferCard, gbc);

        add(mainContent, BorderLayout.CENTER);

        // --- FOOTER: BOTONES ---
        JPanel footerWrapper = new JPanel(new BorderLayout());
        footerWrapper.setBackground(BACKGROUND_COLOR);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        footerPanel.setBackground(BACKGROUND_COLOR);

        RoundedButton btnCancel = new RoundedButton("Cancelar", new Color(130, 140, 150), Color.WHITE, 16);
        RoundedButton btnTransferir = new RoundedButton("Confirmar y Enviar", BUTTON_BLUE, Color.WHITE, 16);

        Dimension btnDim = new Dimension(220, 45);
        btnCancel.setPreferredSize(btnDim);
        btnTransferir.setPreferredSize(btnDim);

        btnCancel.addActionListener(e -> dispose());
        btnTransferir.addActionListener(e -> ejecutarTransferencia());

        footerPanel.add(btnCancel);
        footerPanel.add(btnTransferir);
        
        footerWrapper.add(footerPanel, BorderLayout.NORTH);

        // Franja azul decorativa al final
        JPanel blueStrip = new JPanel();
        blueStrip.setBackground(PRIMARY_COLOR);
        blueStrip.setPreferredSize(new Dimension(getWidth(), 12));
        footerWrapper.add(blueStrip, BorderLayout.SOUTH);

        add(footerWrapper, BorderLayout.SOUTH);
    }

    private JLabel createFormLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        l.setForeground(PRIMARY_COLOR);
        return l;
    }

    private void ejecutarTransferencia() {
        String ciAmigo = txtCedula.getText().trim();
        Integer monto = (Integer) comboMonto.getSelectedItem();

        if (ciAmigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa la cédula del amigo.", "Falta información", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ciAmigo.equals(usuarioActual.getCedula())) {
            JOptionPane.showMessageDialog(this, "No puedes transferir saldo a tu propia cuenta.", "Operación no permitida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double miSaldoActual = GestorSaldosYMenu.leerSaldo(usuarioActual.getCedula());
        if (miSaldoActual < monto) {
            JOptionPane.showMessageDialog(this, "No tienes saldo suficiente.\nSaldo actual: " + String.format(Locale.US, "%.2f", miSaldoActual) + " bs", "Saldo insuficiente", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario amigo = dao.buscarPorCedula(ciAmigo);
        if (amigo == null) {
            JOptionPane.showMessageDialog(this, "La cédula (" + ciAmigo + ") no existe en el sistema.", "Usuario no encontrado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmación final
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Confirmas transferir " + monto + " bs a " + amigo.getNombre() + "?",
            "Confirmar Saldo Pana", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            double nuevoSaldoMio = miSaldoActual - monto;
            double saldoAmigo = GestorSaldosYMenu.leerSaldo(amigo.getCedula());
            saldoAmigo += monto;

            GestorSaldosYMenu.guardarSaldo(usuarioActual.getCedula(), nuevoSaldoMio, 
                "Saldo Pana enviado -> " + amigo.getNombre() + " (CI: " + amigo.getCedula() + ")");
            GestorSaldosYMenu.guardarSaldo(amigo.getCedula(), saldoAmigo, 
                "Saldo Pana recibido <- " + usuarioActual.getNombre() + " (CI: " + usuarioActual.getCedula() + ")");

            JOptionPane.showMessageDialog(this, "¡Transferencia realizada con éxito!\nHas apoyado a " + amigo.getNombre() + " con " + monto + " bs", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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
