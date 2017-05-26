package ch.tofind.commusica.playlist;

import ch.tofind.commusica.media.SavedPlaylist;
import ch.tofind.commusica.media.Track;

import java.io.Serializable;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @brief Class that represents a track linked to a playlist with additional properties.
 */
public class PlaylistTrack implements Serializable {

    //! The ID (link between the track and the playlist).
    private PlaylistTrackId id;

    //! Know if the track has been played or not.
    private BooleanProperty beenPlayedProperty;

    /**
     * Number of votes for the track.
     * This property is only used for Hibernate integration. For other usages, use the property defined below.
     */
    private Integer votes;

    //! Know how much votes this track received.
    private IntegerProperty votesProperty;

    /**
     * @brief Empty constructor for Hibernate.
     */
    private PlaylistTrack() {

    }

    /**
     * @brief Create a PlaylistTrack with it's properties.
     *
     * @param playlist The playlist.
     * @param track The track.
     */
    public PlaylistTrack(SavedPlaylist playlist, Track track) {
        this.id = new PlaylistTrackId(playlist, track);
        this.beenPlayedProperty = new SimpleBooleanProperty(false);
        this.votes = 0;
        this.votesProperty = new SimpleIntegerProperty(this.votes);

        this.votesProperty.addListener(((observable, oldValue, newValue) -> {
            this.votes = newValue.intValue();
        }));
    }

    /**
     * @brief Know if a PlaylistTrack has already been played or not.
     *
     * @return A BooleanProperty telling if the PlaylistTrack has been played or not.
     */
    public BooleanProperty getBeenPlayedProperty() {
        return beenPlayedProperty;
    }

    /**
     * @brief Get the playlist of the PlaylistTrack.
     *
     * @return The playlist of the PlaylistTrack.
     */
    public SavedPlaylist getPlaylist() {
        return id.getPlaylist();
    }

    /**
     * @brief Get the track of the PlaylistTrack.
     *
     * @return The track of the PlaylistTrack.
     */
    public Track getTrack() {
        return id.getTrack();
    }

    /**
     * @brief Get the number of votes of the PlaylistTrack.
     *
     * @return The number of votes of the PlaylistTrack.
     */
    public IntegerProperty getVotesProperty() {
        return votesProperty;
    }

    /**
     * @brief Get the ID of the PlaylistTrack.
     *
     * @return The ID of the PlaylistTrack.
     */
    public PlaylistTrackId getId() {
        return id;
    }

    /**
     * @brief Upvote the current PlaylistTrack.
     */
    public void upvote() {
        if (!beenPlayedProperty.getValue()) {
            votesProperty.setValue(votesProperty.intValue() + 1);
        }
    }

    /**
     * @brief Downvote the current PlaylistTrack.
     */
    public void downvote() {
        if (!beenPlayedProperty.getValue()) {
            votesProperty.setValue(votesProperty.intValue() - 1);
        }
    }

    /**
     * @brief Update the PlaylistTrack.
     */
    public void update() {
        beenPlayedProperty.setValue(true);
    }

    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }

        if (!(object instanceof PlaylistTrack)) {
            return false;
        }

        PlaylistTrack playlistTrack = (PlaylistTrack) object;

        return Objects.equals(id, playlistTrack.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {

        return "PlaylistTrack"                           + '\n' + '\t' +
               "SavedPlaylist: " + id.getPlaylist().getName() + '\n' + '\t' +
               "Track...: " + id.getTrack().getTitle()   + '\n' + '\t' +
               "Votes...: " + votes                      + '\n';
    }

}
