/*
    https://blog.axopen.com/2013/11/les-cles-primaires-composees-avec-hibernate-4/
    https://vladmihalcea.com/2016/08/01/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/
*/
package ch.tofind.commusica.playlist;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import ch.tofind.commusica.database.DatabaseObject;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import java.util.Objects;

public class PlaylistTrack implements DatabaseObject {

    private PlaylistTrackId id;

    //! Number of votes for the track
    private Integer votes;

    /**
     * Create a link between playlist and track
     * @param playlist
     * @param track
     */
    public PlaylistTrack(Playlist playlist, Track track) {
        this.id = new PlaylistTrackId(playlist, track);
        this.votes = 0;
    }

    public PlaylistTrack() {

    }

    public Playlist getPlaylist() {
        return id.getPlaylist();
    }

    public Track getTrack() {
        return id.getTrack();
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
               "Playlist: " + id.getPlaylist().getName() + '\n' + '\t' +
               "Track...: " + id.getTrack().getTitle()   + '\n' + '\t' +
               "Votes...: " + votes                      + '\n';
    }

    @Override
    public void update() {

    }
}
