package ch.tofind.commusica.playlist;

import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistTrackId;

import java.io.Serializable;
import java.util.Objects;

public class PlaylistTrackId implements Serializable {

    //! Playlist related to the track
    private Playlist playlist;

    //! Track related to the playlist
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

    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }

        if (!(object instanceof PlaylistTrackId)) {
            return false;
        }

        PlaylistTrackId playlistTrackId = (PlaylistTrackId) object;


        return Objects.equals(playlist, playlistTrackId.playlist) &&
                Objects.equals(track, playlistTrackId.track);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlist, track);
    }
}
