package com.comedorucv.controladores;

import com.comedorucv.modelos.PlatoCosto;
import java.util.HashMap;
import java.util.Map;

public class GestorCostos {
    private Map<String, PlatoCosto> costos;

    public GestorCostos() {
        this.costos = new HashMap<>();
    }

    public void setCosto(String dia, PlatoCosto platoCosto) {
        costos.put(dia, platoCosto);
    }

    public PlatoCosto getCosto(String dia) {
        return costos.get(dia);
    }

    public void modificarCosto(String dia, double nuevoCosto) {
        PlatoCosto p = costos.get(dia);
        if (p != null) {
            p.setCosto(nuevoCosto);
        }
    }
}
