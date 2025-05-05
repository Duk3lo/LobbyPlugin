package org.astral.lobbyPlugin.utils;

import org.astral.lobbyPlugin.LobbyPlugin;
import org.astral.lobbyPlugin.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Actions {

    private final static LobbyPlugin plugin = LobbyPlugin.getPlugin();
    private final static Set<UUID> blind = new HashSet<>();

    public static void onJoinPlayer(final @NotNull Player player) {
        // Init
        player.getInventory().setHeldItemSlot(Configuration.getSlotJoin());
        playerConfigureSpeed(player);

        //Spawn difference
        final Location baseSpawn = player.getWorld().getSpawnLocation();
        double offsetRange = 0.5;
        double offsetX = (Math.random() - 0.5) * offsetRange * 2;
        double offsetZ = (Math.random() - 0.5) * offsetRange * 2;
        final Location spawnLocation = baseSpawn.clone().add(offsetX, 0, offsetZ);
        spawnLocation.setYaw(baseSpawn.getYaw());
        player.teleportAsync(spawnLocation);
    }

    public static void playerConfigureSpeed(final @NotNull Player player){
        player.setWalkSpeed(Configuration.getSpeedWalkPlayers());
    }

    public static void playerConfigureFly(final @NotNull Player player){
        if (canFly(player)) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.setFlySpeed(Configuration.speedFly());
        }else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    public static void goToSpawn(final @NotNull Player player) {
        final Location location = player.getLocation();
        final World world = player.getWorld();
        Bukkit.getRegionScheduler().run(plugin, location, task -> player.teleportAsync(world.getSpawnLocation()));
    }

    public static void blindPlayer(final @NotNull Player player) {
        if (blind.contains(player.getUniqueId())) {
            blind.remove(player.getUniqueId());
            for (final Player p : player.getServer().getOnlinePlayers())
                if (!p.equals(player))
                    player.showPlayer(plugin, p);
            player.sendMessage("¡Ahora puedes ver a los demás jugadores!");
        } else {
            blind.add(player.getUniqueId());
            for (final Player p : player.getServer().getOnlinePlayers()) {
                if (!p.equals(player))
                    player.hidePlayer(plugin, p);
            }
            player.sendMessage("¡Ahora no puedes ver a los demás jugadores!");
        }
    }

    public static void exitLobby(final @NotNull Player player){
        final UUID uuid = player.getUniqueId();
        blind.remove(uuid);
    }

    public static boolean canFly(final @NotNull Player player){
        boolean hasPermission = player.hasPermission("LobbyPlugin.fly");
        boolean isAllowed = Configuration.isPlayerInteractionAllowed(player.getName());
        boolean canFlyCreative = Configuration.canFlyInCreative();
        boolean isInCreative = player.getGameMode() == GameMode.CREATIVE;
        return hasPermission || isAllowed || (canFlyCreative && isInCreative);
    }

}