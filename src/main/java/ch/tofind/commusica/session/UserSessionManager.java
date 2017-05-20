package ch.tofind.commusica.session;

import ch.tofind.commusica.core.ApplicationProtocol;

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
    private Map<String, UserSession> activeSessions;

    //! Store the inactive sessions
    private Map<String, UserSession> inactiveSessions;

    //! Clean the old sessions on schedule
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

    public void store(String id) {

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

        for (Map.Entry<String, UserSession> entry : activeSessions.entrySet()) {

            UserSession userSession = entry.getValue();

            if (userSession.getUpdate().getTime() > now.getTime() - SESSION_TIME_BEFORE_INACTIVE) {
                activeSessions.remove(userSession.getId());
                inactiveSessions.put(userSession.getId(), userSession);
            }
        }
    }

    /**
     * @brief this is used to update the votes lists of the user session passed in parameter
     * it return the number of vote to add to the track depending on the current state of the lists
     *
     * @param userSessionId the UserSession id which ask for a vote
     * @param trackToVoteId the Track id to vote
     * @param typeOfVote type og the vote: UPVOTE_TRACK or DOWNVOTE_TRACK
     *
     * @return the number to add to the current votes of the track
     */
    public int updateVoteList(String userSessionId, String trackToVoteId, String typeOfVote) {
        UserSession userSession = activeSessions.get(userSessionId);

        int numberOfVotesToReturn = 0;

        // if the vote type is an upvote
        if (typeOfVote.equals(ApplicationProtocol.UPVOTE_TRACK)) {
            // if the track was already updated by this UserSession it cancel the upvote
            if (userSession.getTracksUpvotedIdsList().contains(trackToVoteId)) {
                numberOfVotesToReturn = -1;
                userSession.removeTrackUpvotedId(trackToVoteId);
                return numberOfVotesToReturn;
            }
            // if the track is currently downvoted by this session
            else if (userSession.getTracksDownvotedIdsList().contains(trackToVoteId)) {
                numberOfVotesToReturn = 2;
            }
            // if the track had no vote by this session
            else {
                numberOfVotesToReturn = 1;
            }
            userSession.addTrackUpvotedId(trackToVoteId);
        }
        // if the vote type is an downvote
        else if (typeOfVote.equals(ApplicationProtocol.DOWNVOTE_TRACK)) {
            // if the track was already downvoted by this session it cancels the downvote
            if (userSession.getTracksDownvotedIdsList().contains(trackToVoteId)) {
                numberOfVotesToReturn = 1;
                userSession.removeTrackDownvotedId(trackToVoteId);
                return numberOfVotesToReturn;
            }
            // if the track was currently upvoted by this session
            else if (userSession.getTracksUpvotedIdsList().contains(trackToVoteId)) {
                numberOfVotesToReturn = -2;
            }
            // if the track had no downvote by this session
            else {
                numberOfVotesToReturn = -1;
            }
            userSession.addTrackDownvotedId(trackToVoteId);
        }
        return numberOfVotesToReturn;
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
