package ch.tofind.commusica.session;

import ch.tofind.commusica.core.ApplicationProtocol;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Still a lot of work to do but this is a basic implementation of SesionManager with simple features
 */
public class ServerSessionManager {

    //! Shared instance of the object for all the application
    private static ServerSessionManager instance = null;

    //!
    private static int SESSION_TIME_BEFORE_INACTIVE = 10 * 1000;

    //!
    private static Map<InetAddress, ServerSession> availableServers = new HashMap<>();

    //! Clean the old sessions on schedule
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * UserSessionManager single constructor. Avoid the instantiation.
     */
    private ServerSessionManager() {

        availableServers = new HashMap<>(0);

        // Crée un thread qui nettoie les sessions toutes les N secondes
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            deleteObsoleteSessions();
        }, 0, 2, TimeUnit.SECONDS);
    }

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

    public static void store(InetAddress ip, String name, Integer id) {

        if (availableServers.containsKey(ip)) {
            availableServers.get(ip).update();
        } else {

            availableServers.put(ip, new ServerSession(ip, name, id));
        }
    }


    public static Map<InetAddress, ServerSession> getAvailableServers() {
        return availableServers;
    }


    private void deleteObsoleteSessions() {
        for (Map.Entry<InetAddress, ServerSession> entry : availableServers.entrySet()) {
            // if the server hasn't been refreshed in the last 10s
            if (new Date().getTime() - entry.getValue().getUpdated().getTime() > SESSION_TIME_BEFORE_INACTIVE) {

                availableServers.remove(entry.getValue().getIp());
            }
        }
    }

    public static void serverChooser(Map<InetAddress, ServerSession> serverList) {
        if (!serverList.isEmpty()) {

            ArrayList<ServerSession> servers = new ArrayList<>();

            Scanner scanner = new Scanner(System.in);
            System.out.println("To which server do you want to connect?");
            int i = 1;

            for (Map.Entry<InetAddress, ServerSession> entry : serverList.entrySet()) {
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

            ApplicationProtocol.serverId = servers.get(serverChoice).getId();
            ApplicationProtocol.serverName = servers.get(serverChoice).getName();
            ApplicationProtocol.serverAddress = servers.get(serverChoice).getIp();
        }
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
