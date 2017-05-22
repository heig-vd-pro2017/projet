package ch.tofind.commusica.core;

import ch.tofind.commusica.network.NetworkProtocol;

import java.net.InetAddress;
import java.util.*;

/**
 * @brief This class represents a server or a client core for the current instance.
 */
public class Core implements ICore {

    //! Shared instance of the object for all the application.
    private static AbstractCore instance = null;

    /**
     * @brief Execute a command on the available core.
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
     * @brief Create a core.
     */
    public Core() {

    }

    /**
     * @brief Setup the core as a network core.
     */
    public void setupAsNetwork() {

        if (instance != null) {
            instance.stop();
        }

        instance = new NetworkCore();
    }

    /**
     * @brief Setup the core as a server.
     */
    public void setupAsServer() {

        if (instance != null) {
            instance.stop();
        }

        instance = new ServerCore();
    }

    /**
     * @brief Setup the core as a client.
     */
    public void setupAsClient() {

        if (instance != null) {
            instance.stop();
        }

        instance = new ClientCore();
    }

    @Override
    public void sendUnicast(InetAddress hostname, String message) {
        instance.sendUnicast(hostname, message);
    }

    @Override
    public void sendMulticast(String message) {
        instance.sendMulticast(message);
    }

    @Override
    public void stop() {
        instance.stop();
    }

}
