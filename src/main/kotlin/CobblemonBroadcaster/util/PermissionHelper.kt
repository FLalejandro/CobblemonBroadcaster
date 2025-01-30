package CobblemonBroadcaster.util

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Provides utility functions for checking permissions using LuckPerms.
 * Primarily used to validate if a player has certain privileges within the EEssentials mod.
 */
class PermissionHelper {
    var luckperms: LuckPerms = LuckPermsProvider.get()

    fun getLuckPermsUser(player: ServerPlayerEntity): User {
        return luckperms.getPlayerAdapter<ServerPlayerEntity>(
            ServerPlayerEntity::class.java
        ).getUser(player)
    }
}