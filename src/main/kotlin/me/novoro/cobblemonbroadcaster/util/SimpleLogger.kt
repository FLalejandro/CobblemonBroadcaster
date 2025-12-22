package me.novoro.cobblemonbroadcaster.util

import net.fabricmc.loader.api.FabricLoader

object SimpleLogger {
    fun info(message: String) {
        println("[INFO] $message")
    }

    fun warn(message: String) {
        println("[WARN] $message")
    }

    fun error(message: String, throwable: Throwable? = null) {
        println("[ERROR] $message")
        throwable?.printStackTrace()
    }

    fun debug(message: String) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) println("[DEBUG] $message")
    }
}
