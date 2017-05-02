package ch.tofind.commusica.network;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

/**
 * Still a lot of work to do but this is a basic implementation of SesionManager with simple features
 */
public class SessionManager {


    final static Logger LOG = Logger.getLogger(SessionManager.class.getName());

    private Map<Integer, Session> activeSessions = new HashMap<>();
    private Map<Integer, Session> inactiveSessions = new HashMap<>();

    private int minutesOfSession = 1;

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
            if (now.getTime() - s.getActiveSince().getTime() > /*60 **/ 1000 * minutesOfSession) {
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
