package org.astral.lobbyPlugin.handler;

import org.astral.lobbyPlugin.config.Configuration;
import org.astral.lobbyPlugin.utils.Actions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class CommonListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Actions.onJoinPlayer(player);
        Actions.playerFly(player);
    }

    @EventHandler
    public void onPlayerDamage(final @NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.hasPermission("LobbyPlugin.damage")) {
                event.setCancelled(true);
            }
            final EntityDamageEvent.DamageCause cause = event.getCause();
            if (cause.equals(EntityDamageEvent.DamageCause.VOID)) {
                Actions.goToSpawn(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("LobbyPlugin.interaction") || Configuration.isPlayerInteractionAllowed(player.getName())) {
            final Block clicked = event.getClickedBlock();
            if (clicked == null) return;

            final Material type = clicked.getType();
            final String name = type.name();

            if ((name.contains("DOOR") && !Configuration.getInteractionOptionValue("open_doors")) ||
                    (name.contains("TRAPDOOR") && !Configuration.getInteractionOptionValue("open_trapdoors")) ||
                    (name.contains("FENCE_GATE") && !Configuration.getInteractionOptionValue("open_fence_gates")) ||
                    (name.contains("BUTTON") && !Configuration.getInteractionOptionValue("use_buttons")) ||
                    (type == Material.LEVER && !Configuration.getInteractionOptionValue("use_levers"))) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("LobbyPlugin.interaction") || Configuration.isPlayerInteractionAllowed(player.getName())){
            if (!Configuration.getInteractionOptionValue("break_blocks")){
                event.setCancelled(true);
            }
        }else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("LobbyPlugin.interaction") || Configuration.isPlayerInteractionAllowed(player.getName())) {
            if (!Configuration.getInteractionOptionValue("add_blocks")) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteractEntity(final @NotNull PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();

        if (event.getRightClicked() instanceof ItemFrame) {
            if (player.hasPermission("LobbyPlugin.interaction") || Configuration.isPlayerInteractionAllowed(player.getName())) {
                if (!Configuration.getInteractionOptionValue("interact_item_frames")) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        Actions.exitLobby(player);
        //noinspection deprecation
        event.setQuitMessage(null);
    }
}
