package ch.tofind.commusica.network.server;

import ch.tofind.commusica.network.Protocol;

import java.net.InetAddress;

/**
 * @brief This class represents the server part of the application.
 */
public class Server {

    //! The receptionist that will serve the clients
    private FrontendThread frontendThread;

    /**
     * @brief Constructor.
     *
     * @param port The port to listen on.
     * @param inetAddress The address to use.
     */
    public Server(int port, InetAddress inetAddress) {
        frontendThread = new FrontendThread(port);
    }

    /**
     * @brief Starts the server
     */
    public void start() {
        new Thread(frontendThread).start();
    }

    /**
     * @brief Stops the server
     */
    public void stop() {
        frontendThread.stop();
    }
}
