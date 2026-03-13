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
        if (dia != null && !dia.trim().isEmpty() && platoCosto != null && platoCosto.getCosto() >= 0) {
            costos.put(dia, platoCosto);
        }
    }

    public PlatoCosto getCosto(String dia) {
        if (dia == null || dia.trim().isEmpty())
            return null;
        return costos.get(dia);
    }

    public void modificarCosto(String dia, double nuevoCosto) {
        if (nuevoCosto < 0 || dia == null || dia.trim().isEmpty())
            return;
        PlatoCosto p = costos.get(dia);
        if (p != null) {
            p.setCosto(nuevoCosto);
        }
    }
}
