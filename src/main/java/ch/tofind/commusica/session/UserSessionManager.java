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
public class UserSessionManager {

    //! Shared instance of the object for all the application
    private static UserSessionManager instance = null;

    //!
    private static int SESSION_TIME_BEFORE_INACTIVE = 60 * 1000 * 1;

    //! Store the active sessions
    private Map<Integer, UserSession> activeSessions;

    //! Store the inactive sessions
    private Map<Integer, UserSession> inactiveSessions;

    //! Clean the old sessions on schedule
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * UserSessionManager single constructor. Avoid the instantiation.
     */
    private UserSessionManager() {

        activeSessions = new HashMap<>(0);
        inactiveSessions = new HashMap<>(0);

        // Crée un thread qui nettoie les sessions toutes les N minutes
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            deleteObsoleteSessions();
        }, 0, 30, TimeUnit.SECONDS);
    }

    public static UserSessionManager getInstance() {

        if(instance == null) {
            synchronized (UserSessionManager.class) {
                if (instance == null) {
                    instance = new UserSessionManager();
                }
            }
        }

        return instance;
    }

    public void store(Integer id) {

        if (activeSessions.containsKey(id)) {
            UserSession userSession = activeSessions.get(id);
            userSession.update();
        } else if (inactiveSessions.containsKey(id)) {
            UserSession userSession = inactiveSessions.remove(id);
            userSession.update();
            activeSessions.put(userSession.getId(), userSession);
        } else {
            UserSession userSession = new UserSession(id);
            activeSessions.put(userSession.getId(), userSession);
        }
    }

    public int countActiveSessions() {
        return activeSessions.size();
    }

    private void deleteObsoleteSessions() {

        Date now = new Date();

        for (Map.Entry<Integer, UserSession> entry : activeSessions.entrySet()) {

            UserSession userSession = entry.getValue();

            if (userSession.getUpdate().getTime() > now.getTime() - SESSION_TIME_BEFORE_INACTIVE) {
                activeSessions.remove(userSession.getId());
                inactiveSessions.put(userSession.getId(), userSession);
            }
        }
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
