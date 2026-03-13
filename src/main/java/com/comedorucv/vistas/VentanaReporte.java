package com.comedorucv.vistas;

import com.comedorucv.modelos.Usuario;
import com.comedorucv.utils.DataPathResolver;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;

public class VentanaReporte extends JFrame {

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color TABLE_HEADER_COLOR = new Color(0, 80, 120);

    private Usuario usuario;
    private boolean isDesayuno = true;
    private DefaultTableModel tableModel;
    private JLabel lblDetalleTipos;
    private JLabel lblFechaElab;
    private JLabel lblSemanaRango;

    public VentanaReporte(Usuario u) {
        this.usuario = u;
        setTitle("Comedor UCV - Reporte");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
        cargarDatos();
    }

    private void initUI() {
        // Header al estilo VentanaListaComensales
        JPanel headerPanel = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        headerPanel.setLayout(new OverlayLayout(headerPanel));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(1000, 85));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Partes laterales (Logo e Inicio / Reloj y Cerrar Sesión)
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftNav.setOpaque(false);
        leftNav.add(new LogoPlaceholder());

        JLabel lblInicio = createNavLink("Inicio");
        lblInicio.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AdministradorFrame(usuario).setVisible(true);
                dispose();
            }
        });
        leftNav.add(lblInicio);

        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        navPanel.setOpaque(false);

        JLabel lblLogout = createNavLink("Cerrar sesión");
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

        JPanel sidesPanel = new JPanel(new BorderLayout());
        sidesPanel.setOpaque(false);
        sidesPanel.add(leftNav, BorderLayout.WEST);
        sidesPanel.add(navContainer, BorderLayout.EAST);

        // Título central "COMEDOR UCV"
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        titlePanel.setOpaque(false);
        JLabel mainTitleLabel = new JLabel("COMEDOR UCV");
        mainTitleLabel.setForeground(Color.WHITE);
        mainTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(mainTitleLabel);
        
        // Alinear para OverlayLayout
        titlePanel.setAlignmentX(0.5f);
        titlePanel.setAlignmentY(0.5f);
        sidesPanel.setAlignmentX(0.5f);
        sidesPanel.setAlignmentY(0.5f);

        headerPanel.add(titlePanel);
        headerPanel.add(sidesPanel);
        add(headerPanel, BorderLayout.NORTH);

        // Contenido Principal con GridBagLayout para evitar el "cuadro grande"
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Top section of content (Title, Toggle, Dates, Download)
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);

        JPanel titleAndToggle = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        titleAndToggle.setOpaque(false);

        JLabel lblTitle = new JLabel("Reporte");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(PRIMARY_COLOR);
        titleAndToggle.add(lblTitle);

        JPanel togglePanel = new JPanel();
        togglePanel.setLayout(new BoxLayout(togglePanel, BoxLayout.Y_AXIS));
        togglePanel.setOpaque(false);

        JLabel lblDesayuno = new JLabel("Desayuno");
        lblDesayuno.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDesayuno.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblAlmuerzo = new JLabel("Almuerzo");
        lblAlmuerzo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblAlmuerzo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Runnable updateToggle = () -> {
            if (isDesayuno) {
                lblDesayuno.setForeground(TABLE_HEADER_COLOR);
                lblDesayuno.setText("<html><u>Desayuno</u></html>");
                lblAlmuerzo.setForeground(Color.DARK_GRAY);
                lblAlmuerzo.setText("Almuerzo");
            } else {
                lblDesayuno.setForeground(Color.DARK_GRAY);
                lblDesayuno.setText("Desayuno");
                lblAlmuerzo.setForeground(TABLE_HEADER_COLOR);
                lblAlmuerzo.setText("<html><u>Almuerzo</u></html>");
            }
        };
        updateToggle.run();

        lblDesayuno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isDesayuno = true;
                updateToggle.run();
                cargarDatos();
            }
        });

        lblAlmuerzo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isDesayuno = false;
                updateToggle.run();
                cargarDatos();
            }
        });

        togglePanel.add(lblDesayuno);
        togglePanel.add(Box.createVerticalStrut(5));
        togglePanel.add(lblAlmuerzo);
        
        titleAndToggle.add(togglePanel);
        
        JPanel datesPanel = new JPanel();
        datesPanel.setLayout(new BoxLayout(datesPanel, BoxLayout.Y_AXIS));
        datesPanel.setOpaque(false);
        lblFechaElab = new JLabel("Fecha de elaboración: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblFechaElab.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSemanaRango = new JLabel("Semana: del DD/MM al DD/MM del AAAA");
        lblSemanaRango.setFont(new Font("Segoe UI", Font.BOLD, 14));
        datesPanel.add(lblFechaElab);
        datesPanel.add(Box.createVerticalStrut(5));
        datesPanel.add(lblSemanaRango);
        
        JPanel centerPanelTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        centerPanelTop.setOpaque(false);
        centerPanelTop.add(datesPanel);

        JButton btnDownload = new JButton("\u2B07"); 
        btnDownload.setFont(new Font("Segoe UI", Font.BOLD, 30));
        btnDownload.setForeground(Color.BLACK);
        btnDownload.setContentAreaFilled(false);
        btnDownload.setBorderPainted(false);
        btnDownload.setFocusPainted(false);
        btnDownload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDownload.setToolTipText("Descargar Reporte");

        topSection.add(titleAndToggle, BorderLayout.WEST);
        topSection.add(centerPanelTop, BorderLayout.CENTER);
        topSection.add(btnDownload, BorderLayout.EAST);

        // Top section adding
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        contentPanel.add(topSection, gbc);

        // Tabla
        String[] columnNames = {"DIA DE LA SEMANA", "PLATOS PROPUESTOS", "PLATOS DESPACHADOS", "PLATOS SOBRANTES"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        JTableHeader th = table.getTableHeader();
        th.setPreferredSize(new Dimension(100, 40));
        th.setBackground(TABLE_HEADER_COLOR);
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ((DefaultTableCellRenderer)th.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer firstColumnRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                c.setBackground(new Color(245, 245, 245));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 0) {
                table.getColumnModel().getColumn(i).setCellRenderer(firstColumnRenderer);
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Panel para envolver tabla y su cabecera
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.add(th, BorderLayout.NORTH);
        tableContainer.add(table, BorderLayout.CENTER);
        tableContainer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 15, 0);
        contentPanel.add(tableContainer, gbc);

        // Resumen Footer (Desagregación)
        JPanel footerInfo = new JPanel(new GridLayout(1, 1));
        footerInfo.setBackground(BACKGROUND_COLOR);
        footerInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR), "Desagregación por Tipo de Comensal", 0, 0, new Font("Segoe UI", Font.BOLD, 14), PRIMARY_COLOR));
        
        lblDetalleTipos = new JLabel("Cargando datos...");
        lblDetalleTipos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDetalleTipos.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        footerInfo.add(lblDetalleTipos);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(footerInfo, gbc);

        // Espaciador flexible al final para empujar todo hacia arriba
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        contentPanel.add(new Box.Filler(new Dimension(0,0), new Dimension(0,0), new Dimension(0, 32767)), gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Barra decorativa inferior (más delgada, 15px)
        JPanel bottomDeco = new JPanel();
        bottomDeco.setBackground(PRIMARY_COLOR);
        bottomDeco.setPreferredSize(new Dimension(1000, 15));
        add(bottomDeco, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        String turnoStr = isDesayuno ? "DESAYUNO" : "ALMUERZO";

        // Determinar rango de la semana (Lunes a Viernes)
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate viernes = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        lblSemanaRango.setText("Semana: del " + lunes.format(DateTimeFormatter.ofPattern("dd/MM")) + 
                               " al " + viernes.format(DateTimeFormatter.ofPattern("dd/MM")) + 
                               " del " + lunes.getYear());

        // Map<Día, Object[]> -> [0:Propuestos, 1:Disponibles, 2:Items (String)]
        Map<String, Object[]> menuData = getMenuData(turnoStr); 
        Map<String, Map<String, Integer>> consumosPorDiaYTipo = getConsumosSemana(lunes, viernes, turnoStr);

        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};
        int totalProp = 0;
        int totalDesp = 0;
        int totalSobr = 0;

        for (String dia : dias) {
            Object[] stats = menuData.getOrDefault(dia, new Object[]{0, 0, ""});
            int prop = (int) stats[0];
            int sobr = (int) stats[1]; 
            int desp = Math.max(0, prop - sobr); 

            tableModel.addRow(new Object[]{capitalizar(dia), prop, desp, sobr});
            
            totalProp += prop;
            totalDesp += desp;
            totalSobr += sobr;
        }

        tableModel.addRow(new Object[]{"Total de la semana", totalProp, totalDesp, totalSobr});

        // Detalle por tipo (basado en registros de acceso real)
        StringBuilder detalle = new StringBuilder("<html>");
        Map<String, Integer> totalesPorTipo = new HashMap<>();
        for (Map<String, Integer> consumosDia : consumosPorDiaYTipo.values()) {
            for (Map.Entry<String, Integer> entry : consumosDia.entrySet()) {
                String tipo = entry.getKey();
                totalesPorTipo.put(tipo, totalesPorTipo.getOrDefault(tipo, 0) + entry.getValue());
            }
        }

        detalle.append("<b>Desglose Semanal por Tipo de Usuario:</b><br/>");
        String[] rolesFijos = {"Estudiante", "Estudiante Exonerado", "Estudiante Becario", "Profesor", "Empleado"};
        
        for (String rol : rolesFijos) {
            int cantidad = totalesPorTipo.getOrDefault(rol, 0);
            detalle.append("&nbsp;&bull;&nbsp;").append(rol).append(": ").append(cantidad).append("<br/>");
        }
        
        detalle.append("</html>");
        lblDetalleTipos.setText(detalle.toString());
    }

    private Map<String, Object[]> getMenuData(String turno) {
        Map<String, Object[]> map = new HashMap<>();
        try {
            File menuFile = DataPathResolver.resolve("menu_save.txt").toFile();
            if (menuFile.exists()) {
                List<String> lines = Files.readAllLines(menuFile.toPath());
                for (String l : lines) {
                    if (l.trim().isEmpty()) continue;
                    String[] parts = l.split("\t");
                    if (parts.length >= 9 && parts[1].equalsIgnoreCase(turno)) {
                        int propuestos = Integer.parseInt(parts[7]);
                        int disponibles = Integer.parseInt(parts[8]);
                        
                        // Recopilar items del menu (Plato, Contornos, Bebida, Postre)
                        List<String> itemsList = new ArrayList<>();
                        for (int i = 2; i <= 6; i++) {
                            if (i < parts.length && !parts[i].trim().isEmpty()) {
                                itemsList.add(parts[i].trim());
                            }
                        }
                        String items = String.join(", ", itemsList);
                        
                        map.put(parts[0].toUpperCase(), new Object[]{propuestos, disponibles, items});
                    }
                }
            }
        } catch (Exception e) {}
        return map;
    }

    private Map<String, Map<String, Integer>> getConsumosSemana(LocalDate inicio, LocalDate fin, String turno) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        try {
            File consumosFile = DataPathResolver.resolve("consumos_comedor.txt").toFile();
            if (consumosFile.exists()) {
                List<String> lines = Files.readAllLines(consumosFile.toPath());
                for (String l : lines) {
                    if (l.trim().isEmpty()) continue;
                    String[] p = l.split("\t");
                    if (p.length >= 6) {
                        try {
                            LocalDate fechaConsumo = LocalDate.parse(p[0]);
                            String turnoConsumo = p[4];
                            String rol = p[3];

                            if (!fechaConsumo.isBefore(inicio) && !fechaConsumo.isAfter(fin) && turnoConsumo.equalsIgnoreCase(turno)) {
                                String dia = fechaConsumo.getDayOfWeek().toString();
                                // Traducir día
                                switch (dia) {
                                    case "MONDAY": dia = "LUNES"; break;
                                    case "TUESDAY": dia = "MARTES"; break;
                                    case "WEDNESDAY": dia = "MIERCOLES"; break;
                                    case "THURSDAY": dia = "JUEVES"; break;
                                    case "FRIDAY": dia = "VIERNES"; break;
                                    default: continue;
                                }

                                result.putIfAbsent(dia, new HashMap<>());
                                Map<String, Integer> mapDia = result.get(dia);
                                mapDia.put(rol, mapDia.getOrDefault(rol, 0) + 1);
                            }
                        } catch (Exception ex) {}
                    }
                }
            }
        } catch (Exception e) {}
        return result;
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private JLabel createNavLink(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return lbl;
    }
}
