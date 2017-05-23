package ch.tofind.commusica.session;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @brief Class representing a user session.
 */
public class UserSession implements ISession {

    //! ID of the session.
    private Integer id;

    //! Last time the session was updated.
    private Date updated;

    //! List of the id of the upvoted tracks by this session.
    private Set<String> upvotedTracks;

    //! List of the id of the downvoted tracks by this session.
    private Set<String> downvotedTracks;

    /**
     * @brief Create a session.
     *
     * @param id ID of the session.
     */
    public UserSession(Integer id) {
        this.id = id;
        this.updated = new Date();
        this.upvotedTracks = new HashSet<>();
        this.downvotedTracks = new HashSet<>();
    }

    /**
     * @brief Get the upvoted tracks from the current user.
     *
     * @return The upvoted trackes.
     */
    public Set<String> getUpvotedTracks() {
        return upvotedTracks;
    }

    /**
     * @brief Get the downvoted tracks from the current user.
     *
     * @return The downvoted trackes.
     */
    public Set<String> getDownvotedTracks() {
        return downvotedTracks;
    }

    /**
     * @brief Add an upvoted track for the current user.
     *
     * @param trackId The ID of the track to upvote.
     */
    public void addUpvotedTrack(String trackId) {
        downvotedTracks.remove(trackId);
        upvotedTracks.add(trackId);
    }

    /**
     * @brief Add an downvoted track for the current user.
     *
     * @param trackId The ID of the track to downvote.
     */
    public void addDownvotedTrack(String trackId) {
        upvotedTracks.remove(trackId);
        downvotedTracks.add(trackId);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void update() {
        updated = new Date();
    }

    @Override
    public Date getLastUpdate() {
        return updated;
    }
}
