package ch.tofind.commusica.media;

import ch.tofind.commusica.playlist.PlaylistTrack;

import java.util.List;

/**
 * This class must be used by any kind of playlist.
 */
public interface IPlaylist {

    /**
     * Add a track to the playlist. It also create a PlaylistTrack to link the
     * Track and the playlist (for DB purpose).
     *
     * @param track The track to add to the playlist.
     *
     * @return true if the track was added false otherwise.
     */
    boolean addTrack(Track track);

    /**
     * Check if a track is already contained in the playlist or not.
     *
     * @param track The track to check in the playlist.
     * 
     * @return true if the track was found false otherwise.
     */
    boolean contains(Track track);

    /**
     * Get the PlaylistTrack object where the Track is the one passed by parameter.
     *
     * @param track The Track which we will search in the PlaylistTrack list.
     *
     * @return The PlaylistTrack object where the Track is the one passed by parameter, `null` otherwise.
     */
    PlaylistTrack getPlaylistTrack(Track track);

    /**
     * Get the tracks of the playlist.
     *
     * @return The track of the playlist.
     */
    List<PlaylistTrack> getTracksList();

    /**
     * Check if the playlist is saved or not.
     *
     * @return true if the playlist was saved false otherwise.
     */
    boolean isSaved();
}
