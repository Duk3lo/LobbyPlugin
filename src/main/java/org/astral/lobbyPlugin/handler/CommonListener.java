package org.astral.lobbyPlugin.handler;

import org.astral.lobbyPlugin.config.Configuration;
import org.astral.lobbyPlugin.utils.Actions;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class CommonListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        //noinspection deprecation
        event.setJoinMessage(null);
        final Player player = event.getPlayer();
        Actions.onJoinPlayer(player);
        Actions.playerConfigureFly(player);
    }

    @EventHandler
    public void onFoodLevelChange(final @NotNull FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(final @NotNull EntityDamageEvent event) {
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
    public void onInteractEntity(final @NotNull PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        if (event.getRightClicked() instanceof ItemFrame) {
            if (!canInteract(player, "interact_item_frames")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDestroyEntity(final @NotNull HangingBreakByEntityEvent event){
        final Entity remover = event.getRemover();
        final Entity entity = event.getEntity();
        if (remover instanceof Player player && entity instanceof ItemFrame) {
            if (!canInteract(player, "interact_item_frames")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        final Material type = clicked.getType();
        final String name = type.name();
        if (name.contains("DOOR") && !canInteract(player, "open_doors") ||
                (name.contains("TRAPDOOR") && !canInteract(player, "open_trapdoors")) ||
                (name.contains("FENCE_GATE") && !canInteract(player, "open_fence_gates")) ||
                (name.contains("BUTTON") && !canInteract(player, "use_buttons")) ||
                (type == Material.LEVER && !canInteract(player, "use_levers"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(final @NotNull BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (!canInteract(player, "break_blocks")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(final @NotNull BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (!canInteract(player, "add_blocks")){
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        Actions.exitLobby(player);
        //noinspection deprecation
        event.setQuitMessage(null);
    }

    private boolean canInteract(final @NotNull Player player, final String optionKey) {
        boolean hasPermission = player.hasPermission("LobbyPlugin.interaction");
        boolean canInteractCreative = Configuration.canInteractCreative();
        boolean isAllowed = Configuration.isPlayerInteractionAllowed(player.getName());
        boolean optionEnabled = Configuration.getInteractionOptionValue(optionKey);
        boolean isInCreative = player.getGameMode() == GameMode.CREATIVE;
        return hasPermission || (canInteractCreative && isInCreative) || (isAllowed && optionEnabled);
    }
}
