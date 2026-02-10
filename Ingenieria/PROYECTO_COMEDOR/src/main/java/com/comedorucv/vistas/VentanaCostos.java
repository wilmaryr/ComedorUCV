package com.comedorucv.vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import com.comedorucv.controladores.GestorCostos;
import com.comedorucv.modelos.PlatoCosto;

public class VentanaCostos extends JFrame {
    private GestorCostos gestor;
    private JComboBox<String> cbDia;
    private JTextField txtNombre, txtCosto;
    private Map<String, TarjetaCosto> mapaTarjetas;

    public VentanaCostos(GestorCostos gestor) {
        if (gestor == null)
            this.gestor = new GestorCostos();
        else
            this.gestor = gestor;
        mapaTarjetas = new HashMap<>();

        setTitle("Gestionar costos");
        setSize(900, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initComponentes();
    }

    public GestorCostos getGestor() {
        return gestor;
    }

    private void initComponentes() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbDia = new JComboBox<>(new String[] { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" });
        txtNombre = new JTextField(16);
        txtCosto = new JTextField(10);

        añadirCampo(panelForm, "Dia:", cbDia, 0, 0, gbc);
        añadirCampo(panelForm, "Plato:", txtNombre, 0, 1, gbc);
        añadirCampo(panelForm, "Costo:", txtCosto, 2, 1, gbc);

        JButton btnAgregar = new JButton("Guardar");
        btnAgregar.setBackground(new Color(0, 51, 102));
        btnAgregar.setForeground(Color.WHITE);
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        panelForm.add(btnAgregar, gbc);

        JPanel panelCards = new JPanel(new GridLayout(1, 5, 10, 0));
        String[] dias = { "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES" };
        for (String d : dias) {
            TarjetaCosto t = new TarjetaCosto(d);
            mapaTarjetas.put(d, t);
            panelCards.add(t);

            t.getBtnModificar().addActionListener(e -> {
                PlatoCostoWrapper wrapper = PlatoCostoWrapper.getInstance(gestor, mapaTarjetas);
                wrapper.modificarDesdeTarjeta(d);
            });
        }

        btnAgregar.addActionListener(e -> {
            String dia = (String) cbDia.getSelectedItem();
            try {
                PlatoCosto p = new PlatoCosto(txtNombre.getText(), Double.parseDouble(txtCosto.getText()));
                gestor.setCosto(dia, p);
                mapaTarjetas.get(dia).actualizarInfo(p);
                txtNombre.setText("");
                txtCosto.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Costo debe ser un número.");
            }
        });

        add(panelForm, BorderLayout.NORTH);
        add(panelCards, BorderLayout.CENTER);
    }

    private void añadirCampo(JPanel p, String text, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0.0;
        p.add(new JLabel(text), gbc);
        gbc.gridx = x + 1;
        gbc.weightx = 1.0;
        if (comp instanceof JTextField || comp instanceof JComboBox) {
            comp.setPreferredSize(new Dimension(200, 26));
        }
        p.add(comp, gbc);
        gbc.weightx = 0.0;
    }

    private static class PlatoCostoWrapper {
        private GestorCostos gestor;
        private Map<String, TarjetaCosto> mapa;

        private PlatoCostoWrapper(GestorCostos gestor, Map<String, TarjetaCosto> mapa) {
            this.gestor = gestor;
            this.mapa = mapa;
        }

        public static PlatoCostoWrapper getInstance(GestorCostos gestor, Map<String, TarjetaCosto> mapa) {
            return new PlatoCostoWrapper(gestor, mapa);
        }

        public void modificarDesdeTarjeta(String dia) {
            PlatoCosto p = gestor.getCosto(dia);
            if (p != null) {
                String nuevo = JOptionPane.showInputDialog("Nuevo costo para " + p.getNombre(), p.getCosto());
                try {
                    double val = Double.parseDouble(nuevo);
                    gestor.modificarCosto(dia, val);
                    mapa.get(dia).actualizarInfo(gestor.getCosto(dia));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Costo inválido");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No hay plato definido para ese día.");
            }
        }
    }
}
