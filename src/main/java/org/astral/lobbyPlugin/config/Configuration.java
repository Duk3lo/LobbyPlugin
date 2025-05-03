package org.astral.lobbyPlugin.config;

import org.astral.lobbyPlugin.LobbyPlugin;
import org.astral.lobbyPlugin.handler.LimitsListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Configuration {

    private static LobbyPlugin plugin;
    private static FileConfiguration config;


    private final static LimitsListener LIMITS_LISTENER = new LimitsListener();

    private final static String keyFly = "fly";

    private final static String keyInteraction = "interaction";
    private final static String keyInteractOption = "interaction.options";
    private final static String keyChunkSpawn = "limits.chunk";

    private static boolean isLimitsHandler = false;

    public static void init(final @NotNull LobbyPlugin plugin) {
        Configuration.plugin = plugin;
        final File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveDefaultConfig();
        }
        config = plugin.getConfig();

        registerEvents();
    }

    private static void registerEvents(){
        if (isChunkLimitsEnabled()){
            plugin.getServer().getPluginManager().registerEvents(LIMITS_LISTENER, plugin);
            isLimitsHandler = true;
        }
    }

    public static void updatePlugin(){
        plugin.reloadConfig();
        config = plugin.getConfig();
        if (Configuration.isChunkLimitsEnabled() && !isLimitsHandler) {
            plugin.getServer().getPluginManager().registerEvents(LIMITS_LISTENER, plugin);
            isLimitsHandler = true;
        }else {
            HandlerList.unregisterAll(LIMITS_LISTENER);
            isLimitsHandler = false;
        }
    }

    //-------------------------FLY

    public static @NotNull List<String> getAllowedFlyPlayers(){
        return config.getStringList(keyFly + ".allowed-players");
    }

    public static void addAllowedFlyPlayer(final String name) {
        List<String> list = getAllowedFlyPlayers();
        if (!list.contains(name)) {
            list.add(name);
            config.set(keyFly + ".allowed-players", list);
            plugin.saveConfig();
            plugin.reloadConfig();
            config = plugin.getConfig();
        }
    }

    public static void removeAllowedFlyPlayer(final String name) {
        List<String> list = getAllowedFlyPlayers();
        if (list.contains(name)) {
            list.remove(name);
            config.set(keyFly + ".allowed-players", list);
            plugin.saveConfig();
            plugin.reloadConfig();
            config = plugin.getConfig();
        }
    }

    public static boolean isPlayerFlyAllowed(final String name) {
        return getAllowedFlyPlayers().contains(name);
    }

    public static float speedFly() {
        return (float) config.getDouble(keyFly + ".speed", 0.2f);
    }

    //-------------------------INTERACTION ALLOWED INTERACTION

    public static @NotNull List<String> getAllowedInteractionPlayers() {
        return config.getStringList(keyInteraction + ".allowed-players");
    }

    public static void addAllowedInteractionPlayer(final String name) {
        List<String> list = getAllowedInteractionPlayers();
        if (!list.contains(name)) {
            list.add(name);
            config.set(keyInteraction + ".allowed-players", list);
            plugin.saveConfig();
            plugin.reloadConfig();
            config = plugin.getConfig();
        }
    }

    public static void removeAllowedInteractionPlayer(final String name) {
        List<String> list = getAllowedInteractionPlayers();
        if (list.contains(name)) {
            list.remove(name);
            config.set(keyInteraction + ".allowed-players", list);
            plugin.saveConfig();
            plugin.reloadConfig();
            config = plugin.getConfig();
        }
    }

    public static boolean isPlayerInteractionAllowed(final String name) {
        return getAllowedInteractionPlayers().contains(name);
    }

    //-------------------------INTERACTION-OPTION
    public static @NotNull Map<String, Boolean> getInteractionOptions() {
        Map<String, Boolean> options = new HashMap<>();

        Objects.requireNonNull(config.getConfigurationSection(keyInteractOption)).getKeys(false).forEach(key -> {
            boolean value = config.getBoolean(keyInteractOption + "." + key, false);
            options.put(key, value);
        });

        return options;
    }

    public static boolean getInteractionOptionValue(final @NotNull String option) {
        return config.getBoolean(keyInteractOption + "." + option, true);
    }

    public static void setInteractionOptionValue(final @NotNull String option, final boolean value) {
        config.set(keyInteractOption + "." + option, value);
        plugin.saveConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    //-------------------------CHUNKS - limits


    public static boolean isChunkLimitsEnabled() {
        return config.getBoolean(keyChunkSpawn + ".enabled", true);
    }

    public static int getViewRadiusChunk() {
        return config.getInt(keyChunkSpawn + ".view-radius", 2);
    }

    public static int getMaxUpChunk() {
        return config.getInt(keyChunkSpawn + ".max-up", 160);
    }

    public static int getMaxDownChunk() {
        return config.getInt(keyChunkSpawn + ".max-down", 32);
    }
}