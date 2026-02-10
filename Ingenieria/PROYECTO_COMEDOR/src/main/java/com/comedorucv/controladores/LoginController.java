package com.comedorucv.controladores;

import com.comedorucv.modelos.*;
import com.comedorucv.vistas.*;
import javax.swing.*;

public class LoginController {

    private UsuarioDAO dao = new UsuarioDAO();

    public void login(String correo, String password, JFrame ventanaLogin) {

        if (correo.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos");
            return;
        }

        Usuario u = dao.login(correo, password);

        if (u != null) {
            JOptionPane.showMessageDialog(null, "Bienvenido " + u.getNombre() + "\nSesión como: " + u.getRol());
            ventanaLogin.dispose();

            if (u.getRol().equals("Administrador")) {
                new AdministradorFrame(u).setVisible(true);
            } else {
                new ComensalFrame(u).setVisible(true);
            }

        } else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos", "Error de Acceso",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean verificarUsuario(String cedula, String correo) {
        if (cedula.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos");
            return false;
        }
        return dao.verificarUsuario(cedula, correo);
    }

    public boolean actualizarContrasena(String cedula, String nuevaPass) {
        if (nuevaPass.length() < 8) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener mínimo 8 caracteres");
            return false;
        }
        return dao.actualizarContrasena(cedula, nuevaPass);
    }
}
