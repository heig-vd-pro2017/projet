package ch.tofind.commusica.session;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.utils.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manage the users sessions.
 */
public class UserSessionManager implements ISessionManager {

    //! Shared instance of the object for all the application.
    private static UserSessionManager instance = null;

    //! Store the active sessions.
    private Map<Integer, UserSession> activeSessions;

    //! Store the inactive sessions.
    private Map<Integer, UserSession> inactiveSessions;

    //! Clean the old sessions on schedule.
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * UserSessionManager single constructor. Avoid the instantiation.
     */
    private UserSessionManager() {

        activeSessions = new HashMap<>(0);
        inactiveSessions = new HashMap<>(0);

        // Crée un thread qui nettoie les sessions toutes les N secondes
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            deleteObsoleteSessions();
        }, 0, TIME_BEFORE_SESSION_INACTIVE, TimeUnit.SECONDS);
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

    /**
     * @brief Store a new session.
     *
     * @param id ID of the new session.
     */
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

    /**
     * @brief Count the active sessions.
     *
     * @return The number of active sessions.
     */
    public int countActiveSessions() {
        return activeSessions.size();
    }

    /**
     * @brief Avoid that a track can be upvoted twice by the same user.
     *
     * @param userId The user which ask for a vote.
     * @param trackId The track to vote.
     *
     * @throws Exception If the track has already been upvoted by this user.
     */
    public void upvote(Integer userId, String trackId) throws Exception {

        store(userId);

        UserSession userSession = activeSessions.get(userId);

        if (userSession.getUpvotedTracks().contains(trackId)) {
            throw new Exception("Track has already been upvoted by this user.");
        }

        userSession.addUpvotedTrack(trackId);

    }

    /**
     * @brief Avoid that a track can be downvoted twice by the same user.
     *
     * @param userId The user which ask for a vote.
     * @param trackId The track to vote.
     *
     * @throws Exception If the track has already been downvoted by this user.
     */
    public void downvote(Integer userId, String trackId) throws Exception {

        store(userId);

        UserSession userSession = activeSessions.get(userId);

        if (userSession.getDownvotedTracks().contains(trackId)) {
            throw new Exception("Track has already been downvoted by this user.");
        }

        userSession.addDownvotedTrack(trackId);

    }

    /**
     * @brief Delete the sessions that are not active anymore.
     */
    private void deleteObsoleteSessions() {

        Date now = new Date();

        for (Map.Entry<Integer, UserSession> entry : activeSessions.entrySet()) {

            UserSession userSession = entry.getValue();

            if (userSession.getLastUpdate().getTime() > now.getTime() - TIME_BEFORE_SESSION_INACTIVE) {
                activeSessions.remove(userSession.getId());
                inactiveSessions.put(userSession.getId(), userSession);
            }
        }
    }

    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
