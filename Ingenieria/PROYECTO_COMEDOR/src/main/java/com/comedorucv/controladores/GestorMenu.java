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
        menuSemanal.put(dia, plato);
    }

    public Plato obtenerPlato(String dia) {
        return menuSemanal.get(dia);
    }

    public void eliminarPlato(String dia) {
        menuSemanal.remove(dia);
    }
}
