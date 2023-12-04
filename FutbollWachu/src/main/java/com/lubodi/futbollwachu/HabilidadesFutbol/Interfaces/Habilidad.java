package com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces;

import org.bukkit.Material;

import java.util.UUID;

public interface Habilidad {
    String getNombre();
    Material getMaterial();
    int getCooldown();
    void usar(UUID uuid);
    String getEtiqueta();
}
