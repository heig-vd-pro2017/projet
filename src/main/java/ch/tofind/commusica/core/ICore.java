package ch.tofind.commusica.core;

import java.net.InetAddress;

public interface ICore {

    /**
     * @brief Send a request by unicast to the hostname.
     *
     * @param hostname IP address of the hostname.
     * @param message Message to send to the hostname.
     */
    abstract void sendUnicast(InetAddress hostname, String message);

    /**
     * @brief Send a request by multicast.
     *
     * @param message Message to send.
     */
    abstract void sendMulticast(String message);

    /**
     * @brief Stop the core.
     */
    abstract void stop();
}
