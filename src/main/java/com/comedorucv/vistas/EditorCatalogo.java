package com.comedorucv.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.comedorucv.utils.DataPathResolver;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class EditorCatalogo extends JDialog {
    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color BORDER_BLUE = new Color(0, 119, 181);

    private JTable table;
    private DefaultTableModel tableModel;

    public EditorCatalogo(JFrame parent) {
        super(parent, "Editar Catálogo", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        initUI();
        loadData();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Editor de Catálogo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        String[] cols = { "Tipo", "Nombre", "CF ($)", "CV ($)", "NB", "% Merma", "CCB" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 6; // CCB is read-only
            }
        };
        tableModel.addTableModelListener(new javax.swing.event.TableModelListener() {
            private boolean isUpdating = false;

            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                if (isUpdating)
                    return;
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    int col = e.getColumn();
                    if (col >= 2 && col <= 5) {
                        int row = e.getFirstRow();
                        try {
                            double cf = parseDoubleDef(tableModel.getValueAt(row, 2));
                            double cv = parseDoubleDef(tableModel.getValueAt(row, 3));
                            double nb = parseDoubleDef(tableModel.getValueAt(row, 4));
                            double merma = parseDoubleDef(tableModel.getValueAt(row, 5));

                            if (nb > 0) {
                                double ccb = ((cf + cv) / nb) * (1 + (merma / 100.0));
                                isUpdating = true;
                                tableModel.setValueAt(String.format(java.util.Locale.US, "%.2f", ccb), row, 6);
                                isUpdating = false;
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        });
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JComboBox<String> cbTipo = new JComboBox<>(new String[] { "PLATO", "CONTORNO", "BEBIDA", "POSTRE" });
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cbTipo));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        RoundedButton btnAdd = new RoundedButton("Agregar Fila", PRIMARY_COLOR, Color.WHITE, 20);
        RoundedButton btnDel = new RoundedButton("Eliminar Fila", new Color(220, 53, 69), Color.WHITE, 20);
        RoundedButton btnSave = new RoundedButton("Guardar", new Color(40, 167, 69), Color.WHITE, 20);
        RoundedButton btnCancel = new RoundedButton("Cancelar", Color.GRAY, Color.WHITE, 20);

        Dimension btnDim = new Dimension(120, 35);
        btnAdd.setPreferredSize(btnDim);
        btnDel.setPreferredSize(btnDim);
        btnSave.setPreferredSize(btnDim);
        btnCancel.setPreferredSize(btnDim);

        btnAdd.addActionListener(
                e -> tableModel.addRow(new Object[] { "PLATO", "", "0.0", "0.0", "1.0", "0.0", "0.0" }));

        btnDel.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        btnSave.addActionListener(this::saveData);
        btnCancel.addActionListener(e -> dispose());

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnDel);
        bottomPanel.add(btnSave);
        bottomPanel.add(btnCancel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private double parseDoubleDef(Object obj) {
        if (obj == null || obj.toString().trim().isEmpty())
            return 0.0;
        try {
            return Double.parseDouble(obj.toString().trim().replace(",", "."));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void loadData() {
        try {
            Path file = resolveSavePath("catalogo_save.txt");
            if (!Files.exists(file))
                return;
            List<String> lines = Files.readAllLines(file, java.nio.charset.StandardCharsets.UTF_8);
            for (String l : lines) {
                if (l.trim().isEmpty())
                    continue;
                String[] p = l.split("\t", -1);
                if (p.length >= 7 && !p[1].trim().isEmpty()) {
                    tableModel.addRow(new Object[] { p[0], p[1], p[2], p[3], p[4], p[5], p[6] });
                } else if (p.length >= 3 && !p[1].trim().isEmpty()) {
                    tableModel.addRow(new Object[] { p[0], p[1], "0.0", "0.0", "1.0", "0.0", p[2] });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveData(ActionEvent ev) {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
        try {
            Path file = resolveSavePath("catalogo_save.txt");
            if (file.getParent() != null)
                Files.createDirectories(file.getParent());
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String tipo = tableModel.getValueAt(i, 0).toString().trim();
                String nombre = tableModel.getValueAt(i, 1).toString().trim();
                String cf = tableModel.getValueAt(i, 2).toString().trim().replace(",", ".");
                String cv = tableModel.getValueAt(i, 3).toString().trim().replace(",", ".");
                String nb = tableModel.getValueAt(i, 4).toString().trim().replace(",", ".");
                String merma = tableModel.getValueAt(i, 5).toString().trim().replace(",", ".");
                String ccb = tableModel.getValueAt(i, 6).toString().trim().replace(",", ".");

                if (nombre.isEmpty())
                    continue;
                try {
                    double vCf = Double.parseDouble(cf);
                    double vCv = Double.parseDouble(cv);
                    double vNb = Double.parseDouble(nb);
                    double vMerma = Double.parseDouble(merma);
                    double vCcb = Double.parseDouble(ccb);

                    if (vCf < 0 || vCv < 0 || vNb <= 0 || vMerma < 0 || vCcb < 0) {
                        JOptionPane.showMessageDialog(this,
                                "No se permiten valores negativos (ni divisor nulo) en la fila " + (i + 1),
                                "Error de Valores", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Error de num. en la fila " + (i + 1));
                    return;
                }
                lines.add(String.join("\t", tipo, nombre, cf, cv, nb, merma, ccb));
            }
            Files.write(file, lines, java.nio.charset.StandardCharsets.UTF_8);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar el catálogo.");
        }
    }

    private Path resolveSavePath(String filename) {
        return DataPathResolver.resolve(filename);
    }
}
