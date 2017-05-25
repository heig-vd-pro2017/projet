package ch.tofind.commusica.playlist;

import ch.tofind.commusica.media.SavedPlaylist;
import ch.tofind.commusica.media.Track;

import java.io.Serializable;
import java.util.Objects;

/**
 * @brief Class that makes a link between a track and a playlist
 */
public class PlaylistTrackId implements Serializable {

    //! SavedPlaylist related to the track.
    private SavedPlaylist playlist;

    //! Track related to the playlist.
    private Track track;

    /**
     * @brief Create a link between playlist and track.
     */
    public PlaylistTrackId() {

    }

    /**
     * @brief Create a link between a playlist and a track.
     *
     * @param playlist The playlist.
     * @param track The track.
     */
    public PlaylistTrackId(SavedPlaylist playlist, Track track) {
        this.playlist = playlist;
        this.track = track;

    }

    /**
     * @brief Set the playlist of the PlaylistTrack.
     *
     * @param playlist The playlist.
     */
    public void setPlaylist(SavedPlaylist playlist) {
        this.playlist = playlist;
    }

    /**
     * @brief Get the playlist of the PlaylistTrack.
     *
     * @return The playlist.
     */
    public SavedPlaylist getPlaylist() {
        return playlist;
    }

    /**
     * @brief Set the track of the PlaylistTrack.
     *
     * @param track The track.
     */
    public void setTrack(Track track) {
        this.track = track;
    }

    /**
     * @brief Get the track of the PlaylistTrack.
     *
     * @return The track.
     */
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
