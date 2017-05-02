package ch.tofind.commusica.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Logger {

    private static final boolean DEBUG = Configuration.getInstance().get("DEBUG").equals("1");

    private String className;

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

    public Logger(String className) {
        this.className = className;
    }

    public void log(Level level, String message) {
        if (DEBUG) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z");

            System.out.printf("%s - %s\n", format.format(timestamp), className);
            System.out.printf("%s: %s\n", level, message);
        }
    }

    public void log(Level level, Exception e) {
        log(level, e.getMessage());
        e.printStackTrace();

        throw new RuntimeException(e);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

}
