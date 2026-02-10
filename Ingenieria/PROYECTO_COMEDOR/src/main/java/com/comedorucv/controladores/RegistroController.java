package com.comedorucv.controladores;

import com.comedorucv.modelos.*;
import javax.swing.*;

public class RegistroController {

    private UsuarioDAO dao = new UsuarioDAO();

    public boolean registrar(Usuario u) {

        if (u.getCedula().isEmpty() || u.getNombre().isEmpty() ||
                u.getCorreo().isEmpty() || u.getPassword().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            return false;
        }

        if (!u.getCedula().matches("\\d+")) {
            JOptionPane.showMessageDialog(null, "La cédula solo debe contener números");
            return false;
        }

        if (u.getPassword().length() < 8) {
            JOptionPane.showMessageDialog(null, "La contraseña debe tener mínimo 8 caracteres");
            return false;
        }

        if (!u.getCorreo().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            JOptionPane.showMessageDialog(null, "Formato de correo inválido");
            return false;
        }

        boolean exito = dao.registrar(u);

        if (exito) {
            JOptionPane.showMessageDialog(null, "¡Registro exitoso!\nRol asignado: " + u.getRol());
            return true;
        } else {
            JOptionPane.showMessageDialog(null,
                    "Error: No se encontró la cédula en los registros de Secretaría.\n" +
                            "Solo personal UCV puede registrarse.",
                    "Error de Validación",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
