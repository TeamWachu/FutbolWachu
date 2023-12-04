package com.lubodi.futbollwachu.Instance;


import com.google.common.collect.TreeMultimap;
import com.lubodi.futbollwachu.BolaFisicasYMetodos.Metodos;
import com.lubodi.futbollwachu.Countdown.Countdown;
import com.lubodi.futbollwachu.Countdown.CountdownEnd;
import com.lubodi.futbollwachu.Countdown.CountdownGame;
import com.lubodi.futbollwachu.FutballBola;
import com.lubodi.futbollwachu.GameState;
import com.lubodi.futbollwachu.HabilidadesFutbol.Interfaces.HabilidadesManager;
import com.lubodi.futbollwachu.Manager.ConfigManager;
import com.lubodi.futbollwachu.Manager.Region;
import com.lubodi.futbollwachu.team.Team;
import jdk.tools.jlink.plugin.Plugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.*;

import java.util.*;


    public class Arena {
        private final HashMap<Team, Region> portero;
        private BossBar bossBar;
        private Scoreboard scoreboard;
        private Objective objective;
        private FutballBola minigame;
        private static Arena instance;
        private int id;
        private HashMap<Team, Region> canchas;
        private Location ballSpawn;
        private Location spawn;
        private HashMap<Team, Region> zones;
        private Game game;
        private Metodos metodos;
        private HashMap<Team, Player> porteros; // Creas un HashMap para almacenar los porteros de cada equipo
        private CountdownGame countdownGame;
        private CountdownEnd countdownEnd;
        private Countdown countdown;
        private GameState state;
        private HashMap<UUID, Team> teams;
        private List<UUID> players = new ArrayList<>();
        private HabilidadesManager habilidades;
        private Player lastHitters;
        private Location speackerGame;
        private Location speackerGoal;

        public Arena(FutballBola minigame, int id,HashMap<Team, Region> portero, HashMap<Team, Region> canchas, Location ballSpawn, Location spawn, HashMap<Team, Region> zones,Location speackerGame, Location speackerGoal) {
            this.id = id;
            this.metodos = Metodos.getInstance(minigame);
            this.minigame = minigame;
            this.portero = portero;
            this.canchas = canchas;
            this.ballSpawn = ballSpawn;
            this.spawn = spawn;
            this.zones = zones;
            this.porteros = new HashMap<>();
            this.teams = new HashMap<>();
            this.speackerGame = speackerGame;
            this.speackerGoal = speackerGoal;
            this.lastHitters = getLastHitters();
            this.state = GameState.RECRUITING;
            this.game = new Game(this);
            this.countdown = new Countdown(minigame, this);
            this.countdownGame = new CountdownGame(minigame, this, game);
            this.countdownEnd = new CountdownEnd(minigame, this, game);
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.objective = this.scoreboard.registerNewObjective("Marcador", "dummy", "Puntos");
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            this.habilidades = HabilidadesManager.getInstance();
            for (Team team : Team.values()) {
                Score score = this.objective.getScore(team.getDisplay() + ":");
                score.setScore(0);  // Puedes establecer aquí el puntaje inicial del equipo si es necesario
            }
            Score timeScore = this.objective.getScore("Tiempo:");
            timeScore.setScore(ConfigManager.getCountDownGameSeconds());
            this.bossBar = Bukkit.createBossBar(ChatColor.GREEN + "Tiempo de partido", BarColor.GREEN, BarStyle.SOLID);
        }

        public void startCountdownGame() {
            countdownGame.start();
        }

        public void startCountdownEndGame() {
            countdownEnd.start();
        }

        public void Start() {;
            game.start();
        }

        public void End() {
            game.end();
        }


        public void addPlayers(Player player) {
            players.add(player.getUniqueId());
            player.teleport(spawn);

            TreeMultimap<Integer, Team> count = TreeMultimap.create();
            for (Team team : Team.values()) {
                count.put(getTeamCount(team), team);
            }
            Team lowest = count.values().iterator().next();
            setTeam(player, lowest);
            player.sendMessage(ChatColor.AQUA + "Se te ha seleccionado al equipo " + lowest.getDisplay());

            if (state == GameState.RECRUITING && players.size() >= ConfigManager.getRequiredPlayers()) {
                countdown.start();
            }

            // Aquí verificas si ya hay un portero en el equipo del jugador
            Team playerTeam = getTeam(player);
            if (playerTeam != null) {
                if (!porteros.containsKey(playerTeam)) {
                    // Si no hay un portero en el equipo, asignas al jugador actual como portero
                    porteros.put(playerTeam, player);
                    player.sendMessage(ChatColor.GREEN + "¡Eres el portero del equipo " + playerTeam.getDisplay() + "!");
                }
            }
        }



        public void reset(boolean kickPlayers) {
            if (kickPlayers) {
                Location loc = ConfigManager.getLobbySpawn();
                for (UUID uuid : players) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.teleport(loc);
                        habilidades.eliminarTodasLasHabilidades(player.getUniqueId());
                        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                    }
                }

                players.clear();
                teams.clear();
            }
            sendTitle("", "");
            state = GameState.RECRUITING;
            if (countdown != null) {
                countdown.cancel();
            }

            bossBar = Bukkit.createBossBar(ChatColor.GREEN + "Tiempo de partido", BarColor.GREEN, BarStyle.SOLID);
            eliminarScoreboard("Marcador");
            objective = scoreboard.registerNewObjective("Marcador", "dummy", "Puntos");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (Team team : Team.values()) {
                Score score = this.objective.getScore(team.getDisplay() + ":");
                score.setScore(0);  // Puedes establecer aquí el puntaje inicial del equipo si es necesario
            }
            Score timeScore = this.objective.getScore("Tiempo:");
            timeScore.setScore(ConfigManager.getCountDownGameSeconds());
            countdownGame = new CountdownGame(minigame, this, game);
            countdownEnd = new CountdownEnd(minigame, this, game);
            countdown = new Countdown(minigame, this);
            game = new Game(this);

        }

        public void removePlayers(Player player) {
            players.remove(player.getUniqueId());
            player.teleport(ConfigManager.getLobbySpawn());
            player.sendTitle("", "");
            removeTeam(player);
            if (state == GameState.COUNTDOWN && players.size() < ConfigManager.getRequiredPlayers()) {
                sendmessage(ChatColor.RED + "No hay suficientes jugadores para jugar.");
                reset(false);
                return;
            }
            if (state == GameState.LIVE && players.size() < ConfigManager.getRequiredPlayers()) {
                sendmessage(ChatColor.RED + "Demasiados jugadores se han salido de la partida.");
                reset(false);
            }
        }

        public void sendmessage(String message) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(message);
                }
            }
        }

        public void sendTitle(String title, String subtitle) {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendTitle(title, subtitle);
                }
            }
        }

        public Game getGame() {
            return game;
        }


        public void setTeam(Player player, Team team) {
            removeTeam(player);
            teams.put(player.getUniqueId(), team);
        }

        public void removeTeam(Player player) {
            if (teams.containsKey(player.getUniqueId())) {
                teams.remove(player.getUniqueId());
            }
        }

        public int getTeamCount(Team team) {
            int amount = 0;
            for (Team t : teams.values()) {
                if (t == team) {
                    amount++;
                }
            }
            return amount;
        }

        public Team getTeam(Player player) {
            return teams.get(player.getUniqueId());
        }

        public HashMap<Team, Region> getZones() {
            return zones;
        }

        public void spawnearbola() {
            Bukkit.getScheduler().runTaskLater(minigame, () -> {
                metodos.spawnSilverfishAtLocation(ballSpawn);
                World world = ballSpawn.getWorld();
               world.spawnParticle(Particle.REDSTONE,
                       ballSpawn,
                       100, // Cantidad de partículas
                       1, 2, 1, // Desplazamiento en X, Y y Z
                       5, // Tamaño de la partícula
                       new Particle.DustOptions(
                               Color.fromRGB(0, 0, 0), // Color RGB (azul)
                               1 // Opacidad
                       )
               );
               world.playSound(ballSpawn, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                for (UUID uuid : players) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        checkAndTeleportPlayer(player);
                    }
                }

                removeRedstoneBlock(speackerGoal);
            }, 120);

        }

        public Team getTeamOfCancha(Entity entity) {
            if (entity instanceof Silverfish && entity.getCustomName().equals("Bola")) {
                for (Map.Entry<Team, Region> entry : this.canchas.entrySet()) {
                    Region cancha = entry.getValue();
                    if (cancha.contains(entity)) {
                        Team team = entry.getKey();

                        return team;
                    }
                }
            }

            return null;
        }
        public Team getTeamOfZona(Player player) {
            Team playerTeam = teams.get(player.getUniqueId());
            for (Map.Entry<Team, Region> entry : this.zones.entrySet()) {
                Team teamZona = entry.getKey();
                Region zona = entry.getValue();
                if (playerTeam == teamZona && zona.contains(player)) {
                    return  teamZona;
                }
            }
            return null;
        }
        public boolean getPorteroOfZona(Player player) {

            Team playerTeam = teams.get(player.getUniqueId());

            Team teamDeZona = getTeamOfZona(player);

            for (Map.Entry<Team, Region> entry : this.portero.entrySet()) {

                if (entry.getValue().contains(player) &&
                        playerTeam == teamDeZona &&
                        isPlayerPortero(player)) {

                    return true;

                }

            }

            return false;

        }

        public  boolean isPlayerPortero(Player player) {
            for (Team team : porteros.keySet()) {
                if (porteros.get(team) == player) {
                    return true;
                }
            }
            return false;
        }
        public Entity getEntityInCancha() {
            for (Map.Entry<Team, Region> entry : this.canchas.entrySet()) {
                Team team = entry.getKey();
                Region cancha = entry.getValue();
                for (Entity entity : Bukkit.getWorld("world").getEntities()) {
                    if (entity instanceof Silverfish && entity.getCustomName().equals("Bola") && cancha.contains(entity)) {
                        System.out.println("La entidad está en la cancha del equipo " + team.getDisplay());
                        return entity;
                    }
                }
            }
            return null;
        }


        public void showScoreboardToPlayersInRadius(int radius) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Player nearbyPlayer : player.getWorld().getPlayers()) {
                    if (player != nearbyPlayer && player.getLocation().distance(nearbyPlayer.getLocation()) <= radius) {
                        player.setScoreboard(scoreboard);
                    }
                }
            }
        }

        public void updateScores(Team team, int score) {
            Score scoreObj = objective.getScore(team.getDisplay() + ":");
            scoreObj.setScore(score);
        }
        public void updateScoresTime(int time) {
            Score timeScore = this.objective.getScore("Tiempo:");
            int numero = timeScore.getScore();
            timeScore.setScore(numero - time);
        }



        public void eliminarScoreboard(String nombreMarcador) {
            if (scoreboard.getObjective(nombreMarcador) != null) {
                scoreboard.getObjective(nombreMarcador).unregister();
            }
        }

        public void addPlayersToBossBar() {
            for (UUID playerUUID : players) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    bossBar.addPlayer(player);
                }
            }
        }

        public void removePlayersFromBossBar() {
            for (UUID playerUUID : players) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    bossBar.removePlayer(player);
                }
            }
        }
        public void teleportToEquivalentLocation(Player player, Region sourceRegion, Region targetRegion) {
            double[][] distancesToCorners = sourceRegion.getDistancesToCorners(player);

            double equivalentX = distancesToCorners[1][0] * (Math.max(targetRegion.getCorner1().getX(), targetRegion.getCorner2().getX()) - Math.min(targetRegion.getCorner1().getX(), targetRegion.getCorner2().getX())) + Math.min(targetRegion.getCorner1().getX(), targetRegion.getCorner2().getX());
            double equivalentY = distancesToCorners[1][1] * (Math.max(targetRegion.getCorner1().getY(), targetRegion.getCorner2().getY()) - Math.min(targetRegion.getCorner1().getY(), targetRegion.getCorner2().getY())) + Math.min(targetRegion.getCorner1().getY(), targetRegion.getCorner2().getY());
            double equivalentZ = distancesToCorners[1][2] * (Math.max(targetRegion.getCorner1().getZ(), targetRegion.getCorner2().getZ()) - Math.min(targetRegion.getCorner1().getZ(), targetRegion.getCorner2().getZ())) + Math.min(targetRegion.getCorner1().getZ(), targetRegion.getCorner2().getZ());

            System.out.println("ubicacion del jugador: " + player.getLocation());
            System.out.println("Calculando ubicación equivalente:");
            System.out.println("Distancias a esquinas: [" + distancesToCorners[1][0] + ", " + distancesToCorners[1][1] + ", " + distancesToCorners[1][2] + "]");
            System.out.println("Equivalente X: " + equivalentX);
            System.out.println("Equivalente Y: " + equivalentY);
            System.out.println("Equivalente Z: " + equivalentZ);

            Location equivalentLocation = new Location(player.getWorld(), equivalentX, equivalentY, equivalentZ);
            System.out.println("Teletransportando al jugador a la ubicación equivalente.");

            player.teleport(equivalentLocation);
            System.out.println("Jugador teletransportado con éxito.");
        }



        public void checkAndTeleportPlayer(Player player) {
            Team playerTeam = teams.get(player.getUniqueId());
            Region playerRegion = null;
            Region teamRegion = null;

            // Buscar la región del jugador y la región del equipo del jugador
            for (Map.Entry<Team, Region> entry : this.zones.entrySet()) {
                if (entry.getValue().contains(player)) {
                    playerRegion = entry.getValue();
                }
                if (entry.getKey().equals(playerTeam)) {
                    teamRegion = entry.getValue();
                }
            }

            System.out.println("Jugador: " + player.getName());
            System.out.println("Equipo del jugador: " + playerTeam);
            System.out.println("Región del jugador: " + playerRegion);
            System.out.println("Región del equipo del jugador: " + teamRegion);

            // Si el jugador está en una región que no es la de su equipo, teletransportarlo
            if (playerRegion != null && teamRegion != null && !playerRegion.equals(teamRegion)) {
                teleportToEquivalentLocation(player, playerRegion, teamRegion);
            }
        }

        public static void SoltarBola(Player player) {
            FutballBola plugin = FutballBola.getInstance();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player != null) {
                   player.eject();
                }
            }, 60);
        }

        public void matarSilverfish(Location location, double range) {
            List<Entity> nearbyEntities = (List<Entity>) location.getWorld().getNearbyEntities(location, range, range, range);
            for (Entity entity : nearbyEntities) {
                if (entity.getType() == EntityType.SILVERFISH) {
                    ((LivingEntity) entity).setHealth(0);
                }
            }
        }
        public void placeRedstoneBlock(Location location) {
            // Comprueba si el mundo es válido
            System.out.println(location.toString());
            System.out.println("probando");
          World world = location.getWorld();
            Block block = world.getBlockAt(location);

// Cambia el tipo de bloque
            block.setType(Material.REDSTONE_BLOCK);
        }
        public void removeRedstoneBlock(Location location) {
            // Comprueba si el mundo es válido
            World world = location.getWorld();
            Block block = world.getBlockAt(location);

// Cambia el tipo de bloque
            block.setType(Material.AIR);

        }
        public Location getSpeackerGame() {
            return speackerGame;
        }

        public Location getSpeackerGoal() {
            return speackerGoal;
        }

        public void setLastHitters(Player lastHitters) {
            this.lastHitters = lastHitters;
        }

        public Player getLastHitters() {
            return lastHitters;
        }

        public void updateBossBar(int value) {
            bossBar.setProgress(value / 100.0);
        }
        public List<UUID> getPlayers() {
            return players;
        }

        public int getId() {
            return id;
        }

        public HashMap<Team, Region> getCanchas() {
            return canchas;
        }

        public Location getBallSpawn() {
            return ballSpawn;
        }

        public Location getSpawn() {
            return spawn;
        }

        public GameState getState() {
            return state;
        }
        public void setState(GameState state) {
            this.state = state;
        }

    }

