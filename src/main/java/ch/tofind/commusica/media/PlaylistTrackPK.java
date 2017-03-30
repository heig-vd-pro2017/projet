package ch.tofind.commusica.media;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PlaylistTrackPK implements Serializable {

    //! Playlist ID related to the track
    @Column(name="playlist_id")
    private Integer playlistId;

    //! Track ID related to the playlist
    @Column(name="track_id")
    private Integer trackId;

    /**
     * Create a link between playlist and track
     * @param playlistId
     * @param trackId
     */
    public PlaylistTrackPK(Integer playlistId, Integer trackId) {
        this.playlistId = playlistId;
        this.trackId = trackId;
    }

    /**
     * Create a link between playlist and track
     * @param playlistId
     * @param trackId
     */
    public PlaylistTrackPK() {
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }
}
