package com.lubodi.futbollwachu.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private static FileConfiguration config;

    public static void setupConfig(JavaPlugin plugin) {
        config = plugin.getConfig();
        plugin.saveDefaultConfig();
    }
    public static Location getLobbySpawn(){
        return new Location(
                Bukkit.getWorld(config.getString("lobby-spawn.world")),
                config.getDouble("lobby-spawn.x"),
                config.getDouble("lobby-spawn.y"),
                config.getDouble("lobby-spawn.z"),
                (float) config.getDouble("lobby-spawn.yaw"),
                (float) config.getDouble("lobby-spawn.pitch"));
    }


    public static int getRequiredPlayers() {
        return config.getInt("required-players");
    }
    public static int getMaxPortero() {
        return config.getInt("max-portero");
    }


    public static int getCountDownSeconds() {
        return config.getInt("countdown-seconds");
    }
    public static int getCountDownGameSeconds() {
        return config.getInt("countdownGame-seconds");
    }
    public static int getCountDownEndSeconds() {
        return config.getInt("countdownEnd-seconds");
    }

    private static Location getLocation(String path) {
      return new Location(
              Bukkit.getWorld(config.getString(path + ".world")),
              config.getDouble(path + ".x"),
              config.getDouble(path + ".y"),
              config.getDouble(path + ".z"),
              (float) config.getDouble(path + ".yaw"),
              (float) config.getDouble(path + ".pitch")
      );
    }
}