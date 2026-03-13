package com.comedorucv.vistas;

import com.comedorucv.modelos.Usuario;
import com.comedorucv.modelos.UsuarioDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaListaComensales extends JFrame {

    private final Color PRIMARY_COLOR = new Color(0, 51, 78);
    private final Color BACKGROUND_COLOR = Color.WHITE;

    private JTable tableUsuarios;
    private DefaultTableModel tableModel;
    private UsuarioDAO usuarioDAO;
    private JTextField txtCedula;
    private JComboBox<String> cmbRoles;
    private JComboBox<String> cmbFiltro;
    private Usuario usuarioAdmin;

    public VentanaListaComensales(Usuario admin) {
        this.usuarioAdmin = admin;
        this.usuarioDAO = new UsuarioDAO();

        setTitle("Lista de Comensales - Administrador");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        initUI();
        cargarDatos();
    }

    private void initUI() {
        // Título superior al estilo VentanaPrincipal pero visualmente como AdministradorFrame
        JPanel headerPanel = new JPanel() {
            @Override
            public boolean isOptimizedDrawingEnabled() {
                return false;
            }
        };
        headerPanel.setLayout(new OverlayLayout(headerPanel));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 85));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Partes laterales (Logo e Inicio / Reloj y Cerrar Sesión)
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftNav.setOpaque(false);
        leftNav.add(new LogoPlaceholder());

        JLabel lblInicio = createNavLink("Inicio");
        lblInicio.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new AdministradorFrame(usuarioAdmin).setVisible(true);
                dispose();
            }
        });
        leftNav.add(lblInicio);

        JPanel navContainer = new JPanel(new BorderLayout());
        navContainer.setOpaque(false);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        navPanel.setOpaque(false);

        JLabel lblLogout = createNavLink("Cerrar sesión");
        lblLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (JOptionPane.showConfirmDialog(null, "¿Deseas cerrar sesión?", "Cerrar sesión",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                    new LoginFrame().setVisible(true);
                    dispose();
                }
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

        // Panel de Contenido con título de la vista abajo de la barra azul
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        JPanel subTitleContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        subTitleContainer.setBackground(BACKGROUND_COLOR);
        subTitleContainer.setBorder(BorderFactory.createEmptyBorder(25, 0, 5, 0));
        JLabel lblSubTitle = new JLabel("Lista de comensales");
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblSubTitle.setForeground(PRIMARY_COLOR);
        subTitleContainer.add(lblSubTitle);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.add(new JLabel("Ver por:"));
        cmbFiltro = new JComboBox<>(new String[]{"Totales", "Estudiantes", "Profesores", "Empleados"});
        cmbFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbFiltro.addActionListener(e -> cargarDatos());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        filterPanel.add(cmbFiltro);

        JPanel subTitleWrapper = new JPanel(new BorderLayout());
        subTitleWrapper.setOpaque(false);
        subTitleWrapper.add(subTitleContainer, BorderLayout.NORTH);
        subTitleWrapper.add(filterPanel, BorderLayout.CENTER);

        contentPanel.add(subTitleWrapper, BorderLayout.NORTH);

        // Tabla en el centro
        String[] columnNames = {"Cédula", "Nombre", "Rol"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableUsuarios = new JTable(tableModel);
        tableUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableUsuarios.setRowHeight(25);
        tableUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableUsuarios.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Panel inferior para asignar rol
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pnlBottom.setBackground(BACKGROUND_COLOR);
        pnlBottom.setBorder(BorderFactory.createTitledBorder("Gestionar Roles de Estudiante (Regular, Exonerado o Becario)"));
        ((javax.swing.border.TitledBorder) pnlBottom.getBorder()).setTitleFont(new Font("Segoe UI", Font.BOLD, 14));

        pnlBottom.add(new JLabel("Cédula del Estudiante:"));
        txtCedula = new JTextField(12);
        txtCedula.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlBottom.add(txtCedula);

        pnlBottom.add(new JLabel("Tipo:"));
        cmbRoles = new JComboBox<>(new String[]{"Estudiante", "Estudiante Exonerado", "Estudiante Becario"});
        cmbRoles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlBottom.add(cmbRoles);

        JButton btnActualizar = new JButton("Actualizar Rol");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.setBackground(PRIMARY_COLOR);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarRolEstudiante();
            }
        });
        pnlBottom.add(btnActualizar);

        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        tableModel.setRowCount(0); // Limpiar tabla
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        String filtro = (String) cmbFiltro.getSelectedItem();
        
        for (Usuario u : usuarios) {
            boolean agregar = false;
            String rol = u.getRol().toLowerCase();
            
            if (filtro == null || filtro.equals("Totales")) {
                agregar = true;
            } else if (filtro.equals("Estudiantes") && rol.contains("estudiante")) {
                agregar = true;
            } else if (filtro.equals("Profesores") && rol.contains("profesor")) {
                agregar = true;
            } else if (filtro.equals("Empleados") && rol.contains("empleado")) {
                agregar = true;
            }
            
            if (agregar) {
                Object[] row = {u.getCedula(), u.getNombre(), u.getRol()};
                tableModel.addRow(row);
            }
        }
    }

    private void actualizarRolEstudiante() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una cédula.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nuevoRol = (String) cmbRoles.getSelectedItem();

        boolean exito = usuarioDAO.actualizarRolEstudiante(cedula, nuevoRol);
        if (exito) {
            JOptionPane.showMessageDialog(this, "El rol del estudiante ha sido actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            txtCedula.setText("");
            cargarDatos(); // Refrescar la tabla
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el rol.\nVerifique que la cédula sea correcta y pertenezca a un Estudiante.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createNavLink(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return lbl;
    }
}
