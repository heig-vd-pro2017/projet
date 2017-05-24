package ch.tofind.commusica.media;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.ObservableSortedPlaylistTrackList;

public class EphemeralPlaylist implements IPlaylist {

    private ObservableSortedPlaylistTrackList tracksList;

    //! The playlist that will be saved into the database for keeping track of this one.
    private SavedPlaylist delegate;

    public EphemeralPlaylist(String playlistName) {
        // TODO: Choose the playlist name
        delegate = new SavedPlaylist(playlistName);
        DatabaseManager.getInstance().save(delegate);

        tracksList = new ObservableSortedPlaylistTrackList();
    }

    /**
     * @brief Add a track to the EphemeralPlaylist. It also create a PlaylistTrack to link the
     * Track and the playlist (for DB purpose).
     *
     * @param track The track to add to the playlist.
     *
     * @return true if the track was added false otherwise.
     */
    public boolean addTrack(Track track) {
        // Check that the playlist doesn't already contains the track.
        if (tracksList.stream().noneMatch(p -> p.getTrack().equals(track))) {
            // Create the PlaylistTrack object by associating it with the static playlist.
            PlaylistTrack playlistTrack = new PlaylistTrack(delegate, track);
            tracksList.add(playlistTrack);

            return true;
        }

        return false;
    }

    /**
     * @brief Downvote a PlaylistTrack.
     *
     * @param track The Track form the PlaylistTrack to downvote.
     *
     * @return true if the PlaylistTrack was downvoted, false otherwise.
     */
    public boolean downvoteTrack(Track track) {
        PlaylistTrack playlistTrack = getPlaylistTrack(track);

        if (playlistTrack != null) {
            playlistTrack.downvote();

            return true;
        }

        return false;
    }

    /**
     * @brief Upvote a PlaylistTrack.
     *
     * @param track The Track form the PlaylistTrack to upvote.
     *
     * @return true if the PlaylistTrack was upvoted, false otherwise.
     */
    public boolean upvoteTrack(Track track) {
        PlaylistTrack playlistTrack = getPlaylistTrack(track);

        if (playlistTrack != null) {
            playlistTrack.upvote();

            return true;
        }

        return false;
    }

    /**
     * @brief Get the PlaylistTrack object where the Track is the one passed by parameter.
     *
     * @param track The Track which we will search in the PlaylistTrack list.
     *
     * @return The PlaylistTrack object where the Track is the one passed by parameter.
     */
    public PlaylistTrack getPlaylistTrack(Track track) {
        return tracksList.stream().filter(p -> p.getTrack().equals(track)).findFirst().orElse(null);
    }

    public boolean contains(Track track) {
        return tracksList.stream().anyMatch(p -> p.getTrack().equals(track));
    }

    public ObservableSortedPlaylistTrackList getTracksList() {
        return tracksList;
    }

    public boolean isSaved() {
        return false;
    }

    @Override
    public String toString() {
        return delegate.getName() + "\n" +
                tracksList;
    }

    public String getName() {
        return delegate.getName();
    }
}
