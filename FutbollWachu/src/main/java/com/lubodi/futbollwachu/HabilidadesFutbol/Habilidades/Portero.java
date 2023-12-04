package com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades;

import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import com.lubodi.futbollwachu.Instance.Arena;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public enum Portero implements Habilidad {
    PORTEROHabilidad1(ChatColor.BLUE + "AgarreMáximo", Material.BLUE_WOOL, 5, "Agarre"),
    PORTEROHabilidad2(ChatColor.BLUE + "RechazeMáximo", Material.BLUE_CONCRETE_POWDER, 5, "Agarre");

    private String nombre;
    private Material material;
    private int cooldown;
    private String etiqueta;

    Portero(String nombre, Material material, int cooldown, String etiqueta) {
        this.nombre = nombre;
        this.material = material;
        this.cooldown = cooldown;
        this.etiqueta = etiqueta;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public void usar(UUID uuid) {
        Player jugador = Bukkit.getPlayer(uuid);
        Location jugadorLocations = jugador.getLocation();
        World world = jugador.getWorld();
        world.playSound(jugadorLocations, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
        world.spawnParticle(Particle.REDSTONE,
                jugadorLocations,
                100, // Cantidad de partículas
                1, 1, 1, // Desplazamiento en X, Y y Z
                1, // Tamaño de la partícula
                new Particle.DustOptions(
                        Color.fromRGB(0, 0, 255), // Color RGB (verde)
                        1 // Opacidad
                )
        );
        List<Entity> entidadesCercanas = jugador.getNearbyEntities(10, 10, 5);

        // Busca un Silverfish entre las entidades cercanas.
        for (Entity entidad : entidadesCercanas) {
            if (entidad instanceof Silverfish) {
                Silverfish silverfish = (Silverfish) entidad;

                if (jugador != null) {
                    switch (this){
                        case PORTEROHabilidad1:
                            jugador.addPassenger(silverfish);
                            Arena.SoltarBola(jugador);
                            break;
                        case PORTEROHabilidad2:
                            Vector vectorHaciaSilverfish = silverfish.getLocation().toVector().subtract(jugadorLocations.toVector());

                            // Normalizarlo y multiplicar por fuerza deseada
                            vectorHaciaSilverfish.normalize().multiply(3);

                            // Aplicar vector de empuje al Silverfish
                            silverfish.setVelocity(vectorHaciaSilverfish);
                            break;
                    }

                }

            }
        }
    }
}
