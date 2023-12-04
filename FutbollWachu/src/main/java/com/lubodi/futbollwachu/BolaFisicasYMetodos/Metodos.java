package com.lubodi.futbollwachu.BolaFisicasYMetodos;


import com.lubodi.futbollwachu.FutballBola;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;




public class Metodos implements Listener {



    private final Fisicas fisicas;
    final private FutballBola plugin;
    private static Metodos instance = null;

    public static Metodos getInstance(FutballBola plugin) {
        if (instance == null) {
            instance = new Metodos(plugin);
        }
        return instance;
    }
    public Metodos(FutballBola plugin) {
        this.plugin = plugin;
        this.fisicas = new Fisicas();
    }
    public  void spawnSilverfishAtLocation(Location location) {
        // Crea el Silverfish pacífico en la ubicación proporcionada
        Silverfish silverfish = (Silverfish) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SILVERFISH);

        // Aplica lentitud al Silverfish (opcional)
        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200));
        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 200));
        // Hace al Silverfish inmortal
        silverfish.setCustomName("Bola");
        silverfish.setCustomNameVisible(true);
        silverfish.setSilent(true);
        fisicas.agregarSilverfishVelocidadCaida(silverfish);
        fisicas.agregarSilverfishVelocidadVertical(silverfish);

        // Aplica las físicas a la "Bola"
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                fisicas.aplicarFriccion(silverfish);
                fisicas.calcularFallVelocity(silverfish); // Calcula y almacena la velocidad de caída
                fisicas.calcularVelocidadHorizontal(silverfish);
                fisicas.detectarColisionYAplicarRebote(silverfish);
                fisicas.aplicarRebote(silverfish);


            }
        };
        task.runTaskTimer(plugin, 0, 4); // Ejecuta el task cada tick
        fisicas.agregarTask(silverfish, task);
    }

    public void matarBola(Entity entity) {
        if (entity.getCustomName() != null && entity.getCustomName().equals("Bola")) {
            entity.remove();
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntityType() == EntityType.SILVERFISH && event.getTarget() != null) {
            Silverfish silverfish = (Silverfish) event.getEntity();
            if ("Bola".equals(silverfish.getCustomName())) {
                event.setCancelled(true); // Cancela el evento solo si el Silverfish tiene el nombre personalizado "Bola"
            }
        }
    }
    @EventHandler
    public  void  onEntityDeath(EntityDeathEvent event){
        if(event.getEntityType() == EntityType.SILVERFISH && event.getEntity().getCustomName() != null){
            if("Bola".equals(event.getEntity().getCustomName())){
               fisicas.quitarTask(event.getEntity());
               fisicas.quitarSilverfishVelocidadCaida(event.getEntity());
               fisicas.quitarAgregarSilverfishVelocidadVertical(event.getEntity());
            }
        }
    }

    @EventHandler
    public  void  onEntityDeath(EntityDamageEvent event){
        if(event.getEntityType() == EntityType.SILVERFISH && event.getEntity().getCustomName() != null){
            if("Bola".equals(event.getEntity().getCustomName())){
                if(event.getCause() == EntityDamageEvent.DamageCause.FALL){
                  event.setCancelled(true);
                }

            }
        }
    }

}
