package ch.tofind.commusica.network;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

/**
 * Still a lot of work to do but this is a basic implementation of SesionManager with simple features
 */
public class SessionManager {


    final static Logger LOG = Logger.getLogger(SessionManager.class.getName());

    private Map<Integer, Session> sessions = new HashMap<>();
    private int minutesOfSession = 1;

    public void storeSession(Session s) {
        sessions.put(s.getId(), s);
        System.out.println(sessions);
        LOG.info("New session with id " + s.getId() + " stored.");
    }

    public int countSessions() {
        return sessions.size();
    }

    /**
     * Remove session with the id passed in parameter
     *
     * @param id
     */
    public void removeSessionById(int id) {
        sessions.remove(id);
        LOG.info("Session with id " + id + " deleted.");
    }

    public void updateSession(int id) {
        sessions.get(id).setActiveSince(new Timestamp(System.currentTimeMillis()));
        sessions.get(id).setActive(true);
    }

    public boolean idAlreadyStored(int id) {
        return sessions.containsKey(id);
    }

    public void deleteObsoleteSessions() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Iterator<Session> iterator = sessions.values().iterator();
        while(iterator.hasNext()) {
        //for (Iterator<Session> iterator = sessions.iterator() && iterator.hasNext(); ) {
            Session s = iterator.next();
            if (s.getActive() && now.getTime() - s.getDateAdded().getTime() > /*60 **/ 1000 * minutesOfSession) {
                // for tests you can uncomment this line and comment the line on top of this comment
                //if (now.getTime() - s.getDateAdded().getTime() > 3000) {
                s.setActive(false);
                LOG.info("Session with id " + s.getId() + " became obsolete.");
            }
        }
    }

    public void setMinutesOfSession(int hours) {
        minutesOfSession = hours;
    }
}
