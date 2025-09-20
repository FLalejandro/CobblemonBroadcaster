package me.novoro.cobblemontracker.api.permissions;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link PermissionProvider} representing an environment without any permission provider.
 * Does not support meta values.
 */
public final class DefaultPermissionProvider implements PermissionProvider {
    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public boolean hasPermission(ServerPlayerEntity player, String permission, int level) {
        return player.hasPermissionLevel(level);
    }

    @Override
    public @Nullable String getMetaValue(ServerPlayerEntity player, String metaKey) {
        return null;
    }
}
