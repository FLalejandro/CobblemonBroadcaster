package me.novoro.cobblemontracker.config;

import me.novoro.cobblemontracker.api.configuration.Configuration;
import me.novoro.cobblemontracker.api.configuration.VersionedConfig;
import me.novoro.cobblemontracker.commands.CommandBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Seam's module manager. This lets the rest of the mod know what is enabled or disabled.
 */
public final class ModuleManager extends VersionedConfig {
    // Modules marked as true in modules.yml.
    private static final List<String> ACTIVE_MODULES = new ArrayList<>();
    // Commands marked as true in modules.yml.
    private static final List<String> ACTIVE_COMMANDS = new ArrayList<>();

    @Override
    protected void reload(Configuration moduleConfig) {
        super.reload(moduleConfig);
        ModuleManager.ACTIVE_MODULES.clear();
        Configuration moduleSection = moduleConfig.getSection("Modules");
        for (String key : moduleSection.getKeys()) {
            if (moduleSection.getBoolean(key)) ModuleManager.ACTIVE_MODULES.add(key);
        }
        ModuleManager.ACTIVE_COMMANDS.clear();
        Configuration commandSection = moduleConfig.getSection("Commands");
        for (String key : commandSection.getKeys()) {
            if (commandSection.getBoolean(key)) ModuleManager.ACTIVE_COMMANDS.add(key);
        }
    }

    /**
     * Checks if a module is currently enabled.
     * @param key The module's ID.
     * @return Whether the module is enabled.
     */
    public static boolean isModuleEnabled(String key) {
        return ModuleManager.ACTIVE_MODULES.contains(key);
    }

    /**
     * Checks if a command is currently enabled.
     * @param command The {@link CommandBase} to check.
     * @return Whether the command should be enabled.
     */
    public static boolean isCommandEnabled(CommandBase command) {
        String module = command.getControllingModule();
        if (module != null) return ModuleManager.isModuleEnabled(module);
        String commandGroup = command.getCommandGroup();
        if (commandGroup != null) return ModuleManager.ACTIVE_COMMANDS.contains(commandGroup);
        return ModuleManager.ACTIVE_COMMANDS.contains(command.getCommand());
    }

    @Override
    public double getCurrentConfigVersion() {
        return 2.0;
    }

    @Override
    protected String getConfigFileName() {
        return "modules.yml";
    }
}
