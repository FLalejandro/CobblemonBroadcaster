package me.novoro.cobblemonbroadcaster.util

import net.minecraft.server.command.ServerCommandSource

object Permissions {
    var permissionAPIPresent: Boolean = false
    fun initialize() {
        try {
            Class.forName("me.lucko.fabric.api.permissions.v0.Permissions")
            permissionAPIPresent = true
        } catch (_: Exception) {}
    }

    fun has(source: ServerCommandSource, permission: String, level: Int = 2): Boolean {
        return if (permissionAPIPresent) {
            PermissionUtils.has(source, permission, level)
        } else {
            source.hasPermissionLevel(level)
        }
    }
}