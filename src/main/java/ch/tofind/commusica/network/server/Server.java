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
     * @param hostname The address to use.
     * @param port The port to listen on.
     */
    public Server(InetAddress hostname, int port) {
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
