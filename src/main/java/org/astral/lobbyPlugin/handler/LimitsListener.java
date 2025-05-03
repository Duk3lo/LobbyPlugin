package org.astral.lobbyPlugin.handler;

import org.astral.lobbyPlugin.utils.Actions;
import org.astral.lobbyPlugin.utils.Limits;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

public final class LimitsListener implements Listener {
    @EventHandler
    public void onChunkLoad(final @NotNull ChunkLoadEvent event) {
        final Chunk chunk = event.getChunk();
        Limits.updateChunk(chunk);
    }

    @EventHandler
    public void onPlayerMove(final @NotNull PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (Limits.isOutsideLimits(player)) {
            Actions.goToSpawn(player);
        }
    }
}
