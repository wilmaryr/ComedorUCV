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
import com.comedorucv.utils.DataPathResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.OverlayLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
    private JComboBox<String> cbPlatoPrincipal;
    private JTextField txtInsumos, txtDisponibilidad;
    private JComboBox<String> cbContorno1, cbContorno2;
    private JComboBox<String> cbBebida, cbPostre;

    private JLabel lblDesayuno, lblAlmuerzo;
    private String turnoActual = "DESAYUNO";

    private Map<String, Double> catalogoPlatosPrincipales;
    private Map<String, Double> catalogoContornos;
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
        setSize(1300, 950);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
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

        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftNav.setOpaque(false);
        leftNav.add(new LogoPlaceholder());

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

        JLabel titleLabel = new JLabel("COMEDOR UCV", javax.swing.SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 0));

        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);

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
        RoundedButton btnEditar = new RoundedButton("Editar catálogo", BORDER_BLUE, Color.WHITE, 20);
        btnEditar.setPreferredSize(new Dimension(150, 40));

        btnEditar.addActionListener(e -> {
            EditorCatalogo editor = new EditorCatalogo(this);
            editor.setVisible(true);
            loadCatalogFromDisk();
            updateCatalogUI();
        });

        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        rightNav.setOpaque(false);
        rightNav.add(lblLogout);
        navContainer.add(rightNav, BorderLayout.NORTH);

        RelojLabel dateLabel = new RelojLabel();
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        datePanel.setOpaque(false);
        datePanel.add(dateLabel);

        navContainer.add(datePanel, BorderLayout.CENTER);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);

        JPanel sidesPanel = new JPanel(new BorderLayout());
        sidesPanel.setOpaque(false);
        sidesPanel.add(leftNav, BorderLayout.WEST);
        sidesPanel.add(navContainer, BorderLayout.EAST);

        headerPanel.add(titlePanel);
        headerPanel.add(sidesPanel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentArea = new JPanel();
        contentArea.setBackground(BACKGROUND_COLOR);
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));

        JPanel titleContainer = new JPanel(new GridBagLayout());
        titleContainer.setBackground(BACKGROUND_COLOR);
        titleContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        GridBagConstraints tgbc = new GridBagConstraints();

        JLabel lblTitle = new JLabel("Gestionar menú");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(PRIMARY_COLOR);
        tgbc.gridx = 0;
        tgbc.gridy = 0;
        titleContainer.add(lblTitle, tgbc);

        JPanel turnTabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        turnTabs.setOpaque(false);
        lblDesayuno = createTurnLabel("Desayuno", true);
        lblAlmuerzo = createTurnLabel("Almuerzo", false);
        turnTabs.add(lblDesayuno);
        turnTabs.add(lblAlmuerzo);

        tgbc.gridx = 0;
        tgbc.gridy = 1;
        tgbc.insets = new Insets(10, 0, 0, 0);
        titleContainer.add(turnTabs, tgbc);
        contentArea.add(titleContainer);

        initializeCatalog();

        RoundedBorderPanel formBox = new RoundedBorderPanel(30, 3, BORDER_BLUE);
        formBox.setLayout(new GridBagLayout());
        formBox.setBackground(BACKGROUND_COLOR);
        formBox.setMinimumSize(new Dimension(1100, 250));
        formBox.setPreferredSize(new Dimension(1100, 250));
        formBox.setMaximumSize(new Dimension(1150, 250));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(10, 25, 10, 25);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.anchor = GridBagConstraints.WEST;

        cbDia = new JComboBox<>(new String[] { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" });
        cbPlatoPrincipal = new JComboBox<>(catalogoPlatosPrincipales.keySet().toArray(new String[0]));
        cbContorno1 = new JComboBox<>(catalogoContornos.keySet().toArray(new String[0]));
        cbContorno2 = new JComboBox<>(catalogoContornos.keySet().toArray(new String[0]));
        cbBebida = new JComboBox<>(catalogoBebidas.keySet().toArray(new String[0]));
        cbPostre = new JComboBox<>(catalogoPostres.keySet().toArray(new String[0]));
        txtInsumos = new JTextField();
        txtDisponibilidad = new JTextField();

        fgbc.gridx = 0;
        fgbc.gridy = 0;
        formBox.add(createField("Dia de la semana:", cbDia), fgbc);
        fgbc.gridy = 1;
        formBox.add(createField("Plato principal", cbPlatoPrincipal), fgbc);

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
        fgbc.gridheight = 1;
        fgbc.fill = GridBagConstraints.NONE;
        fgbc.insets = new Insets(10, 10, 5, 30);
        formBox.add(btnEditar, fgbc);

        RoundedButton btnAgregar = new RoundedButton("Agregar", PRIMARY_COLOR, Color.WHITE, 40);
        btnAgregar.setPreferredSize(new Dimension(150, 40));
        fgbc.gridy = 1;
        fgbc.insets = new Insets(5, 10, 10, 30);
        formBox.add(btnAgregar, fgbc);

        contentArea.add(Box.createVerticalStrut(20));
        JPanel formWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formWrapper.setOpaque(false);
        formWrapper.add(formBox);
        contentArea.add(formWrapper);

        JPanel cardsWrapper = new JPanel(new GridLayout(1, 5, 20, 0));
        cardsWrapper.setBackground(BACKGROUND_COLOR);
        cardsWrapper.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        cardsWrapper.setMinimumSize(new Dimension(1100, 320));
        cardsWrapper.setPreferredSize(new Dimension(1100, 320));
        cardsWrapper.setMaximumSize(new Dimension(1250, 320));

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
                        cbPlatoPrincipal.setSelectedItem(p.getNombre());
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
        contentArea.add(Box.createVerticalStrut(60));
        contentArea.add(cardsWrapper);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(PRIMARY_COLOR);
        footerPanel.setPreferredSize(new Dimension(getWidth(), 40));
        add(footerPanel, BorderLayout.SOUTH);

        loadMenuFromDisk();
        loadCostosFromDisk();
        updateTurnUI();

        btnAgregar.addActionListener(e -> {
            String diaSel = (String) cbDia.getSelectedItem();
            String platoPrinc = (String) cbPlatoPrincipal.getSelectedItem();
            if (platoPrinc == null || platoPrinc.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El plato principal es obligatorio.");
                return;
            }
            String c1 = (String) cbContorno1.getSelectedItem();
            try {
                Plato p = new Plato((String) cbPlatoPrincipal.getSelectedItem(), c1,
                        (String) cbContorno2.getSelectedItem(),
                        (String) cbBebida.getSelectedItem(), (String) cbPostre.getSelectedItem(),
                        Integer.parseInt(txtInsumos.getText()), Integer.parseInt(txtDisponibilidad.getText()));

                mapaPlatos.computeIfAbsent(diaSel, k -> new HashMap<>()).put(turnoActual, p);
                mapaTarjetas.get(diaSel).actualizarInfo(p);

                double total = catalogoPlatosPrincipales.getOrDefault((String) cbPlatoPrincipal.getSelectedItem(), 0.0)
                        +
                        catalogoContornos.getOrDefault(c1, 0.0) +
                        catalogoContornos.getOrDefault((String) cbContorno2.getSelectedItem(), 0.0) +
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
        if (!loadCatalogFromDisk()) {
            catalogoPlatosPrincipales = new LinkedHashMap<>();
            catalogoPlatosPrincipales.put("", 0.0);
            catalogoContornos = new LinkedHashMap<>();
            catalogoContornos.put("", 0.0);
            catalogoBebidas = new LinkedHashMap<>();
            catalogoBebidas.put("", 0.0);
            catalogoPostres = new LinkedHashMap<>();
            catalogoPostres.put("", 0.0);
            saveCatalogToDisk();
        }
    }

    private void updateCatalogUI() {
        String selPlato = (String) cbPlatoPrincipal.getSelectedItem();
        String selContorno1 = (String) cbContorno1.getSelectedItem();
        String selContorno2 = (String) cbContorno2.getSelectedItem();
        String selBebida = (String) cbBebida.getSelectedItem();
        String selPostre = (String) cbPostre.getSelectedItem();

        cbPlatoPrincipal.removeAllItems();
        cbContorno1.removeAllItems();
        cbContorno2.removeAllItems();
        cbBebida.removeAllItems();
        cbPostre.removeAllItems();

        for (String item : catalogoPlatosPrincipales.keySet()) {
            cbPlatoPrincipal.addItem(item);
        }
        for (String item : catalogoContornos.keySet()) {
            cbContorno1.addItem(item);
            cbContorno2.addItem(item);
        }
        for (String item : catalogoBebidas.keySet()) {
            cbBebida.addItem(item);
        }
        for (String item : catalogoPostres.keySet()) {
            cbPostre.addItem(item);
        }

        if (selPlato != null && catalogoPlatosPrincipales.containsKey(selPlato))
            cbPlatoPrincipal.setSelectedItem(selPlato);
        if (selContorno1 != null && catalogoContornos.containsKey(selContorno1))
            cbContorno1.setSelectedItem(selContorno1);
        if (selContorno2 != null && catalogoContornos.containsKey(selContorno2))
            cbContorno2.setSelectedItem(selContorno2);
        if (selBebida != null && catalogoBebidas.containsKey(selBebida))
            cbBebida.setSelectedItem(selBebida);
        if (selPostre != null && catalogoPostres.containsKey(selPostre))
            cbPostre.setSelectedItem(selPostre);
    }

    private void saveCatalogToDisk() {
        try {
            Path file = resolveSavePath("catalogo_save.txt");
            if (file.getParent() != null)
                Files.createDirectories(file.getParent());
            List<String> lines = new ArrayList<>();
            for (Map.Entry<String, Double> e : catalogoPlatosPrincipales.entrySet())
                lines.add("PLATO\t" + e.getKey() + "\t0.0\t0.0\t1.0\t0.0\t" + e.getValue());
            for (Map.Entry<String, Double> e : catalogoContornos.entrySet())
                lines.add("CONTORNO\t" + e.getKey() + "\t0.0\t0.0\t1.0\t0.0\t" + e.getValue());
            for (Map.Entry<String, Double> e : catalogoBebidas.entrySet())
                lines.add("BEBIDA\t" + e.getKey() + "\t0.0\t0.0\t1.0\t0.0\t" + e.getValue());
            for (Map.Entry<String, Double> e : catalogoPostres.entrySet())
                lines.add("POSTRE\t" + e.getKey() + "\t0.0\t0.0\t1.0\t0.0\t" + e.getValue());
            Files.write(file, lines, java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean loadCatalogFromDisk() {
        try {
            Path file = resolveSavePath("catalogo_save.txt");
            if (!Files.exists(file))
                return false;

            Map<String, Double> cPlatosPrincipales = new LinkedHashMap<>();
            cPlatosPrincipales.put("", 0.0);
            Map<String, Double> cContornos = new LinkedHashMap<>();
            cContornos.put("", 0.0);
            Map<String, Double> cBebidas = new LinkedHashMap<>();
            cBebidas.put("", 0.0);
            Map<String, Double> cPostres = new LinkedHashMap<>();
            cPostres.put("", 0.0);

            List<String> lines = Files.readAllLines(file, java.nio.charset.StandardCharsets.UTF_8);
            for (String l : lines) {
                if (l.trim().isEmpty())
                    continue;
                String[] p = l.split("\t", -1);
                if (p.length >= 7 && !p[1].trim().isEmpty()) {
                    try {
                        double price = Double.parseDouble(p[6].replace(",", "."));
                        if (p[0].equalsIgnoreCase("PLATO"))
                            cPlatosPrincipales.put(p[1], price);
                        else if (p[0].equalsIgnoreCase("CONTORNO"))
                            cContornos.put(p[1], price);
                        else if (p[0].equalsIgnoreCase("BEBIDA"))
                            cBebidas.put(p[1], price);
                        else if (p[0].equalsIgnoreCase("POSTRE"))
                            cPostres.put(p[1], price);
                    } catch (NumberFormatException ignored) {
                    }
                } else if (p.length >= 3 && !p[1].trim().isEmpty()) {
                    try {
                        double price = Double.parseDouble(p[2].replace(",", "."));
                        if (p[0].equalsIgnoreCase("PLATO"))
                            cPlatosPrincipales.put(p[1], price);
                        else if (p[0].equalsIgnoreCase("CONTORNO"))
                            cContornos.put(p[1], price);
                        else if (p[0].equalsIgnoreCase("BEBIDA"))
                            cBebidas.put(p[1], price);
                        else if (p[0].equalsIgnoreCase("POSTRE"))
                            cPostres.put(p[1], price);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            catalogoPlatosPrincipales = cPlatosPrincipales;
            catalogoContornos = cContornos;
            catalogoBebidas = cBebidas;
            catalogoPostres = cPostres;
            return true;
        } catch (Exception ex) {
            return false;
        }
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
        return DataPathResolver.resolve(filename);
    }

    private void limpiarCampos() {
        cbPlatoPrincipal.setSelectedIndex(0);
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
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4,
                    radius, radius);
            g2.dispose();
        }
    }
}
