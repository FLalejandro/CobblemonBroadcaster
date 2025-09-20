package me.novoro.cobblemontracker.api.permissions;

import me.novoro.cobblemontracker.CobblemonTracker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * An interface used to tell Seam how to check permissions.
 * To register a custom {@link PermissionProvider}, use {@link CobblemonTracker#setPermissionProvider(PermissionProvider)}.
 * @see DefaultPermissionProvider
 * @see LuckPermsPermissionProvider
 */
public interface PermissionProvider {
    /**
     * Gets the name of the {@link PermissionProvider}.
     */
    String getName();

    /**
     * Checks a player for a permission.
     * @param player The target {@link ServerPlayerEntity}.
     * @param permission The permission to check.
     * @param level The permission level to fall back to.
     * @return Whether the player has the permission.
     */
    boolean hasPermission(ServerPlayerEntity player, String permission, int level);

    /**
     * Checks a player for a permission.
     * @param source The {@link ServerCommandSource} of a command.
     * @param permission The permission to check.
     * @param level The permission level to fall back to.
     * @return Whether the player has the permission.
     */
    default boolean hasPermission(ServerCommandSource source, String permission, int level) {
        if (source.getPlayer() == null) return true;
        return this.hasPermission(source.getPlayer(), permission, level);
    }

    /**
     * Gets a meta value on a {@link ServerPlayerEntity}.
     * @param player The target {@link ServerPlayerEntity}.
     * @param metaKey The meta key you're attempting to check.
     */
    @Nullable String getMetaValue(ServerPlayerEntity player, String metaKey);

    /**
     * Gets a meta value on a {@link ServerCommandSource}.
     * If the {@link ServerCommandSource} is not a player, this returns null.
     * @param source The target {@link ServerCommandSource}.
     * @param metaKey The meta key you're attempting to check.
     */
    default String getMetaValue(ServerCommandSource source, String metaKey) {
        if (source.getPlayer() == null) return null;
        return this.getMetaValue(source.getPlayer(), metaKey);
    }

    /**
     * Gets a meta {@link Integer} value on a {@link ServerPlayerEntity}.
     * @param player The target {@link ServerPlayerEntity}.
     * @param metaKey The meta key you're attempting to check.
     */
    default Integer getMetaIntValue(ServerPlayerEntity player, String metaKey) {
        String meta = this.getMetaValue(player, metaKey);
        if (meta == null) return null;
        try {
            return Integer.parseInt(meta);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets a meta {@link Integer} value on a {@link ServerCommandSource}.
     * If the {@link ServerCommandSource} is not a player, this returns null.
     * @param source The target {@link ServerCommandSource}.
     * @param metaKey The meta key you're attempting to check.
     */
    default Integer getMetaIntValue(ServerCommandSource source, String metaKey) {
        if (source.getPlayer() == null) return null;
        return this.getMetaIntValue(source.getPlayer(), metaKey);
    }

    /**
     * Gets a meta {@link Double} value on a {@link ServerPlayerEntity}.
     * @param player The target {@link ServerPlayerEntity}.
     * @param metaKey The meta key you're attempting to check.
     */
    default Double getMetaDoubleValue(ServerPlayerEntity player, String metaKey) {
        String meta = this.getMetaValue(player, metaKey);
        if (meta == null) return null;
        try {
            return Double.parseDouble(meta);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets a meta {@link Double} value on a {@link ServerCommandSource}.
     * If the {@link ServerCommandSource} is not a player, this returns null.
     * @param source The target {@link ServerCommandSource}.
     * @param metaKey The meta key you're attempting to check.
     */
    default Double getMetaDoubleValue(ServerCommandSource source, String metaKey) {
        if (source.getPlayer() == null) return null;
        return this.getMetaDoubleValue(source.getPlayer(), metaKey);
    }
}
