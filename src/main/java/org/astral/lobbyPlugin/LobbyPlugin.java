package org.astral.lobbyPlugin;

import org.astral.lobbyPlugin.command.Commands;
import org.astral.lobbyPlugin.config.Configuration;
import org.astral.lobbyPlugin.handler.CommonListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class LobbyPlugin extends JavaPlugin {

    private static LobbyPlugin lobbyPlugin;

    @Override
    public void onEnable() {
        lobbyPlugin = this;

        Configuration.init(this);

        final Commands commands = new Commands();
        final CommonListener commonListener = new CommonListener();

        final PluginCommand pluginCommand = Objects.requireNonNull(getCommand("lobby"));
        pluginCommand.setAliases(List.of("lb"));
        pluginCommand.setExecutor(commands);



        getServer().getPluginManager().registerEvents(commonListener, this);


        getLogger().info("Hello World");
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye");
    }

    public static LobbyPlugin getPlugin(){ return lobbyPlugin; }
}
