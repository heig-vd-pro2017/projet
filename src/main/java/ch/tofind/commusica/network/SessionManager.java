package ch.tofind.commusica.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Still a lot of work to do but this is a basic implementation of SesionManager with simple features
 */
public class SessionManager {


    final static Logger LOG = Logger.getLogger(SessionManager.class.getName());

    private List<Session> sessions = new ArrayList<>();
    private int hoursOfSession = 1;

    public void storeSession(Session s) {
        sessions.add(s);
        System.out.println(sessions);
        LOG.info("New session with id " + s.getId() + " stored.");
    }

    /**
     * Remove session with the id passed in parameter
     *
     * @param id
     */
    public void removeSessionById(int id) {
        for (Iterator<Session> iterator = sessions.iterator(); iterator.hasNext(); ) {
            Session s = iterator.next();
            if (s.getId() == id) {
                iterator.remove();

                LOG.info("Session with id " + s.getId() + " deleted.");
            }
        }
    }

    public void updateSession(int id) {
        for (Iterator<Session> iterator = sessions.iterator(); iterator.hasNext(); ) {
            Session s = iterator.next();
            if (s.getId() == id) {
                s.updateDateAdded();
            }
        }

    }

    public boolean idAlreadyStored(int id) {
        for (Session s : sessions) {
            if (s.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void deleteObsoleteSessions() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        for (Iterator<Session> iterator = sessions.iterator(); iterator.hasNext(); ) {
            Session s = iterator.next();
            if (now.getTime() - s.getDateAdded().getTime() > 60 * 60 * 1000 * hoursOfSession) {
            // for tests you can uncomment this line and comment the line on top of this comment
            //if (now.getTime() - s.getDateAdded().getTime() > 3000) {
                iterator.remove();
                LOG.info("Session with id " + s.getId() + " removed because obsolete.");
            }
        }
    }

    public void setHoursOfSession(int hours) {
        hoursOfSession = hours;
    }
}
