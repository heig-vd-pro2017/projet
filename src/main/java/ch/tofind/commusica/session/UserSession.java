package ch.tofind.commusica.session;

import java.util.Date;
import java.util.HashSet;

public class UserSession {

    //! ID of the session
    private String id;

    //! Last time the session was updated
    private Date updated;

    //! List of the id of the upvoted tracks by this session
    private HashSet<String> tracksUpvotedIdsList;

    //!
    private HashSet<String> tracksDownvotedIdsList;

    /**
     * @brief Create a session
     * @param id ID of the session
     */
    public UserSession(String id) {
        this.id = id;
        this.updated = new Date();
        this.tracksUpvotedIdsList = new HashSet<>();
        this.tracksDownvotedIdsList = new HashSet<>();
    }

    /**
     * @brief Get the session's ID
     * @return ID of the session
     */
    public String getId() {
        return id;
    }

    /**
     * @brief Get the session's last update
     * @return Date when the session was updated
     */
    public Date getUpdate() {
        return updated;
    }

    public HashSet<String> getTracksUpvotedIdsList() {
        return tracksUpvotedIdsList;
    }

    public HashSet<String> getTracksDownvotedIdsList() {
        return tracksDownvotedIdsList;
    }

    /**
     * @brief Update the session
     */
    public void update() {
        updated = new Date();
    }

    public void addTrackUpvotedId(String id) {
        tracksUpvotedIdsList.add(id);
        tracksDownvotedIdsList.remove(id);
    }

    public void addTrackDownvotedId(String id) {
        tracksDownvotedIdsList.add(id);
        tracksUpvotedIdsList.remove(id);
    }

    public void removeTrackUpvotedId(String id) {
        tracksUpvotedIdsList.remove(id);
    }

    public void removeTrackDownvotedId(String id) {
        tracksDownvotedIdsList.remove(id);
    }
}
