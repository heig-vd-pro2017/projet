package ch.tofind.commusica.playlist;

import ch.tofind.commusica.media.SavedPlaylist;
import ch.tofind.commusica.media.Track;

import java.io.Serializable;
import java.util.Objects;

public class PlaylistTrackId implements Serializable {

    //! SavedPlaylist related to the track
    private SavedPlaylist playlist;

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
    public PlaylistTrackId(SavedPlaylist playlist, Track track) {
        this.playlist = playlist;
        this.track = track;

    }
    public void setPlaylist(SavedPlaylist playlist) {
        this.playlist = playlist;
    }

    public SavedPlaylist getPlaylist() {
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
