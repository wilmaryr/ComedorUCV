package com.comedorucv.vistas;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.comedorucv.controladores.GestorMenu;
import com.comedorucv.modelos.Plato;
import com.comedorucv.modelos.Usuario;

public class VentanaPrincipal extends JFrame {
    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color FIELD_BG = new Color(242, 242, 242);
    private final Color BORDER_BLUE = new Color(0, 119, 181);

    private GestorMenu gestor;
    private Map<String, Map<String, Double>> mapaCostos;
    private Map<String, Map<String, Plato>> mapaPlatos;

    private JComboBox<String> cbDia;
    private JTextField txtPlato, txtInsumos, txtDisponibilidad;
    private JComboBox<String> cbContorno1, cbContorno2;
    private JComboBox<String> cbBebida, cbPostre;

    private JLabel lblDesayuno, lblAlmuerzo;
    private String turnoActual = "DESAYUNO";

    private Map<String, Double> catalogoPlatos;
    private Map<String, Double> catalogoBebidas;
    private Map<String, Double> catalogoPostres;

    private Map<String, TarjetaDia> mapaTarjetas;
    private Usuario usuarioActual;

    public VentanaPrincipal(Usuario u) {
        this.usuarioActual = u;
        gestor = new GestorMenu();
        mapaCostos = new HashMap<>();
        mapaPlatos = new HashMap<>();
        mapaTarjetas = new HashMap<>();

        setTitle("Gestionar menú");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftNav.setOpaque(false);
        leftNav.add(new LogoPlaceholder(45));

        JLabel lblInicio = createNavLink("Inicio");
        lblInicio.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (usuarioActual != null && "Administrador".equals(usuarioActual.getRol())) {
                    new AdministradorFrame(usuarioActual).setVisible(true);
                } else {
                    new ComensalFrame(usuarioActual).setVisible(true);
                }
                dispose();
            }
        });
        leftNav.add(lblInicio);
        headerPanel.add(leftNav, BorderLayout.WEST);

        JLabel lblLogout = createNavLink("Cerrar sesión");
        lblLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (JOptionPane.showConfirmDialog(null, "¿Deseas cerrar sesión?", "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    dispose();
                }
            }
        });
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        rightNav.setOpaque(false);
        rightNav.add(lblLogout);
        headerPanel.add(rightNav, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentArea = new JPanel();
        contentArea.setBackground(BACKGROUND_COLOR);
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        GridBagConstraints tgbc = new GridBagConstraints();

        JLabel lblTitle = new JLabel("Gestionar menú");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(PRIMARY_COLOR);
        tgbc.gridx = 0;
        tgbc.gridy = 0;
        titlePanel.add(lblTitle, tgbc);

        JPanel turnTabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        turnTabs.setOpaque(false);
        lblDesayuno = createTurnLabel("Desayuno", true);
        lblAlmuerzo = createTurnLabel("Almuerzo", false);
        turnTabs.add(lblDesayuno);
        turnTabs.add(lblAlmuerzo);

        tgbc.gridy = 1;
        tgbc.insets = new Insets(10, 0, 0, 0);
        titlePanel.add(turnTabs, tgbc);
        contentArea.add(titlePanel);

        initializeCatalog();

        RoundedBorderPanel formBox = new RoundedBorderPanel(30, 3, BORDER_BLUE);
        formBox.setLayout(new GridBagLayout());
        formBox.setBackground(BACKGROUND_COLOR);
        formBox.setPreferredSize(new Dimension(1100, 200));
        formBox.setMaximumSize(new Dimension(1150, 200));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(10, 25, 10, 25);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.anchor = GridBagConstraints.WEST;

        cbDia = new JComboBox<>(new String[] { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" });
        txtPlato = new JTextField();
        cbContorno1 = new JComboBox<>(catalogoPlatos.keySet().toArray(new String[0]));
        cbContorno2 = new JComboBox<>(catalogoPlatos.keySet().toArray(new String[0]));
        cbBebida = new JComboBox<>(catalogoBebidas.keySet().toArray(new String[0]));
        cbPostre = new JComboBox<>(catalogoPostres.keySet().toArray(new String[0]));
        txtInsumos = new JTextField();
        txtDisponibilidad = new JTextField();

        fgbc.gridx = 0;
        fgbc.gridy = 0;
        formBox.add(createField("Dia de la semana:", cbDia), fgbc);
        fgbc.gridy = 1;
        formBox.add(createField("Plato principal", txtPlato), fgbc);

        fgbc.gridx = 1;
        fgbc.gridy = 0;
        formBox.add(createField("Contorno 1:", cbContorno1), fgbc);
        fgbc.gridy = 1;
        formBox.add(createField("Contorno 2:", cbContorno2), fgbc);

        fgbc.gridx = 2;
        fgbc.gridy = 0;
        formBox.add(createField("Bebida:", cbBebida), fgbc);
        fgbc.gridy = 1;
        formBox.add(createField("Postre:", cbPostre), fgbc);

        fgbc.gridx = 3;
        fgbc.gridy = 0;
        formBox.add(createField("Insumos por plato:", txtInsumos), fgbc);
        fgbc.gridy = 1;
        formBox.add(createField("Disponibilidad:", txtDisponibilidad), fgbc);

        fgbc.gridx = 4;
        fgbc.gridy = 0;
        fgbc.gridheight = 2;
        fgbc.fill = GridBagConstraints.NONE;
        fgbc.insets = new Insets(0, 10, 0, 30);
        RoundedButton btnAgregar = new RoundedButton("Agregar", PRIMARY_COLOR, Color.WHITE, 40);
        btnAgregar.setPreferredSize(new Dimension(100, 45));
        formBox.add(btnAgregar, fgbc);

        contentArea.add(Box.createVerticalStrut(20));
        JPanel formWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formWrapper.setOpaque(false);
        formWrapper.add(formBox);
        contentArea.add(formWrapper);

        JPanel cardsWrapper = new JPanel(new GridLayout(1, 5, 20, 0));
        cardsWrapper.setBackground(BACKGROUND_COLOR);
        cardsWrapper.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        cardsWrapper.setMaximumSize(new Dimension(1250, 400));

        String[] dias = { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" };
        for (String d : dias) {
            TarjetaDia tarjeta = new TarjetaDia(d);
            mapaTarjetas.put(d, tarjeta);
            cardsWrapper.add(tarjeta);

            tarjeta.getBtnEliminar().addActionListener(e -> {
                Map<String, Plato> pT = mapaPlatos.get(d);
                if (pT != null)
                    pT.remove(turnoActual);
                Map<String, Double> cT = mapaCostos.get(d);
                if (cT != null)
                    cT.remove(turnoActual);
                tarjeta.actualizarInfo(null);
                tarjeta.actualizarCosto(null);
                saveMenuToDisk();
                saveCostosToDisk();
            });

            tarjeta.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Map<String, Plato> pT = mapaPlatos.get(d);
                    Plato p = (pT == null ? null : pT.get(turnoActual));
                    if (p != null) {
                        cbDia.setSelectedItem(d);
                        txtPlato.setText(p.getNombre());
                        cbContorno1.setSelectedItem(p.getContorno1() == null ? "" : p.getContorno1());
                        cbContorno2.setSelectedItem(p.getContorno2() == null ? "" : p.getContorno2());
                        cbBebida.setSelectedItem(p.getBebida() == null ? "" : p.getBebida());
                        cbPostre.setSelectedItem(p.getPostre() == null ? "" : p.getPostre());
                        txtInsumos.setText(String.valueOf(p.getInsumos()));
                        txtDisponibilidad.setText(String.valueOf(p.getDisponibilidad()));
                    }
                }
            });
        }
        contentArea.add(Box.createVerticalStrut(20));
        contentArea.add(cardsWrapper);

        add(contentArea, BorderLayout.CENTER);

        loadMenuFromDisk();
        loadCostosFromDisk();
        updateTurnUI();

        btnAgregar.addActionListener(e -> {
            String diaSel = (String) cbDia.getSelectedItem();
            String c1 = (String) cbContorno1.getSelectedItem();
            if (c1 == null || c1.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Contorno 1 es obligatorio.");
                return;
            }
            try {
                Plato p = new Plato(txtPlato.getText(), c1, (String) cbContorno2.getSelectedItem(),
                        (String) cbBebida.getSelectedItem(), (String) cbPostre.getSelectedItem(),
                        Integer.parseInt(txtInsumos.getText()), Integer.parseInt(txtDisponibilidad.getText()));

                mapaPlatos.computeIfAbsent(diaSel, k -> new HashMap<>()).put(turnoActual, p);
                mapaTarjetas.get(diaSel).actualizarInfo(p);

                double total = catalogoPlatos.getOrDefault(c1, 0.0) +
                        catalogoPlatos.getOrDefault((String) cbContorno2.getSelectedItem(), 0.0) +
                        catalogoBebidas.getOrDefault((String) cbBebida.getSelectedItem(), 0.0) +
                        catalogoPostres.getOrDefault((String) cbPostre.getSelectedItem(), 0.0);

                mapaCostos.computeIfAbsent(diaSel, k -> new HashMap<>()).put(turnoActual, total);
                mapaTarjetas.get(diaSel).actualizarCosto(total);

                limpiarCampos();
                saveMenuToDisk();
                saveCostosToDisk();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Insumos y Disponibilidad deben ser números.");
            }
        });
    }

    private JLabel createNavLink(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return lbl;
    }

    private JLabel createTurnLabel(String text, boolean active) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(active ? BORDER_BLUE : Color.GRAY);
        if (active)
            lbl.setText("<html><u>" + text + "</u></html>");
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                turnoActual = text.toUpperCase();
                updateTurnUI();
            }
        });
        return lbl;
    }

    private void updateTurnUI() {
        lblDesayuno.setForeground("DESAYUNO".equals(turnoActual) ? BORDER_BLUE : Color.GRAY);
        lblDesayuno.setText("DESAYUNO".equals(turnoActual) ? "<html><u>Desayuno</u></html>" : "Desayuno");

        lblAlmuerzo.setForeground("ALMUERZO".equals(turnoActual) ? BORDER_BLUE : Color.GRAY);
        lblAlmuerzo.setText("ALMUERZO".equals(turnoActual) ? "<html><u>Almuerzo</u></html>" : "Almuerzo");

        for (Map.Entry<String, TarjetaDia> entry : mapaTarjetas.entrySet()) {
            Map<String, Plato> pT = mapaPlatos.get(entry.getKey());
            entry.getValue().actualizarInfo(pT == null ? null : pT.get(turnoActual));
            Map<String, Double> cT = mapaCostos.get(entry.getKey());
            entry.getValue().actualizarCosto(cT == null ? null : cT.get(turnoActual));
        }
    }

    private JPanel createField(String labelText, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.DARK_GRAY);
        p.add(lbl, BorderLayout.NORTH);

        if (comp instanceof JTextField) {
            comp.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(15),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            comp.setBackground(FIELD_BG);
        } else if (comp instanceof JComboBox) {
            comp.setBackground(FIELD_BG);
            comp.setPreferredSize(new Dimension(180, 35));
        }
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void initializeCatalog() {
        catalogoPlatos = new LinkedHashMap<>();
        catalogoPlatos.put("", 0.0);
        catalogoPlatos.put("Pabellón Criollo", 4.85);
        catalogoPlatos.put("Asado Negro con Arroz", 5.10);
        catalogoPlatos.put("Arepa Pelúa", 3.80);
        catalogoPlatos.put("Pollo a la Canasta con Papas", 4.25);
        catalogoPlatos.put("Pasta a la Boloñesa", 3.95);
        catalogoPlatos.put("Pescado Frito con Ensalada", 5.50);
        catalogoPlatos.put("Carne Guisada con Papas", 4.40);
        catalogoPlatos.put("Arroz con Pollo", 3.75);
        catalogoPlatos.put("Chuleta Ahumada Parrillera", 4.90);
        catalogoPlatos.put("Pasticho de Carne", 5.75);
        catalogoPlatos.put("Milanesa de Pollo con Puré", 4.15);
        catalogoPlatos.put("Sopa de Res (Hervido)", 3.20);
        catalogoPlatos.put("Caraotas Negras con Queso", 3.10);
        catalogoPlatos.put("Ensalada César con Pollo", 4.50);
        catalogoPlatos.put("Lasaña de Vegetales", 4.60);
        catalogoPlatos.put("Hamburguesa Universitaria", 4.20);
        catalogoPlatos.put("Perro Caliente Especial (2)", 3.30);
        catalogoPlatos.put("Filete de Merluza al Vapor", 5.25);
        catalogoPlatos.put("Albóndigas con Pasta", 4.10);
        catalogoPlatos.put("Pollo Sudado con Yuca", 3.90);
        catalogoPlatos.put("Bistec a lo Pobre", 4.95);
        catalogoPlatos.put("Risotto de Champiñones", 5.20);
        catalogoPlatos.put("Tacos de Carne (3)", 4.15);
        catalogoPlatos.put("Club House Sandwich", 4.80);
        catalogoPlatos.put("Cerdo Agridulce con Arroz", 4.65);
        catalogoPlatos.put("Ñoquis con Pesto", 4.85);
        catalogoPlatos.put("Pollo Teriyaki", 4.75);
        catalogoPlatos.put("Salpicón de Mariscos", 6.10);
        catalogoPlatos.put("Tortilla Española", 3.80);
        catalogoPlatos.put("Cazuela de Granos Mixtos", 3.40);

        catalogoBebidas = new LinkedHashMap<>();
        catalogoBebidas.put("", 0.0);
        catalogoBebidas.put("Papelón con Limón", 0.85);
        catalogoBebidas.put("Jugo de Guayaba", 1.10);
        catalogoBebidas.put("Jugo de Parchita", 1.20);
        catalogoBebidas.put("Chicha de Arroz", 1.95);
        catalogoBebidas.put("Batido de Cambur", 1.50);
        catalogoBebidas.put("Té Helado", 0.95);
        catalogoBebidas.put("Café Negro", 0.60);
        catalogoBebidas.put("Café con Leche", 1.25);
        catalogoBebidas.put("Refresco de Lata", 1.40);
        catalogoBebidas.put("Agua Mineral", 0.80);

        catalogoPostres = new LinkedHashMap<>();
        catalogoPostres.put("", 0.0);
        catalogoPostres.put("Quesillo Venezolano", 1.90);
        catalogoPostres.put("Arroz con Leche", 1.50);
        catalogoPostres.put("Dulce de Lechosa", 1.80);
        catalogoPostres.put("Torta de Tres Leches", 2.75);
        catalogoPostres.put("Brownie de Chocolate", 1.70);
        catalogoPostres.put("Ensalada de Frutas", 2.20);
        catalogoPostres.put("Majarete de Coco", 1.60);
        catalogoPostres.put("Galletas de Avena (3 u.)", 1.15);
        catalogoPostres.put("Helado de Mantecado", 1.40);
        catalogoPostres.put("Torta de Piña", 2.30);
        catalogoPostres.put("Gelatina de Sabores", 0.85);
        catalogoPostres.put("Mouse de Parchita", 1.95);
        catalogoPostres.put("Golfeado con Queso", 2.25);
        catalogoPostres.put("Alfajores (2 u.)", 1.65);
        catalogoPostres.put("Pie de Limón", 2.40);
    }

    private void saveMenuToDisk() {
        try {
            Path file = resolveSavePath("menu_save.txt");
            if (file.getParent() != null)
                Files.createDirectories(file.getParent());
            List<String> lines = new ArrayList<>();
            for (String dia : mapaPlatos.keySet()) {
                Map<String, Plato> pT = mapaPlatos.get(dia);
                if (pT == null)
                    continue;
                for (Map.Entry<String, Plato> e : pT.entrySet()) {
                    Plato p = e.getValue();
                    if (p == null)
                        continue;
                    lines.add(String.join("\t", dia, e.getKey(), p.getNombre(), p.getContorno1(),
                            p.getContorno2(), p.getBebida(), p.getPostre(),
                            String.valueOf(p.getInsumos()), String.valueOf(p.getDisponibilidad())));
                }
            }
            Files.write(file, lines, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadMenuFromDisk() {
        try {
            Path file = resolveSavePath("menu_save.txt");
            if (!Files.exists(file))
                return;
            List<String> lines = Files.readAllLines(file, java.nio.charset.StandardCharsets.UTF_8);
            for (String l : lines) {
                if (l.trim().isEmpty())
                    continue;
                String[] parts = l.split("\t", -1);
                if (parts.length < 9)
                    continue;
                try {
                    Plato p = new Plato(parts[2], parts[3], parts[4], parts[5], parts[6],
                            Integer.parseInt(parts[7]), Integer.parseInt(parts[8]));
                    mapaPlatos.computeIfAbsent(parts[0], k -> new HashMap<>()).put(parts[1], p);
                } catch (Exception nfe) {
                }
            }
        } catch (Exception ex) {
        }
    }

    private void saveCostosToDisk() {
        try {
            Path file = resolveSavePath("costos_save.txt");
            if (file.getParent() != null)
                Files.createDirectories(file.getParent());
            List<String> lines = new ArrayList<>();
            for (String dia : mapaCostos.keySet()) {
                Map<String, Double> pT = mapaCostos.get(dia);
                if (pT == null)
                    continue;
                for (Map.Entry<String, Double> e : pT.entrySet()) {
                    lines.add(String.join("\t", dia, e.getKey(), String.format(Locale.US, "%.2f", e.getValue())));
                }
            }
            Files.write(file, lines, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException ex) {
        }
    }

    private void loadCostosFromDisk() {
        try {
            Path file = resolveSavePath("costos_save.txt");
            if (!Files.exists(file))
                return;
            List<String> lines = Files.readAllLines(file, java.nio.charset.StandardCharsets.UTF_8);
            for (String l : lines) {
                if (l.trim().isEmpty())
                    continue;
                String[] parts = l.split("\t", -1);
                if (parts.length < 3)
                    continue;
                try {
                    mapaCostos.computeIfAbsent(parts[0], k -> new HashMap<>()).put(parts[1],
                            Double.parseDouble(parts[2]));
                } catch (Exception nfe) {
                }
            }
        } catch (Exception ex) {
        }
    }

    private Path resolveSavePath(String filename) {
        return Paths.get(System.getProperty("data.dir", Paths.get(System.getProperty("user.dir"), "data").toString()), filename);
    }

    private void limpiarCampos() {
        txtPlato.setText("");
        cbContorno1.setSelectedIndex(0);
        cbContorno2.setSelectedIndex(0);
        cbBebida.setSelectedIndex(0);
        cbPostre.setSelectedIndex(0);
        txtInsumos.setText("");
        txtDisponibilidad.setText("");
    }

    static class RoundedBorderPanel extends JPanel {
        private int radius;
        private int strokeWidth;
        private Color borderColor;

        public RoundedBorderPanel(int radius, int stroke, Color border) {
            this.radius = radius;
            this.strokeWidth = stroke;
            this.borderColor = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.drawRoundRect(strokeWidth / 2, strokeWidth / 2, getWidth() - strokeWidth, getHeight() - strokeWidth,
                    radius, radius);
            g2.dispose();
        }
    }
}
