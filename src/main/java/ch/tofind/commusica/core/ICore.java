package ch.tofind.commusica.core;

import java.net.InetAddress;

/**
 * @brief This interface is used to implement any core.
 */
public interface ICore {

    /**
     * @brief Send a request by unicast to the hostname.
     *
     * @param hostname IP address of the hostname.
     * @param message Message to send to the hostname.
     */
    void sendUnicast(InetAddress hostname, String message);

    /**
     * @brief Send a request by multicast.
     *
     * @param message Message to send.
     */
    void sendMulticast(String message);

    /**
     * @brief Stop the core.
     */
    void stop();
}
