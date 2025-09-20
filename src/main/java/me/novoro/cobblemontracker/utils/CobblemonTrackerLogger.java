package me.novoro.cobblemontracker.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CobblemonTracker's Logger. It's not recommended to use this externally.
 */
public class CobblemonTrackerLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("CobblemonTracker");

    /**
     * Sends an info log to console.
     * @param s The string to log.
     */
    public static void info(String s) {
        CobblemonTrackerLogger.LOGGER.info("{}{}", "[CobblemonTracker]: ", s);
    }

    /**
     * Sends a warn log to console.
     * @param s The string to log.
     */
    public static void warn(String s) {
        CobblemonTrackerLogger.LOGGER.warn("{}{}", "[CobblemonTracker]: ", s);
    }

    /**
     * Sends an error log to console.
     * @param s The string to log.
     */
    public static void error(String s) {
        CobblemonTrackerLogger.LOGGER.error("{}{}", "[CobblemonTracker]: ", s);
    }

    /**
     * Prints a stacktrace using CobblemonTracker's Logger.
     * @param throwable The exception to print.
     */
    public static void printStackTrace(Throwable throwable) {
        CobblemonTrackerLogger.error(throwable.toString());
        StackTraceElement[] trace = throwable.getStackTrace();
        for (StackTraceElement traceElement : trace) CobblemonTrackerLogger.error("\tat " + traceElement);
    }
}
