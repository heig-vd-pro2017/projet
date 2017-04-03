/*
    https://blog.axopen.com/2013/11/les-cles-primaires-composees-avec-hibernate-4/
    https://vladmihalcea.com/2016/08/01/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/
*/
package ch.tofind.commusica.media;

import javax.persistence.*;
import java.io.Serializable;

public class PlaylistTrack {

    private class PlaylistTrackId implements Serializable {

        //! Playlist ID related to the track
        private Playlist playlist;

        //! Track ID related to the playlist
        private Track track;

        /**
         * Create a link between playlist and track
         */
        public PlaylistTrackId() {

        }

        /**
         * Create a link between playlist and track
         * @param playlist
         * @param track
         */
        public PlaylistTrackId(Playlist playlist, Track track) {
            this.playlist = playlist;
            this.track = track;

        }
        public void setPlaylist(Playlist playlist) {
            this.playlist = playlist;
        }

        public Playlist getPlaylist() {
            return playlist;
        }

        public void setTrack(Track track) {
            this.track = track;
        }

        public Track getTrack() {
            return track;
        }
    }

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

    public void upvote() {
        votes++;
    }

    public void downvote() {
        votes--;
    }

}
