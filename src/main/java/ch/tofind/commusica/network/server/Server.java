package ch.tofind.commusica.network.server;

import ch.tofind.commusica.network.NetworkUtils;
import ch.tofind.commusica.session.SessionManager;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

    //! Logger for debugging proposes.
    final static Logger LOG = Logger.getLogger(Server.class.getName());

    //!
    private int port;

    //!
    private InetAddress addressOfInterface;

    //!
    private ReceptionistWorker receptionistWorker;

    //!
    private Runnable serverDiscovery;

    //!
    private Runnable playlistUpdater;

    /**
     * Constructor
     *
     * @param port the port to listen on
     */
    public Server(int port) {
        this.port = port;
        this.addressOfInterface = NetworkUtils.getAddressOfInterface();
    }

    /**
     * This method initiates the process. The server creates a socket and binds it
     * to the previously specified port. It then waits for clients in a infinite
     * loop.
     */
    public void serveClients() {
        LOG.info("Starting the Receptionist Worker on a new thread...");
        receptionistWorker = new ReceptionistWorker(port);
        new Thread(receptionistWorker).start();
        new Thread(ServerDiscovery.getSharedInstance()).start();
        new Thread(PlaylistUpdateSender.getSharedInstance()).start();
    }

    /**
     * Disconnect the server and its threads.
     */
    public void disconnect() {
        receptionistWorker.stop();
        PlaylistUpdateSender.getSharedInstance().stop();
        ServerDiscovery.getSharedInstance().stop();
        SessionManager.getInstance().stop();

        LOG.log(Level.INFO, "Server disconnected.");
    }
}
