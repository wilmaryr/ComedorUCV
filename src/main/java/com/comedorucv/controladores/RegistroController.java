package com.comedorucv.controladores;

import com.comedorucv.modelos.*;
import javax.swing.*;

public class RegistroController {

    private UsuarioDAO dao = new UsuarioDAO();

    public boolean registrar(Usuario u) {

        if (u.getCedula().isEmpty() || u.getCorreo().isEmpty() || u.getPassword().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            return false;
        }

        // Validación: Verificar si el usuario ya está registrado por cédula
        if (dao.buscarPorCedula(u.getCedula()) != null) {
            JOptionPane.showMessageDialog(null, "La cédula ingresada ya se encuentra registrada en el sistema.",
                    "Usuario Existente", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación: Verificar si el correo ya está en uso
        if (dao.buscarPorCorreo(u.getCorreo()) != null) {
            JOptionPane.showMessageDialog(null, "El correo electrónico ya se encuentra registrado por otro usuario.",
                    "Correo en Uso", JOptionPane.WARNING_MESSAGE);
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

        if (!u.getCorreo()
                .matches("^[a-zA-Z0-9._%+-]+@(gmail\\.com|hotmail\\.com|yahoo\\.com|outlook\\.com|ucv\\.ve)$")) {
            JOptionPane.showMessageDialog(null,
                    "Formato de correo inválido. Dominios permitidos: @gmail.com, @hotmail.com, @yahoo.com, @outlook.com, @ucv.ve");
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
