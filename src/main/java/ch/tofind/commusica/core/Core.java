package ch.tofind.commusica.core;

import java.util.ArrayList;

/**
 * This class represents a server or a client core for the current instance.
 */
public class Core {

    //! Shared instance of the object for all the application.
    private static AbstractCore instance = null;

    /**
     * Execute a command on the available core.
     *
     * @param command Command to execute.
     * @param args Args of the command.
     *
     * @return The output of the command.
     */
    public static String execute(String command, ArrayList<Object> args) {
        return instance.execute(command, args);
    }

    /**
     * Tells if the current Core is the server or not.
     * @return True if the current Core is the server, otherwise false.
     */
    public static boolean isServer() {
        return instance.isServer();
    }

    /**
     * Setup the core as a server.
     */
    public static void setupAsServer() {

        if (instance != null) {
            instance.stop();
        }

        instance = new ServerCore();
    }

    /**
     * Setup the core as a client.
     */
    public static void setupAsClient() {

        if (instance != null) {
            instance.stop();
        }

        instance = new ClientCore();
    }

    /**
     * Stop the core.
     */
    public static void stop() {

        if (instance != null) {
            instance.stop();
        }
    }
}
