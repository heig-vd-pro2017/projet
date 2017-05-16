package ch.tofind.commusica.core;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * @brief This class represents a server or a client core for the current instance.
 */
public class Core implements ICore {

    //! Shared instance of the object for all the application.
    private static AbstractCore instance = null;

    //! Interface to use for Multicast.
    private InetAddress interfaceToUse;

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
     *
     * @param interfaceToUse Interface to use for multicast.
     */
    public Core(InetAddress interfaceToUse) {
        this.interfaceToUse = interfaceToUse;
    }

    /**
     * @brief Setup the core as a server.
     *
     * @param name Name of the server.
     * @param multicastAddress Multicast address.
     * @param multicastPort Multicast port.
     * @param unicastPort Unicast port.
     */
    public void setupAsServer(String name, String multicastAddress, int multicastPort, int unicastPort) {
        instance = new ServerCore(name, multicastAddress, multicastPort, interfaceToUse, unicastPort);
    }

    /**
     * @brief Setup the core as a client.
     *
     * @param multicastAddress Multicast address.
     * @param multicastPort Multicast port.
     */
    public void setupAsClient(String multicastAddress, int multicastPort) {
        instance = new ClientCore(multicastAddress, multicastPort, interfaceToUse);
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
