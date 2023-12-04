package com.lubodi.futbollwachu;




import com.lubodi.futbollwachu.BolaFisicasYMetodos.Fisicas;

import com.lubodi.futbollwachu.BolaFisicasYMetodos.Metodos;
import com.lubodi.futbollwachu.Commands.ArenaCommand;
import com.lubodi.futbollwachu.HabilidadesFutbol.Commands.ComandoFutbol;
import com.lubodi.futbollwachu.HabilidadesFutbol.GUI.HabilidadGUI;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.HabilidadesFutbol.Listeners.HabilidadHandClickListener;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Listeners.ConnectListener;
import com.lubodi.futbollwachu.Listeners.GameListener;
import com.lubodi.futbollwachu.Manager.ArenaManager;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FutballBola extends JavaPlugin {
    private static FutballBola instance;
    private Fisicas fisicas;
    private ArenaManager arenaManager;
    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.setupConfig(this);
        arenaManager = new ArenaManager(this);
        fisicas = new Fisicas();
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new Metodos(this), this);
        getCommand("futbol").setExecutor(new ComandoFutbol(this));
        // Register events.
        getServer().getPluginManager().registerEvents(new ConnectListener(  this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new HabilidadHandClickListener(this, HabilidadesManager.getInstance()), this);
        getServer().getPluginManager().registerEvents(new HabilidadGUI(this, HabilidadesManager.getInstance()),this);
        getCommand("arena").setExecutor(new ArenaCommand(this));
    }
    public Fisicas getFisicas() {
        return fisicas;
    }
    public ArenaManager getArenaManager(){return  arenaManager; };

    public static FutballBola getInstance() {
        return instance;
    }
}
