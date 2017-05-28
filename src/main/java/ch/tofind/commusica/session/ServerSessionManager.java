package ch.tofind.commusica.session;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.utils.Logger;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Manage the servers sessions.
 */
public class ServerSessionManager implements ISessionManager {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(ServerSessionManager.class.getSimpleName());

    //! Shared instance of the object for all the application.
    private static ServerSessionManager instance = null;

    //! Store the active sessions.
    private static ObservableMap<Integer, ServerSession> availableServers;

    //! Clean the old sessions on schedule.
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * ServerSessionManager single constructor. Avoid the instantiation.
     */
    private ServerSessionManager() {

        availableServers = FXCollections.observableHashMap();

        // Crée un thread qui nettoie les sessions toutes les N secondes
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(this::deleteObsoleteSessions, 0, TIME_BEFORE_SESSION_INACTIVE, TimeUnit.SECONDS);
    }

    /**
     * Get the object instance.
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
     * Store a new session.
     *
     * @param id ID of the new session.
     * @param serverIp The server's IP.
     * @param serverName The server's name.
     */
    public void store(Integer id, InetAddress serverIp, String serverName) {

        if (availableServers.containsKey(id)) {
            ServerSession serverSession = availableServers.get(id);
            serverSession.update();
            availableServers.put(serverSession.getId(), serverSession);
        } else {
            ServerSession serverSession = new ServerSession(id, serverIp, serverName);
            availableServers.put(serverSession.getId(), serverSession);
        }
    }

    /**
     * Get the available servers for the user.
     *
     * @return The available servers.
     */
    public ObservableMap<Integer, ServerSession> getAvailableServers() {
        return availableServers;
    }

    /**
     * Delete the sessions that are not active anymore.
     */
    private void deleteObsoleteSessions() {

        Date now = new Date();

        for (Map.Entry<Integer, ServerSession> entry : availableServers.entrySet()) {

            ServerSession serverSession = entry.getValue();

            if (serverSession.getLastUpdate().getTime() < now.getTime() - TIME_BEFORE_SESSION_INACTIVE * 1000) {

                LOG.info("Removing old server session.");

                Platform.runLater(() -> availableServers.remove(serverSession.getId()));

                if (serverSession.getId() == ApplicationProtocol.serverId) {
                    ApplicationProtocol.serverId = null;
                    ApplicationProtocol.serverAddress = null;
                    ApplicationProtocol.serverName = null;
                }
            }
        }
    }

    @Override
    public void stop() {

        scheduledExecutorService.shutdown();

        instance = null;
    }
}
