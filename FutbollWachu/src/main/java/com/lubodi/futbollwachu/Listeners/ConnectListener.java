package com.lubodi.futbollwachu.Listeners;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectListener implements Listener {



    private FutballBola minigame;
    public ConnectListener(FutballBola minigame){
        this.minigame = minigame;
    }

    @EventHandler
    public void  onJoin(PlayerJoinEvent e){
        e.getPlayer().teleport(ConfigManager.getLobbySpawn());

    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Arena arenas = minigame.getArenaManager().getArena(e.getPlayer());
        if(arenas != null){
            arenas.removePlayers(e.getPlayer());
            HabilidadesManager.getInstance().eliminarTodasLasHabilidades(e.getPlayer().getUniqueId());
        }

    }
}

