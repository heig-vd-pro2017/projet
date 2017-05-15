package ch.tofind.commusica.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * @brief This class represents an abstract core to execute code asked from the network.
 */
abstract class AbstractCore {

    /**
     * @brief Execute a command on the available core.
     * @param command Command to execute.
     * @param args Args of the command.
     * @return The output of the command.
     */
    public String execute(String command, ArrayList<Object> args) {

        String result = "";

        try {
            Method method = this.getClass().getMethod( command, ArrayList.class);
            result = (String) method.invoke(this, args);
        } catch (NoSuchMethodException e) {
            // Do nothing
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @brief Send a request by unicast to the hostname.
     * @param hostname IP address of the hostname.
     * @param message Message to send to the hostname.
     */
    abstract void sendUnicast(InetAddress hostname, String message);

    /**
     * @brief Send a request by multicast.
     * @param message Message to send.
     */
    abstract void sendMulticast(String message);

    /**
     * @brief Stop the core.
     */
    abstract void stop();
}
