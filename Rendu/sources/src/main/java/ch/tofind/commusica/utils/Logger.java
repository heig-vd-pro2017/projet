package ch.tofind.commusica.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is used for debugging.
 */
public class Logger {

    //! Set the debugging for the all the application.
    private static final boolean DEBUG = Configuration.getInstance().get("DEBUG").equals("1");

    //! Class that asked to debug.
    private String className;

    //! All the available colors to show on the screen.
    private enum ANSIColor {
        BLUE(34), GREEN(32), RED(31), RESET(0), YELLOW(33);

        int code;

        ANSIColor(int code) {
            this.code = code;
        }

        public String toString() {
            return String.format("\u001B[%dm", code);
        }
    }

    //! All the available levels for logging.
    public enum Level {
        INFO(ANSIColor.BLUE), SEVERE(ANSIColor.RED), SUCCESS(ANSIColor.GREEN), WARNING(ANSIColor.YELLOW);

        ANSIColor color;

        Level(ANSIColor color) {
            this.color = color;
        }

        public String toString() {
            return String.format("%s%s%s", color, name(), ANSIColor.RESET);
        }
    }

    /**
     * Logger constructor.
     *
     * @param className Name of the class that asked the debug.
     */
    public Logger(String className) {
        this.className = className;
    }

    /**
     * Display the log message with a certain level.
     *
     * @param level The level of the message.
     * @param message The message to display.
     */
    private void log(Level level, String message) {
        if (DEBUG) {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z");

            System.out.printf("%s - %s - %s: %s\n", format.format(now), className, level, message);
            //System.out.printf("%s: %s\n", level, message);
        }
    }

    /**
     * Throw an exception with a certain level.
     *
     * @param level The level of the exception.
     * @param e The exception to throw.
     */
    private void log(Level level, Exception e) {
        log(level, e.getMessage());
        e.printStackTrace();
    }

    /**
     * Log as info.
     *
     * @param message The message to display.
     */
    public void info(String message) {
        log(Level.INFO, message);
    }

    /**
     * Log as warning.
     *
     * @param message The message to display.
     */
    public void warning(String message) {
        log(Level.WARNING, message);
    }

    /**
     * Log as error.
     *
     * @param message The message to display.
     */
    public void error(String message) {
        log(Level.SEVERE, message);
    }

    /**
     * Log as error.
     *
     * @param e The exception that has been thrown.
     */
    public void error(Exception e) {
        log(Level.SEVERE, e);
    }
}