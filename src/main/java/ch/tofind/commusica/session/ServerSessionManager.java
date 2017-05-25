package ch.tofind.commusica.session;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.network.Server;
import ch.tofind.commusica.utils.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manage the servers sessions.
 */
public class ServerSessionManager implements ISessionManager {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(ServerSessionManager.class.getSimpleName());

    //! Shared instance of the object for all the application.
    private static ServerSessionManager instance = null;

    //! Store the active sessions.
    //private static Map<Integer, ServerSession> availableServers;
    private static ObservableMap<Integer, ServerSession> availableServers;

    //private static ObservableList<ServerSession> observableServersList;

    //! Clean the old sessions on schedule.
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * ServerSessionManager single constructor. Avoid the instantiation.
     */
    private ServerSessionManager() {

        //availableServers = new HashMap<>(0);
        availableServers = FXCollections.observableHashMap();

        // Crée un thread qui nettoie les sessions toutes les N secondes
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            deleteObsoleteSessions();
        }, 0, TIME_BEFORE_SESSION_INACTIVE, TimeUnit.SECONDS);
    }

    /**
     * @brief Get the object instance.
     *
     * @return The instance of the object.
     */
    public static ServerSessionManager getInstance() {

        if(instance == null) {
            synchronized (ServerSessionManager.class) {
                if (instance == null) {
                    instance = new ServerSessionManager();
                }
            }
        }

        return instance;
    }

    /**
     * @brief Store a new session.
     *
     * @param id ID of the new session.
     * @param serverIp The server's IP.
     * @param serverName The server's name.
     */
    public void store(Integer id, InetAddress serverIp, String serverName) {

        if (availableServers.containsKey(id)) {
            ServerSession serverSession = availableServers.get(id);
            serverSession.update();
        } else {
            ServerSession serverSession = new ServerSession(id, serverIp, serverName);
            availableServers.put(serverSession.getId(), serverSession);
            //observableServersList.add(serverSession);
        }
    }

    /**
     * @brief Get the available servers for the user.
     *
     * @return The available servers.
     */
    public ObservableMap<Integer, ServerSession> getAvailableServers() {
        return availableServers;
    }

    /*public ObservableList<ServerSession> getObservableServersList() {
        return observableServersList;
    }*/

    /**
     * @brief Choose a server in the available servers.
     * @param serverList The available servers.
     */
    public void serverChooser(Map<Integer, ServerSession> serverList) {
        if (!serverList.isEmpty()) {

            ArrayList<ServerSession> servers = new ArrayList<>();

            Scanner scanner = new Scanner(System.in);
            System.out.println("To which server do you want to connect?");
            int i = 1;

            for (Map.Entry<Integer, ServerSession> entry : serverList.entrySet()) {
                System.out.println("[" + i++ + "]" + "    " + entry.getValue());
                servers.add(entry.getValue());
            }

            int serverChoice = -1;
            while (serverChoice < 0) {
                serverChoice = scanner.nextInt();

                if (serverChoice > serverList.size()) {
                    System.out.println("Not valid!");
                    serverChoice = -1;
                }
            }
            serverChoice--;

            ServerSession chosenServer = servers.get(serverChoice);

            ApplicationProtocol.serverId = chosenServer.getId();
            ApplicationProtocol.serverAddress = chosenServer.getServerIp();
            ApplicationProtocol.serverName = chosenServer.getServerName();
        }
    }

    /**
     * @brief Delete the sessions that are not active anymore.
     */
    private void deleteObsoleteSessions() {

        Date now = new Date();

        for (Map.Entry<Integer, ServerSession> entry : availableServers.entrySet()) {

            ServerSession serverSession = entry.getValue();

            if (serverSession.getLastUpdate().getTime() > now.getTime() - TIME_BEFORE_SESSION_INACTIVE) {

                LOG.info("Removing old server session.");

                availableServers.remove(serverSession.getId());
                //observableServersList.remove(serverSession);
            }
        }
    }

    @Override
    public void stop() {

        scheduledExecutorService.shutdown();

        instance = null;
    }
}
