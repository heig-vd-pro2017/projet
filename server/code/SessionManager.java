import java.sql.Timestamp;
import java.util.ArrayList;
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
        LOG.info("New session with id " + s.getId() + " stored.");
    }

    /**
     * Remove session with the id passed in parameter
     *
     * @param id
     */
    public void removeSessionById(int id) {
        for (Session s : sessions) {
            if (s.getId() == id) {
                sessions.remove(s);
                LOG.info("Session with id " + s.getId() + " deleted.");
            }
        }
    }

    public boolean idAlreadyStored(int id) {
        for (Session s : sessions) {
            if (s.getId() == id) {
                s.updateDateAdded();
                return true;
            }
        }
        return false;
    }

    public void deleteObsoleteSessions() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for(Session s : sessions) {
            if(now.getTime() - s.getDateAdded().getTime() > 1000 * 60 * 60 * hoursOfSession) {

            // for tests you can uncomment this line and comment the line on top of this comment
            // if (now.getTime() - s.getDateAdded().getTime() > 3000) {

                LOG.info("Session with id " + s.getId() + " removed because obsolete.");
                sessions.remove(s);
            }
        }
    }

    public void setHoursOfSession(int hours) {
        hoursOfSession = hours;
    }
}
