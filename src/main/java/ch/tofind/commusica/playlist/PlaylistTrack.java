/*
    https://blog.axopen.com/2013/11/les-cles-primaires-composees-avec-hibernate-4/
    https://vladmihalcea.com/2016/08/01/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/
*/
package ch.tofind.commusica.playlist;

import ch.tofind.commusica.media.SavedPlaylist;
import ch.tofind.commusica.media.Track;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.Serializable;
import java.util.Objects;

public class PlaylistTrack implements Serializable {

    private PlaylistTrackId id;

    private BooleanProperty beenPlayedProperty;

    /**
     * Number of votes for the track.
     * This property is only used for Hibernate integration. For other usages, use the property defined below.
     */
    private Integer votes;

    private IntegerProperty votesProperty;

    /**
     * Create a link between playlist and track
     * @param playlist
     * @param track
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

    public BooleanProperty getBeenPlayedProperty() {
        return beenPlayedProperty;
    }

    public SavedPlaylist getPlaylist() {
        return id.getPlaylist();
    }

    public Track getTrack() {
        return id.getTrack();
    }

    public IntegerProperty getVotesProperty() {
        return votesProperty;
    }

    public void upvote() {
        if (!beenPlayedProperty.getValue()) {
            votesProperty.setValue(votesProperty.intValue() + 1);
        }
    }

    public void downvote() {
        if (!beenPlayedProperty.getValue()) {
            votesProperty.setValue(votesProperty.intValue() - 1);
        }
    }

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
