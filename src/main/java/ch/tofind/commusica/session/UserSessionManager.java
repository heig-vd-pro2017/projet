package ch.tofind.commusica.session;

import ch.tofind.commusica.utils.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manage the users sessions.
 */
public class UserSessionManager implements ISessionManager {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(UserSessionManager.class.getSimpleName());

    //! Shared instance of the object for all the application.
    private static UserSessionManager instance = null;

    //! Store the active sessions.
    private Map<Integer, UserSession> activeSessions;

    //! Store the inactive sessions.
    private Map<Integer, UserSession> inactiveSessions;

    //! Users who asked to play/pause the current track.
    private Set<Integer> usersAskedForPlayPause;

    //! Users who asked to change the track.
    private Set<Integer> usersAskedForNextTrack;

    //! Users who asked to change the track.
    private Set<Integer> usersAskedForPreviousTrack;

    //! Users who asked to turn up the volume.
    private Set<Integer> usersAskedToTurnVolumeUp;

    //! Users who asked to turn down the volume.
    private Set<Integer> usersAskedToTurnVolumeDown;

    //! Clean the old sessions on schedule.
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * UserSessionManager single constructor. Avoid the instantiation.
     */
    private UserSessionManager() {

        activeSessions = new HashMap<>();
        inactiveSessions = new HashMap<>();

        usersAskedForPlayPause = new HashSet<>();
        usersAskedForNextTrack = new HashSet<>();
        usersAskedForPreviousTrack = new HashSet<>();
        usersAskedToTurnVolumeUp = new HashSet<>();
        usersAskedToTurnVolumeDown = new HashSet<>();

        // Crée un thread qui nettoie les sessions toutes les N secondes
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            deleteObsoleteSessions();
        }, 0, TIME_BEFORE_SESSION_INACTIVE, TimeUnit.SECONDS);
    }

    /**
     * @brief Get the object instance.
     *
     * @return The instance of the object.
     */
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
     * @brief Count the inactive sessions.
     *
     * @return The number of inactive sessions.
     */
    public int countInactiveSessions() {
        return inactiveSessions.size();
    }

    /**
     * @brief Count the requests to play/pause the current track.
     *
     * @return The number of request to play/pause the current track.
     */
    public int countPlayPauseRequests() {
        return usersAskedForPlayPause.size();
    }

    /**
     * @brief Count the requests for the next track.
     *
     * @return The number of request for the next track.
     */
    public int countNextTrackRequests() {
        return usersAskedForNextTrack.size();
    }

    /**
     * @brief Count the requests for the previous track.
     *
     * @return The number of request for the previous track.
     */
    public int countPreviousTrackRequests() {
        return usersAskedForPreviousTrack.size();
    }

    /**
     * @brief Count the requests to turn the volume up.
     *
     * @return The number of request to turn the volume up.
     */
    public int countTurnVolumeUpRequests() {
        return usersAskedToTurnVolumeUp.size();
    }

    /**
     * @brief Count the requests to turn the volume down.
     *
     * @return The number of request to turn the volume down.
     */
    public int countTurnVolumeDownRequests() {
        return usersAskedToTurnVolumeDown.size();
    }

    /**
     * @brief Store the fact that a user would like to play/pause the current track.
     *
     * @param userId The user which ask to play/pause the current track.
     */
    public void playPause(Integer userId) {

        store(userId);

        usersAskedForPlayPause.add(userId);
    }

    /**
     * @brief Store the fact that a user would like the next track.
     *
     * @param userId The user which ask for the next track.
     */
    public void previousTrack(Integer userId) {

        store(userId);

        usersAskedForPreviousTrack.add(userId);
    }

    /**
     * @brief Store the fact that a user would like the previous track.
     *
     * @param userId The user which ask for the previous track.
     */
    public void nextTrack(Integer userId) {

        store(userId);

        usersAskedForNextTrack.add(userId);
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

        if (userSession.getDownvotedTracks().contains(trackId)) {
            userSession.getDownvotedTracks().remove(trackId);
        } else {
            userSession.addUpvotedTrack(trackId);
        }

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

        if (userSession.getUpvotedTracks().contains(trackId)) {
            userSession.getUpvotedTracks().remove(trackId);
        } else {
            userSession.addDownvotedTrack(trackId);
        }

    }

    /**
     * @brief Store the fact that a user would like to turn the volume up.
     *
     * @param userId The user which ask for to turn the volume up.
     */
    public void turnVolumeUp(Integer userId) {

        store(userId);

        usersAskedToTurnVolumeUp.add(userId);
    }

    /**
     * @brief Store the fact that a user would like to turn the volume down.
     *
     * @param userId The user which ask for to turn the volume down.
     */
    public void turnVolumeDown(Integer userId) {

        store(userId);

        usersAskedToTurnVolumeDown.add(userId);
    }

    /**
     * @brief Reset all requests for the next track.
     */
    public void resetNextTrackRequests() {
        usersAskedForNextTrack.clear();
    }

    /**
     * @brief Reset all requests for the previous track.
     */
    public void resetPreviousTrackRequests() {
        usersAskedForPreviousTrack.clear();
    }

    /**
     * @brief Reset all requests to turn the volume up.
     */
    public void resetTurnVolumeUpRequests() {
        usersAskedToTurnVolumeUp.clear();
    }

    /**
     * @brief Reset all requests to turn the volume down.
     */
    public void resetTurnVolumeDownRequests() {
        usersAskedToTurnVolumeDown.clear();
    }

    /**
     * @brief Reset all requests to turn the volume down.
     */
    public void resetPlayPauseRequests() {
        usersAskedForPlayPause.clear();
    }

    /**
     * @brief Delete the sessions that are not active anymore.
     */
    private void deleteObsoleteSessions() {

        Date now = new Date();

        for (Map.Entry<Integer, UserSession> entry : activeSessions.entrySet()) {

            UserSession userSession = entry.getValue();

            if (userSession.getLastUpdate().getTime() > now.getTime() - TIME_BEFORE_SESSION_INACTIVE) {

                LOG.info("Removing old user session.");

                activeSessions.remove(userSession.getId());

                usersAskedForNextTrack.remove(userSession.getId());
                usersAskedToTurnVolumeUp.remove(userSession.getId());
                usersAskedToTurnVolumeDown.remove(userSession.getId());

                inactiveSessions.put(userSession.getId(), userSession);
            }
        }
    }

    @Override
    public void stop() {
        
        scheduledExecutorService.shutdown();

        instance = null;
    }
}
