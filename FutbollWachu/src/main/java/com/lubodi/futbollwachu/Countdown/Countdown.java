package com.lubodi.futbollwachu.Countdown;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import com.lubodi.futbollwachu.team.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Countdown extends BukkitRunnable {
    private FutballBola minigame;
    private Arena arenas;
    private int countdownSeconds;


    public Countdown(FutballBola minigame, Arena arenas) {
        this.minigame = minigame;
        this.arenas = arenas;
        this.countdownSeconds = ConfigManager.getCountDownSeconds();
    }

    public void start() {
        arenas.setState(GameState.COUNTDOWN);
        runTaskTimer(minigame, 1, 20);
        for (UUID uuid : arenas.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                arenas.checkAndTeleportPlayer(player);
            }
        }

    }

    @Override
    public void run() {
        if(countdownSeconds == 0){
            cancel();
            arenas.Start();
            arenas.startCountdownGame();
            arenas.sendTitle("","");
            arenas.showScoreboardToPlayersInRadius(200);
            return;
        }
        if(countdownSeconds <= 10 || countdownSeconds % 15 == 0){
            arenas.sendmessage(ChatColor.GREEN + "Game will start in" + countdownSeconds + " second" + (countdownSeconds == 1 ? "" :"s") + ".");
        }
        arenas.sendTitle(ChatColor.GREEN.toString() + countdownSeconds + " second" + (countdownSeconds == 1 ? "" :"s"), ChatColor.GRAY + "until game start" );
        countdownSeconds--;
    }
}
