package com.lubodi.futbollwachu.HabilidadesFutbol.Habilidades;


import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.Habilidad;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public enum Fuerza implements Habilidad {
    Fuerza10(ChatColor.RED + "Fuerza 10", Material.ORANGE_WOOL, 10, 50, "Fuerza"),  // Cambiado "Fuerza 10" a "Tiro"
    FUERZA20(ChatColor.RED+"Fuerza 20", Material.ORANGE_CONCRETE_POWDER, 80, 15, "Fuerza"),
    FUERZA30(ChatColor.RED +"Fuerza 30", Material.ORANGE_CONCRETE, 30, 100, "Fuerza");

    private String nombre;
    private Material material;
    private int cooldown;
    private double potencia;
    private String etiqueta;  // Nuevo campo

    Fuerza(String nombre, Material material, int cooldown, double potencia, String etiqueta) {
        this.nombre = nombre;
        this.material = material;
        this.cooldown = cooldown;
        this.potencia = potencia;
        this.etiqueta = etiqueta;  // Inicializar el nuevo campo
    }

    public double getPotencia() {
        return potencia;
    }

    @Override
    public String getEtiqueta() {
        return etiqueta;
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
    public void usar(UUID uuid) {
        // Obtén el jugador a partir del UUID.
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
                        Color.fromRGB(255, 0, 0), // Color RGB (verde)
                        1 // Opacidad
                )
        );
        // Comprueba si el jugador existe (es decir, está en línea).
        if (jugador != null) {
            // Obtiene todas las entidades cercanas al jugador.
            List<Entity> entidadesCercanas = jugador.getNearbyEntities(5, 5, 5);

            // Busca un Silverfish entre las entidades cercanas.
            for (Entity entidad : entidadesCercanas) {
                if (entidad instanceof Silverfish) {
                    Silverfish silverfish = (Silverfish) entidad;

                    // Comprueba si el Silverfish tiene el nombre "bola".
                    if ("Bola".equals(silverfish.getCustomName())) {
                        // Crea un vector en la dirección en la que el jugador está mirando.
                        Vector direccion = jugador.getLocation().getDirection();

                        // Aplica la potencia de la habilidad al vector.


                        // Lanza el Silverfish en esa dirección.
                        direccion.multiply(this.potencia);
                        silverfish.setVelocity(direccion);
                    }
                }
            }
        }
    }


}

