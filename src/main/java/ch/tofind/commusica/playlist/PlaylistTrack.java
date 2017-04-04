/*
    https://blog.axopen.com/2013/11/les-cles-primaires-composees-avec-hibernate-4/
    https://vladmihalcea.com/2016/08/01/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/
*/
package ch.tofind.commusica.playlist;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

public class PlaylistTrack implements Serializable {

    private PlaylistTrackId id;

    //! Number of votes for the track
    private Integer votes;

    /**
     * Create a link between playlist and track
     * @param playlistId
     * @param trackId
     */
    public PlaylistTrack(Playlist playlist, Track track) {
        this.id = new PlaylistTrackId(playlist, track);
        this.votes = 0;
    }

    public PlaylistTrack() {

    }

    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }

        if (!(object instanceof Track)) {
            return false;
        }

        PlaylistTrack playlistTrack = (PlaylistTrack) object;

        return Objects.equals(id, playlistTrack.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Integer getVotes() {
        return votes;
    }

    public void upvote() {
        votes++;
    }

    public void downvote() {
        votes--;
    }

    public Playlist getPlaylist() {
        return id.getPlaylist();
    }

    public Track getTrack() {
        return id.getTrack();
    }

}
