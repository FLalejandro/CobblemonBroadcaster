package me.novoro.cobblemontracker.api.permissions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.platform.PlayerAdapter;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link PermissionProvider} representing an environment running LuckPerms.
 */
public final class LuckPermsPermissionProvider implements PermissionProvider {
    private final LuckPerms luckPermsAPI;

    public LuckPermsPermissionProvider() {
        this.luckPermsAPI = LuckPermsProvider.get();
    }

    @Override
    public String getName() {
        return "LuckPerms";
    }

    @Override
    public boolean hasPermission(ServerPlayerEntity player, String permission, int level) {
        if (player.hasPermissionLevel(4)) return true; // OP should have all permissions.
        return this.getPlayerAdapter().getPermissionData(player).checkPermission(permission).asBoolean();
    }

    @Override
    public @Nullable String getMetaValue(ServerPlayerEntity player, String metaKey) {
        return this.getPlayerAdapter().getMetaData(player).getMetaValue(metaKey);
    }

    private PlayerAdapter<ServerPlayerEntity> getPlayerAdapter() {
        return this.luckPermsAPI.getPlayerAdapter(ServerPlayerEntity.class);
    }
}
