package com.comedorucv.controladores;

import java.util.HashMap;
import java.util.Map;
import com.comedorucv.modelos.Plato;

public class GestorMenu {
    private Map<String, Plato> menuSemanal;

    public GestorMenu() {
        this.menuSemanal = new HashMap<>();
    }

    public void agregarPlato(String dia, Plato plato) {
        if (dia != null && !dia.trim().isEmpty() && plato != null) {
            menuSemanal.put(dia, plato);
        }
    }

    public Plato obtenerPlato(String dia) {
        if (dia == null || dia.trim().isEmpty())
            return null;
        return menuSemanal.get(dia);
    }

    public void eliminarPlato(String dia) {
        menuSemanal.remove(dia);
    }
}
