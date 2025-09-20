package me.novoro.cobblemontracker;

import com.mojang.brigadier.CommandDispatcher;
import me.novoro.cobblemontracker.api.configuration.Configuration;
import me.novoro.cobblemontracker.api.configuration.YamlConfiguration;
import me.novoro.cobblemontracker.api.permissions.DefaultPermissionProvider;
import me.novoro.cobblemontracker.api.permissions.LuckPermsPermissionProvider;
import me.novoro.cobblemontracker.api.permissions.PermissionProvider;
import me.novoro.cobblemontracker.commands.TrackerReloadCommand;
import me.novoro.cobblemontracker.config.*;
import me.novoro.cobblemontracker.utils.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CobblemonTracker implements ModInitializer {
    public static final String MOD_PREFIX = "&f ";

    public static CobblemonTracker instance = null;
    private MinecraftServer server = null;
    private PermissionProvider permissionProvider = null;

    private final LangManager langManager = new LangManager();
    private final ModuleManager moduleManager = new ModuleManager();
    private final SettingsManager settingsManager = new SettingsManager();

    @Override
    public void onInitialize() {
        CobblemonTracker.instance = this;

        // novoro signature ;)
        displayAsciiArt();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.checkPermissionProvider();
            this.reloadConfigs();
        });

        // Listener for Player Joins (Relevant for FaintEvent)
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var player = handler.player;
            playerLoginTimes.put(player.getUuid(), System.currentTimeMillis());
        });

        // Reloads modules on startup. Needs to be called before commands are registered.
        moduleManager.reload();

        // Registers all of CobblemonTracker's commands.
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            this.registerCommands(dispatcher);
        });
    }

    // Reloads Configs
    public void reloadConfigs() {
        // Lang
        this.langManager.reload();
        // Settings
        this.settingsManager.reload();
        // ToDo: Reload our *other* configs lol
    }

    /**
     * Register all commands provided by the mod.
     */
    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Reload Command
        new TrackerReloadCommand().register(dispatcher);
    }

    private final Map<UUID, Long> playerLoginTimes = new HashMap<>();

    /**
     * Displays an ASCII Art representation of the mod's name in the log.
     */
    private void displayAsciiArt() {
        CobblemonTrackerLogger.info("\u001B[1;36m   _____ ______          __  __  \u001B[0m");
    }

    /**
     * Gets CobblemonTracker's current instance. It is not recommended to use externally.
     */
    public static CobblemonTracker inst() {
        return CobblemonTracker.instance;
    }

    /**
     * Gets the current {@link MinecraftServer} CobblemonTracker is currently running on.
     */
    public static MinecraftServer getServer() {
        return CobblemonTracker.instance.server;
    }

    /**
     * Gets the {@link PermissionProvider} CobblemonTracker is currently using.
     */
    public static PermissionProvider getPermissionProvider() {
        return CobblemonTracker.instance.permissionProvider;
    }

    /**
     * Sets what {@link PermissionProvider} CobblemonTracker will use to handle all permissions.
     */
    public static void setPermissionProvider(PermissionProvider provider) {
        CobblemonTracker.instance.permissionProvider = provider;
        CobblemonTrackerLogger.info("Registered " + provider.getName() + " as CobblemonTracker's permission provider.");
    }

    // Checks the server for the built-in permission providers.
    private void checkPermissionProvider() {
        if (this.permissionProvider != null) return;
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            this.permissionProvider = new LuckPermsPermissionProvider();
            CobblemonTrackerLogger.info("Found LuckPerms! Permission support enabled.");
            return;
        } catch (ClassNotFoundException ignored) {
        }
        this.permissionProvider = new DefaultPermissionProvider();
        CobblemonTrackerLogger.warn("Couldn't find a built in permission provider.. falling back to permission levels.");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getDataFolder() {
        File folder = FabricLoader.getInstance().getConfigDir().resolve("CobblemonTracker").toFile();
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getFile(String fileName) {
        File file = new File(this.getDataFolder(), fileName);
        if (!file.exists()) file.getParentFile().mkdirs();
        return file;
    }

    public Configuration getConfig(String fileName, boolean saveResource) {
        File configFile = this.getFile(fileName);
        if (!configFile.exists()) {
            if (!saveResource) return null;
            this.saveResource(fileName, false);
        }
        return this.getConfig(configFile);
    }

    public Configuration getConfig(File configFile) {
        try {
            return YamlConfiguration.loadConfiguration(configFile); // ?
        } catch (IOException e) {
            CobblemonTrackerLogger.error("Something went wrong getting the config: " + configFile.getName() + ".");
            CobblemonTrackerLogger.printStackTrace(e);
        }
        return null;
    }

    public void saveConfig(String fileName, Configuration config) {
        File file = this.getFile(fileName);
        try {
            YamlConfiguration.save(config, file);
        } catch (IOException e) {
            CobblemonTrackerLogger.warn("Something went wrong saving the config: " + fileName + ".");
            CobblemonTrackerLogger.printStackTrace(e);
        }
    }

    @SuppressWarnings("resource")
    public void saveResource(String fileName, boolean overwrite) {
        File file = this.getFile(fileName);
        if (file.exists() && !overwrite) return;
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            Path path = Paths.get("configurations", fileName);
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(path.toString().replace("\\", "/"));
            assert in != null;
            in.transferTo(outputStream);
        } catch (IOException e) {
            CobblemonTrackerLogger.error("Something went wrong saving the resource: " + fileName + ".");
            CobblemonTrackerLogger.printStackTrace(e);
        }
    }
}