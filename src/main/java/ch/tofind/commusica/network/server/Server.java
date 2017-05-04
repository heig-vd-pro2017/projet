package ch.tofind.commusica.network.server;

import ch.tofind.commusica.network.Protocol;

import java.net.InetAddress;

/**
 * @brief This class represents the server part of the application.
 */
public class Server {

    //! The receptionist that will serve the clients
    private FrontendThread frontendThread;

    //! The servant that will manage the client
    private BroadcastThread broadcastThread;

    /**
     * @brief Constructor.
     *
     * @param port The port to listen on.
     * @param inetAddress The address to use.
     */
    public Server(int port, InetAddress inetAddress) {
        frontendThread = new FrontendThread(port);
        broadcastThread = new BroadcastThread(inetAddress, Protocol.IP_MULTICAST_DISCOVERY);

        new Thread(frontendThread).start();
        new Thread(broadcastThread).start();
    }

    /**
     * @brief Stop the server
     */
    public void stop() {
        frontendThread.stop();
        broadcastThread.stop();
    }
}
