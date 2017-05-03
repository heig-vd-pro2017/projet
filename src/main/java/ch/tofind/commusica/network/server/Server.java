package ch.tofind.commusica.network.server;

import ch.tofind.commusica.network.NetworkUtils;
import ch.tofind.commusica.network.session.SessionManager;

import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

    //! Logger for debugging proposes.
    final static Logger LOG = Logger.getLogger(Server.class.getName());

    //!
    private SessionManager sessionManager = SessionManager.getInstance();

    //!
    private int port;

    //!
    private InetAddress addressOfInterface;

    //!
    private ScheduledExecutorService scheduledExecutorService;

    //!
    private ReceptionistWorker receptionist;

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
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    sessionManager.deleteObsoleteSessions();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        receptionist = new ReceptionistWorker(port);
        new Thread(receptionist).start();
        new Thread(ServerDiscovery.getSharedInstance()).start();
        new Thread(PlaylistUpdateSender.getSharedInstance()).start();
    }




    /**
     * Disconnect the server and its threads.
     */
    public void disconnect() {
        receptionist.stop();
        PlaylistUpdateSender.getSharedInstance().stop();
        ServerDiscovery.getSharedInstance().stop();
        scheduledExecutorService.shutdown();

        LOG.log(Level.INFO, "Server disconnected.");
    }
}
