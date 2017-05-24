package ch.tofind.commusica.media;

import ch.tofind.commusica.playlist.PlaylistTrack;

import java.util.List;

public interface IPlaylist {

    /**
     * @brief Add a track to the EphemeralPlaylist. It also create a PlaylistTrack to link the
     * Track and the playlist (for DB purpose).
     *
     * @param track The track to add to the playlist.
     *
     * @return true if the track was added false otherwise.
     */
    boolean addTrack(Track track);

    boolean contains(Track track);

    /**
     * @brief Get the PlaylistTrack object where the Track is the one passed by parameter.
     *
     * @param track The Track which we will search in the PlaylistTrack list.
     *
     * @return The PlaylistTrack object where the Track is the one passed by parameter, `null` otherwise.
     */
    PlaylistTrack getPlaylistTrack(Track track);

    List<PlaylistTrack> getTracksList();

    boolean isSaved();
}
