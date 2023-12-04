package com.lubodi.futbollwachu.Countdown;

import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.Instance.Arena;
import com.lubodi.futbollwachu.Instance.Game;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
public class CountdownEnd extends BukkitRunnable {
    private FutballBola minigame;
    private Arena arenas;
    private int countdownSeconds;

    private Game game;
    public CountdownEnd(FutballBola minigame, Arena arenas, Game game) {
        this.minigame = minigame;
        this.arenas = arenas;
        this.countdownSeconds = ConfigManager.getCountDownEndSeconds();
        this.game = game;
    }

    public void start() {
        arenas.setState(GameState.COUNTDOWN);
        runTaskTimer(minigame, 1, 20);
    }

    @Override
    public void run() {
        if (countdownSeconds == 0) {
            cancel();
            arenas.End();
            arenas.removeRedstoneBlock(arenas.getSpeackerGame());
            return;
        }
        countdownSeconds--;
    }
}

