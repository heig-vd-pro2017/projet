package ch.tofind.commusica.network.session;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Still a lot of work to do but this is a basic implementation of SesionManager with simple features
 */
public class SessionManager {

    //!
    final static Logger LOG = Logger.getLogger(SessionManager.class.getName());

    //! Shared instance of the session manager for all the application
    private static SessionManager instance = null;

    //!
    private Map<Integer, Session> activeSessions;

    //!
    private Map<Integer, Session> inactiveSessions;

    //!
    private int minutesOfSession;

    //!
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * PlaylistManager single constructor. Avoid the instantiation.
     */
    private SessionManager() {

        activeSessions = new HashMap<>(0);
        inactiveSessions = new HashMap<>(0);
        minutesOfSession = 1;

        // Crée un thread qui nettoie les sessions toutes les N minutes
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            deleteObsoleteSessions();
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static SessionManager getInstance() {

        if(instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }

        return instance;
    }

    public void storeSession(Session s) {
        inactiveSessions.remove(s.getId());
        activeSessions.put(s.getId(), s);

        System.out.println("Active sessions:\n" + activeSessions);
        System.out.println("Inactive sessions:\n" + inactiveSessions);
    }

    public int countActiveSessions() {
        return activeSessions.size();
    }

    private void deleteObsoleteSessions() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (Map.Entry<Integer, Session> entry : activeSessions.entrySet()) {

            Session session = entry.getValue();

            if (now.getTime() - session.getActiveSince().getTime() > 60 * 1000 * minutesOfSession) {
                activeSessions.remove(session.getId());
                inactiveSessions.put(session.getId(), session);
            }
        }
    }

    public void setMinutesOfSession(int hours) {
        minutesOfSession = hours;
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
