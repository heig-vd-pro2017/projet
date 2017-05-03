package ch.tofind.commusica.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Logger {

    //! Set the debugging for the all the application.
    private static final boolean DEBUG = Configuration.getInstance().get("DEBUG").equals("1");

    //! Class that asked to debug.
    private String className;

    //!
    private enum ANSIColor {
        BLUE(34), GREEN(42), RED(31), RESET(0), YELLOW(43);

        int code;

        ANSIColor(int code) {
            this.code = code;
        }

        public String toString() {
            return String.format("\u001B[%dm", code);
        }
    }

    //!
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
     * @brief Logger constructor.
     *
     * @param className Name of the class that asked the debug.
     */
    public Logger(String className) {
        this.className = className;
    }

    /**
     * @brief Display the log message with a certain level.
     *
     * @param level The level of the message.
     * @param message The message to display.
     */
    public void log(Level level, String message) {
        if (DEBUG) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z");

            System.out.printf("%s - %s\n", format.format(timestamp), className);
            System.out.printf("%s: %s\n", level, message);
        }
    }

    /**
     * @brief Throw an exception with a certain level.
     *
     * @param level The level of the exception.
     * @param e The exception to throw.
     */
    public void log(Level level, Exception e) {
        log(level, e.getMessage());
        e.printStackTrace();

        throw new RuntimeException(e);
    }
}
