package me.novoro.cobblemontracker.utils;

import me.novoro.cobblemontracker.CobblemonTracker;
import net.minecraft.server.world.ServerWorld;

/**
 * Provides various Location utility methods to Seam.
 */
public final class LocationUtil {
    /**
     * Gets a {@link ServerWorld} via a world name.
     * @param worldName The name of the world you're attempting to obtain.
     * @return The {@link ServerWorld} with the specified world name.
     */

    public static ServerWorld getWorld(String worldName) {
        for (ServerWorld serverWorld : CobblemonTracker.getServer().getWorlds()) {
            if (!LocationUtil.getWorldName(serverWorld).equals(worldName)) continue;
            return serverWorld;
        }
        return null;
    }

    /**
     * Gets the world name of a specified {@link ServerWorld}.
     */
    public static String getWorldName(ServerWorld world) {
        return world.getRegistryKey().getValue().toString();
    }

}