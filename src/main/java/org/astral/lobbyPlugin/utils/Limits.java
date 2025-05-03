package org.astral.lobbyPlugin.utils;

import org.astral.lobbyPlugin.LobbyPlugin;
import org.astral.lobbyPlugin.config.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Limits {

    private static final LobbyPlugin plugin = LobbyPlugin.getPlugin();

    private static final int CHUNK_RATIO = Configuration.getViewRadiusChunk();
    private static final int MAX_UP = Configuration.getMaxUpChunk();
    private static final int MAX_DOWN = Configuration.getMaxDownChunk();
    private static final HashMap<Long, Boolean> eChunk = new HashMap<>();

    private static final Chunk spawnChunk = Bukkit.getWorlds().get(0).getSpawnLocation().getChunk();

    public static void updateChunk(final @NotNull Chunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();
        long key = getKey(x, z);

        if (!eChunk.containsKey(key)) {
            World world = chunk.getWorld();
            Bukkit.getRegionScheduler().run(plugin, world, x, z, task -> {
                boolean empty = isEmptyChunk(chunk);
                eChunk.put(key, empty);
            });
        }
    }

    public static boolean isOutsideLimits(final @NotNull Player player) {
        var loc = player.getLocation();
        Chunk chunk = loc.getChunk();
        int cX = chunk.getX();
        int cZ = chunk.getZ();
        long key = getKey(cX, cZ);

        Boolean isEmpty = eChunk.get(key);
        if (isEmpty == null) return false;

        int spawnX = spawnChunk.getX();
        int spawnZ = spawnChunk.getZ();
        int dx = cX - spawnX;
        int dz = cZ - spawnZ;

        boolean outOfRadius = (dx * dx + dz * dz) > (CHUNK_RATIO * CHUNK_RATIO);
        boolean outOfY = loc.getY() > MAX_UP || loc.getY() < MAX_DOWN;

        return (outOfRadius && isEmpty && !nearbyToRegion(cX, cZ)) || outOfY;
    }

    private static boolean nearbyToRegion(int cX, int cZ) {
        for (int dx = -CHUNK_RATIO; dx <= CHUNK_RATIO; dx++) {
            int dzMax = CHUNK_RATIO - Math.abs(dx);
            for (int dz = -dzMax; dz <= dzMax; dz++) {
                long key = getKey(cX + dx, cZ + dz);
                Boolean empty = eChunk.get(key);
                if (empty != null && !empty) return true;
            }
        }
        return false;
    }

    private static boolean isEmptyChunk(Chunk chunk) {
        for (int x = 0; x < 16; x++)
            for (int y = chunk.getWorld().getMinHeight(); y < chunk.getWorld().getMaxHeight(); y++)
                for (int z = 0; z < 16; z++)
                    if (!chunk.getBlock(x, y, z).isEmpty()) return false;
        return true;
    }

    private static long getKey(int x, int z) {
        return (((long) x) << 32) | (z & 0xFFFFFFFFL);
    }
}