package ch.tofind.commusica.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Still a lot of work to do but this is a basic implementation of SesionManager with simple features
 */
public class SessionManager {

    //! Shared instance of the object for all the application
    private static SessionManager instance = null;

    //!
    private static int SESSION_TIME_BEFORE_INACTIVE = 60 * 1000 * 1;

    //! Store the active sessions
    private Map<Integer, Session> activeSessions;

    //! Store the inactive sessions
    private Map<Integer, Session> inactiveSessions;

    //! Clean the old sessions on schedule
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * SessionManager single constructor. Avoid the instantiation.
     */
    private SessionManager() {

        activeSessions = new HashMap<>(0);
        inactiveSessions = new HashMap<>(0);

        // Crée un thread qui nettoie les sessions toutes les N minutes
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            deleteObsoleteSessions();
        }, 0, 30, TimeUnit.SECONDS);
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

    public void store(Integer id) {

        if (activeSessions.containsKey(id)) {
            Session session = activeSessions.get(id);
            session.update();
        } else if (inactiveSessions.containsKey(id)) {
            Session session = inactiveSessions.remove(id);
            session.update();
            activeSessions.put(session.getId(), session);
        } else {
            Session session = new Session(id);
            activeSessions.put(session.getId(), session);
        }
    }

    public int countActiveSessions() {
        return activeSessions.size();
    }

    private void deleteObsoleteSessions() {

        Date now = new Date();

        for (Map.Entry<Integer, Session> entry : activeSessions.entrySet()) {

            Session session = entry.getValue();

            if (session.getUpdate().getTime() > now.getTime() - SESSION_TIME_BEFORE_INACTIVE) {
                activeSessions.remove(session.getId());
                inactiveSessions.put(session.getId(), session);
            }
        }
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
