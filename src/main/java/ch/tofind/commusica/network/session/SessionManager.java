package ch.tofind.commusica.network.session;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

    /**
     * PlaylistManager single constructor. Avoid the instantiation.
     */
    private SessionManager() {
        activeSessions = new HashMap<>(0);
        inactiveSessions = new HashMap<>(0);
        minutesOfSession = 1;
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

    public void deleteObsoleteSessions() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Iterator<Session> iterator = activeSessions.values().iterator();
        while(iterator.hasNext()) {
        //for (Iterator<Session> iterator = sessions.iterator() && iterator.hasNext(); ) {
            Session s = iterator.next();
            if (now.getTime() - s.getActiveSince().getTime() > 60 * 1000 * minutesOfSession) {
                // for tests you can uncomment this line and comment the line on top of this comment
                //if (now.getTime() - s.getDateAdded().getTime() > 3000) {
                activeSessions.remove(s.getId());
                inactiveSessions.put(s.getId(), s);
                LOG.info("Session with id " + s.getId() + " became obsolete.");

                System.out.println("Active sessions:\n" + activeSessions);
                System.out.println("Inactive sessions:\n" + inactiveSessions);
            }
        }
    }

    public void setMinutesOfSession(int hours) {
        minutesOfSession = hours;
    }
}
