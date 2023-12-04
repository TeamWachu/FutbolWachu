package com.lubodi.futbollwachu.BolaFisicasYMetodos;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


import java.util.HashMap;
import java.util.Map;


public class Fisicas {

    private final Map<Entity, BukkitRunnable> bolaTasks = new HashMap<>();
    private final Map<Entity, Double> fallVelocities = new HashMap<>();
    HashMap<Entity, Integer> rebotesPorEntidad = new HashMap<>();

    public void aplicarFriccion(Entity entity) {

            if (entity.getLocation().getY() % 1 < 0.00001) {
                Vector friction = entity.getVelocity();
                if (friction.length() > 0) { // Check if the velocity is not zero
                    friction.normalize();
                    friction.multiply(0.70);
                    entity.setVelocity(friction);
                }
            }
        }

    public void calcularVelocidadHorizontal(Entity entity) {
        Vector velocity = velocidades.get(entity);
        if (velocity == null) {
            velocity = new Vector(0, 0, 0);
        }

        // Asume que la velocidad horizontal es 0.1 bloques/tick
        double horizontalSpeed = 0.1;
        double newHorizontalSpeedX = velocity.getX() + horizontalSpeed;
        double newHorizontalSpeedZ = velocity.getZ() + horizontalSpeed;

        velocity.setX(newHorizontalSpeedX);
        velocity.setZ(newHorizontalSpeedZ);
        velocidades.put(entity, velocity);
    }




    public void calcularFallVelocity(Entity entity) {
        // Si la entidad está en el aire
        if (entity.getLocation().getY() % 1 >= 0.00001) {
            Double accumulatedFallVelocity = fallVelocities.get(entity);
            if (accumulatedFallVelocity == null) {
                accumulatedFallVelocity = 0.0;
            }
            double fallVelocity = accumulatedFallVelocity + entity.getFallDistance(); // Calcula la velocidad de caída
            fallVelocities.put(entity, fallVelocity); // Acumula la velocidad de caída
        }
    }




    public void aplicarRebote(Entity entity) {
        // Verificar si la entidad está cerca del suelo
        if (entity.getLocation().getY() % 1 < 0.00001) {
            Double velocidadCaida = fallVelocities.get(entity);
            if (velocidadCaida > 1.0) {
                double factorRebote = Math.sqrt(velocidadCaida) * 0.3; // Usamos la raíz cuadrada de la velocidad de caída
                // Reducir la velocidad de caída para el próximo rebote
                fallVelocities.put(entity, factorRebote);

                // Aplicar la velocidad modificada a la entidad en el eje Y (vertical)
                entity.setVelocity(entity.getVelocity().setY(factorRebote));
            }
        }
    }



        // ... (otras partes del código)



    // Crea un HashMap para almacenar las velocidades de las entidades
    private final Map<Entity, Vector> velocidades = new HashMap<>();

    public void detectarColisionYAplicarRebote(Entity entity) {
        World w = entity.getWorld();
        Location l = entity.getLocation();

        // Obtiene la velocidad actual de la entidad del HashMap
        Vector velocity = velocidades.get(entity);

        // Si la velocidad es 0, entonces no hay necesidad de comprobar las colisiones
        if (velocity.length() <= 0.7) {
            return;
        }

        velocity.normalize();
        velocity.multiply(0.30);
        velocidades.put(entity, velocity);
        // Comprueba si el bloque en la dirección positiva del eje X es sólido
        if (!(w.getBlockAt(l.getBlockX() + 1, l.getBlockY(), l.getBlockZ()).getType() == Material.AIR)) {
            velocity.setX(-velocity.getX()); // Invierte la velocidad en el eje X
            entity.setVelocity(velocity);
        }

        // Comprueba si el bloque en la dirección negativa del eje X es sólido
        if (!(w.getBlockAt(l.getBlockX() - 1, l.getBlockY(), l.getBlockZ()).getType() == Material.AIR)) {
            velocity.setX(-velocity.getX()); // Invierte la velocidad en el eje X
            entity.setVelocity(velocity);
        }

        // Comprueba si el bloque en la dirección positiva del eje Z es sólido
        if (!(w.getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() + 1).getType() == Material.AIR)) {
            velocity.setZ(-velocity.getZ()); // Invierte la velocidad en el eje Z
            entity.setVelocity(velocity);
        }

        // Comprueba si el bloque en la dirección negativa del eje Z es sólido
        if (!(w.getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ() - 1).getType() == Material.AIR)) {
            velocity.setZ(-velocity.getZ()); // Invierte la velocidad en el eje Z
            entity.setVelocity(velocity);
        }

        // Aplica la nueva velocidad a la entidad
    }







    public void agregarSilverfishVelocidadVertical(Entity entity) {
        velocidades.put(entity, new Vector(0.0, 0.0, 0.0)); // Inicializa la velocidad de caída a 0
        }
    public void agregarSilverfishVelocidadCaida(Entity entity) {
            fallVelocities.put(entity, 0.0); // Inicializa la velocidad de caída a 0
        }

    public void agregarTask(Entity entity, BukkitRunnable task) {
        bolaTasks.put(entity, task);
    }

    public void  quitarAgregarSilverfishVelocidadVertical(Entity entity) {
            velocidades.remove(entity);
        }

    public void quitarSilverfishVelocidadCaida(Entity entity) {
            fallVelocities.remove(entity);

    }
    public void quitarTask(Entity entity) {
        BukkitRunnable task = bolaTasks.get(entity);
        if (task != null) {
            task.cancel();
            bolaTasks.remove(entity);
        }
    }

}
