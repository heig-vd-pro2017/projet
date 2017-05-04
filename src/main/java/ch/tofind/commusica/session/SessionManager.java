package ch.tofind.commusica.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Still a lot of work to do but this is a basic implementation of SesionManager with simple features
 */
public class SessionManager {

    //! Shared instance of the object for all the application
    private static SessionManager instance = null;

    //! Store the active sessions
    private Map<Integer, Session> activeSessions;

    //! Store the inactive sessions
    private Map<Integer, Session> inactiveSessions;

    //!
    private int minutesOfSession;

    //!
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * SessionManager single constructor. Avoid the instantiation.
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

    public void store(Integer id) {

        if (activeSessions.containsKey(id)) {
            Session session = inactiveSessions.get(id);
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

            if (now.getTime() - session.getUpdate().getTime() > 60 * 1000 * minutesOfSession) {
                activeSessions.remove(session.getId());
                inactiveSessions.put(session.getId(), session);
            }
        }
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
