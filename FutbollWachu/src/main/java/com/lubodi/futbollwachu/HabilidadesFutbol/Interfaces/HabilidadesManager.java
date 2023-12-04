package com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces;

// Clase que administra las habilidades disponibles
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Fuerza;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Portero;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Regate;
import com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades.Velocidad;
import com.lubodi.futbollwachu.Instance.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HabilidadesManager {
    public final Map<UUID, Map<Habilidad, ItemStack>> itemsReemplazo = new HashMap<>();
    public final Map<UUID, Map<Habilidad, ItemStack>> itemsOriginales = new HashMap<>();

    private static Arena arena;
    private static HabilidadesManager instance;
    private Map<UUID, List<Habilidad>> habilidadesJugador = new HashMap<>();
    private Map<UUID, Map<Habilidad, Boolean>> habilidadesActivas = new HashMap<>();


    private Cache<UUID, Long> cooldowns = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)  // Reemplaza 10 con la duración del cooldown en segundos.
            .build();
    private Cache<UUID, Long> cooldownatrapar = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)  // Reemplaza 10 con la duración del cooldown en segundos.
            .build();

    public void iniciarCooldownAtrapar(UUID jugadorId) {
        cooldownatrapar.put(jugadorId, System.currentTimeMillis());
    }

    public boolean estaEnCooldownAtrapar(UUID jugadorId) {
        return cooldownatrapar.getIfPresent(jugadorId) != null;
    }

    public void registrarHabilidad(UUID jugador, Habilidad habilidad) {
        habilidadesJugador.computeIfAbsent(jugador, k -> new ArrayList<>());

        List<Habilidad> habilidades = habilidadesJugador.get(jugador);

        // Evitar duplicados
        if (!habilidades.contains(habilidad)) {
            habilidades.add(habilidad);
        }
    }

    public static HabilidadesManager getInstance() {
        if (instance == null) {
            instance = new HabilidadesManager();
        }
        return instance;
    }

    public List<Habilidad> getHabilidades(UUID jugador) {
        return habilidadesJugador.getOrDefault(jugador, Collections.emptyList());
    }

    public static List<Habilidad> getTodasLasHabilidades() {
        List<Habilidad> todasLasHabilidades = new ArrayList<>();
        todasLasHabilidades.addAll(Arrays.asList(Fuerza.values()));
        todasLasHabilidades.addAll(Arrays.asList(Velocidad.values()));
        todasLasHabilidades.addAll(Arrays.asList(Regate.values()));
        todasLasHabilidades.addAll(Arrays.asList(Portero.values()));


        return todasLasHabilidades;
    }

    public void eliminarHabilidad(UUID jugador, Habilidad habilidad) {
        habilidadesJugador.getOrDefault(jugador, Collections.emptyList()).remove(habilidad);
    }

    public void eliminarTodasLasHabilidades(UUID jugador) {
        habilidadesJugador.remove(jugador);
    }

    public void activarHabilidad(UUID jugadorId, Habilidad habilidad) {
        Map<Habilidad, Boolean> habilidadesJugador = habilidadesActivas.get(jugadorId);
        if (habilidadesJugador == null) {
            habilidadesJugador = new HashMap<>();
            habilidadesActivas.put(jugadorId, habilidadesJugador);
        } else {
            // Desactiva cualquier habilidad activa.
            for (Map.Entry<Habilidad, Boolean> entry : habilidadesJugador.entrySet()) {
                if (entry.getValue()) {
                    entry.setValue(false);
                }
            }
        }
        // Activa la nueva habilidad.
        habilidadesJugador.put(habilidad, true);
    }


    public Habilidad obtenerHabilidadActiva(UUID jugadorId) {
        Map<Habilidad, Boolean> habilidadesJugador = habilidadesActivas.get(jugadorId);
        if (habilidadesJugador != null) {
            for (Map.Entry<Habilidad, Boolean> entry : habilidadesJugador.entrySet()) {
                if (entry.getValue()) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }


    public boolean estaEnCooldown(UUID jugadorId) {
        Long tiempoFinCooldown = cooldowns.getIfPresent(jugadorId);
        if (tiempoFinCooldown != null) {
            return System.currentTimeMillis() < tiempoFinCooldown;
        }
        return false;
    }


    public Habilidad obtenerHabilidadDeItem(ItemStack item) {
        Material material = item.getType();
        for (Habilidad habilidad : HabilidadesManager.getTodasLasHabilidades()) {
            if (habilidad.getMaterial() == material) {
                return habilidad;
            }
        }
        return null;
    }
    public void desactivarHabilidad(UUID jugadorId, Habilidad habilidad) {
        Map<Habilidad, Boolean> habilidadesJugador = habilidadesActivas.get(jugadorId);
        if (habilidadesJugador != null) {
            habilidadesJugador.put(habilidad, false);
        }
    }

}
