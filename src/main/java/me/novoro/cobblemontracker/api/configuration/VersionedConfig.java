package me.novoro.cobblemontracker.api.configuration;

import me.novoro.cobblemontracker.CobblemonTracker;

/**
 * A class all versioned configs should extend. This makes updating configs as simple as we can possibly make it.
 * Only downside is it requires us to have a static version of the config class instead of having the class static.
 */
public abstract class VersionedConfig {
    /**
     * Gets the current config version the specified {@link VersionedConfig} is on.
     * Raising this value to something above the currently saved config will trigger {@link VersionedConfig#updateConfig(Configuration, double)}.
     */
    protected abstract double getCurrentConfigVersion();

    /**
     * Gets the file name of the target config for easy saving and loading.
     */
    protected abstract String getConfigFileName();

    /**
     * The reload method that should be used to reload configs in CobblemonTracker's main class.
     * Not intended to be overridden.
     */
    public void reload() {
        this.reload(CobblemonTracker.inst().getConfig(this.getConfigFileName(), true));
    }

    /**
     * The reload method to be overridden to load values from the {@link Configuration}.
     * Forgetting to call super.reload(Configuration) will result in configs not being updated.
     */
    protected void reload(Configuration config) {
        double configVersion = config.getDouble("Config-Version", 1.0);
        if (this.getCurrentConfigVersion() > configVersion) {
            this.updateConfig(config, configVersion);
            CobblemonTracker.inst().saveConfig(this.getConfigFileName(), config);
        }
    }

    /**
     * Updates the given config with new values.
     * Forgetting to call super.updateConfig(Configuration, double) will result in the version not being updated.
     * @param input The currently loaded config.
     * @param loadedVersion The version of the given config.
     */
    protected void updateConfig(Configuration input, double loadedVersion) {
        input.set("Config-Version", this.getCurrentConfigVersion());
    }
}