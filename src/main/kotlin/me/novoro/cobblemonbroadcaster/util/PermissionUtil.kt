package me.novoro.cobblemonbroadcaster.util

import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.ServerCommandSource

object PermissionUtils {
    fun has(source: ServerCommandSource, permission: String, level: Int): Boolean {
        return Permissions.check(source, permission, level)
    }
}