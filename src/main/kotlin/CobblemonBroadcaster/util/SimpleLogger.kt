package CobblemonBroadcaster.util

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
        println("[DEBUG] $message")
    }
}
