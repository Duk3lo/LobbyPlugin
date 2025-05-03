package org.astral.lobbyPlugin.command;

import org.astral.lobbyPlugin.config.Configuration;
import org.astral.lobbyPlugin.utils.Actions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Commands implements CommandExecutor , TabCompleter {

    private static final String blind = "blind";
    private static final String reload = "reload";
    private static final String interaction = "interaction";
    private static final String allow_interact = "allow_interact_players";
    private static final String allow_fly = "allow_fly_player";

    private static final String add = "add";
    private static final String remove = "remove";
    private static final String set_options = "set_option";

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = null;
        if ((commandSender instanceof Player)){
            player = ((Player) commandSender);
        }

        if (player == null)return false;

        if (strings.length >= 1){
            switch (strings[0]) {
                case blind -> {
                    Actions.blindPlayer(player);
                    return true;
                }
                case reload -> {
                    if (player.hasPermission("LobbyPlugin.reload")) {
                        Configuration.updatePlugin();
                        player.sendMessage("§aPlugin recargado correctamente.");
                    } else {
                        player.sendMessage("§cNo tienes permiso para usar este comando.");
                    }
                    return true;
                }
                case interaction -> {
                    if (strings.length >= 2) {
                        if (strings[1].equals(set_options)) {
                            if (strings.length >= 3) {
                                String optionName = strings[2];

                                if (!Configuration.getInteractionOptions().containsKey(optionName)) {
                                    player.sendMessage("§cOpción no válida: " + optionName);
                                    return true;
                                }

                                if (strings.length == 3) {
                                    boolean currentValue = Configuration.getInteractionOptionValue(optionName);
                                    player.sendMessage("La opción §e" + optionName + "§r está actualmente " + (currentValue ? "§aactivada" : "§cdesactivada"));
                                    return true;
                                }


                                if (strings.length == 4) {
                                    String valueStr = strings[3];
                                    if (valueStr.equalsIgnoreCase("true") || valueStr.equalsIgnoreCase("false")) {
                                        boolean newValue = Boolean.parseBoolean(valueStr);
                                        Configuration.setInteractionOptionValue(optionName, newValue);
                                        player.sendMessage("§aLa opción §e" + optionName + "§a ha sido " + (newValue ? "§aactivada" : "§cdesactivada") + "§a correctamente.");
                                    } else {
                                        player.sendMessage("§cValor inválido. Usa 'true' o 'false'.");
                                    }
                                    return true;
                                }
                            } else {
                                player.sendMessage("§cUso incorrecto. Usa: /interaction set_option <opción> [true|false]");
                                return true;
                            }
                        } else {
                            player.sendMessage("§cSubcomando no válido. Usa: set_option");
                            return true;
                        }
                    }
                }
            }

            if (strings.length >= 2) {
                String targetName = strings.length >= 3 ? strings[2] : player.getName();
                Player target = Bukkit.getPlayerExact(targetName); // para aplicar efectos si está conectado

                if (strings[0].equals(allow_fly)) {
                    if (strings[1].equals(add)) {
                        if (Configuration.isPlayerFlyAllowed(targetName)) {
                            player.sendMessage("§cEl jugador §e" + targetName + " §ya puede volar.");
                        } else {
                            Configuration.addAllowedFlyPlayer(targetName);
                            if (target != null) Actions.playerFly(target);
                            player.sendMessage("§aEl jugador §e" + targetName + " §aahora puede volar.");
                        }
                        return true;
                    } else if (strings[1].equals(remove)) {
                        if (!Configuration.isPlayerFlyAllowed(targetName)) {
                            player.sendMessage("§cEl jugador §e" + targetName + " §cno tiene permiso de vuelo.");
                        } else {
                            Configuration.removeAllowedFlyPlayer(targetName);
                            if (target != null) Actions.playerFly(target);
                            player.sendMessage("§cEl jugador §e" + targetName + " §cya no puede volar.");
                        }
                        return true;
                    }
                }

                if (strings[0].equals(allow_interact)) {
                    if (strings[1].equals(add)) {
                        if (Configuration.isPlayerInteractionAllowed(targetName)) {
                            player.sendMessage("§cEl jugador §e" + targetName + " §yya puede interactuar.");
                        } else {
                            Configuration.addAllowedInteractionPlayer(targetName);
                            player.sendMessage("§aEl jugador §e" + targetName + " §aahora puede interactuar.");
                        }
                        return true;
                    } else if (strings[1].equals(remove)) {
                        if (!Configuration.isPlayerInteractionAllowed(targetName)) {
                            player.sendMessage("§cEl jugador §e" + targetName + " §cno tiene permisos de interacción.");
                        } else {
                            Configuration.removeAllowedInteractionPlayer(targetName);
                            player.sendMessage("§cEl jugador §e" + targetName + " §cya no puede interactuar.");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public @Unmodifiable @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1){
            return List.of(blind, reload, allow_interact, interaction, allow_fly);
        }
        if (args.length == 2) {
            if (args[0].equals(allow_interact) || args[0].equals(allow_fly)) {
                return List.of(add, remove);
            }

            if (args[0].equals(interaction)){
                return List.of(set_options);
            }
        }

        if (args.length == 3) {
            if ((args[0].equals(allow_fly) || args[0].equals(allow_interact)) && (args[1].equals(add) || args[1].equals(remove))) {
                List<String> allowedPlayers = args[0].equals(allow_fly)
                        ? Configuration.getAllowedFlyPlayers()
                        : Configuration.getAllowedInteractionPlayers();

                if (args[1].equals(add)) {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(playerName -> !allowedPlayers.contains(playerName))
                            .collect(Collectors.toList());
                } else {
                    return allowedPlayers;
                }
            }

            if (args[0].equals(interaction) && args[1].equals(set_options)) {
                return new ArrayList<>(Configuration.getInteractionOptions().keySet());
            }
        }

        if (args.length == 4){
            if (args[0].equals(interaction) && args[1].equals(set_options) && Configuration.getInteractionOptions().containsKey(args[2])) {
                boolean currentValue = Configuration.getInteractionOptionValue(args[2]);
                return List.of(String.valueOf(!currentValue));
            }
        }

        return List.of();
    }
}