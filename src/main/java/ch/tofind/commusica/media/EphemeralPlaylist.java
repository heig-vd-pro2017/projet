package ch.tofind.commusica.media;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.ObservableSortedPlaylistTrackList;
import javafx.application.Platform;

/**
 * @brief This class represents a playlist that is currently constructed.
 * This is the "Playing" playlist in the UI.
 */
public class EphemeralPlaylist implements IPlaylist {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(EphemeralPlaylist.class.getSimpleName());

    //! Playlist's tracks.
    private ObservableSortedPlaylistTrackList tracksList;

    //! The playlist that will be saved into the database for keeping track of this one.
    private SavedPlaylist delegate;

    /**
     * @brief Create the Playing playlist.
     *
     * @param playlistName The name of the playlist.
     */
    public EphemeralPlaylist(String playlistName) {
        // TODO: Choose the playlist name
        delegate = new SavedPlaylist(playlistName);

        tracksList = new ObservableSortedPlaylistTrackList();
    }

    /**
     * @brief Downvote a track in the playlist.
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
     * @brief Upvote a track in the playlist.
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
     * @brief Get the playing playlist's name.
     *
     * @return The name of the playlist.
     */
    public String getName() {
        return delegate.getName();
    }

    /**
     * Updates the current playlist from the playlist given as parameter.
     *
     * @param playlist The playlist to update from.
     */
    public void updateFrom(EphemeralPlaylist playlist) {
        delegate.setName(playlist.getName());

        playlist.tracksList.forEach(item -> {
            PlaylistTrack playlistTrack = getPlaylistTrack(item.getTrack());
            // If the track isn't in the playlist yet, we add it.
            if (playlistTrack == null) {
                addTrack(item.getTrack());
            }
            // Otherwise, we update the votes value.
            else {
                // We tell the JavaFX thread to execute this method. If we don't an exception will be raised since we
                // are not on the same thread.
                Platform.runLater(() -> playlistTrack.getVotesProperty().setValue(item.getVotesProperty().getValue()));
            }
        });

        save();
    }

    /**
     * @brief Save the ephemeral playlist into the database.
     *
     * Beware, this method has an effect only if it's the one stored by the PlaylistManager.
     */
    public void save() {
        if (PlaylistManager.getInstance().getPlaylist() == this) {
            DatabaseManager.getInstance().save(delegate);
        }

    }

    public void saveTrack(Track track) {
        PlaylistTrack playlistTrack = getPlaylistTrack(track);

        if (playlistTrack != null) {
            DatabaseManager.getInstance().save(playlistTrack);
        }
    }

    @Override
    public String toString() {
        return delegate.getName() + "\n" +
                tracksList;
    }

    @Override
    public boolean addTrack(Track track) {
        // Check that the playlist doesn't already contains the track.
        if (tracksList.stream().noneMatch(p -> p.getTrack().equals(track))) {
            if (PlaylistManager.getInstance().getPlaylist() == this) {
                DatabaseManager.getInstance().save(track);
            }

            // Create the PlaylistTrack object by associating it with the static playlist.
            PlaylistTrack playlistTrack = new PlaylistTrack(delegate, track);
            tracksList.add(playlistTrack);

            return true;
        }

        return false;
    }

    @Override
    public boolean contains(Track track) {
        return tracksList.stream().anyMatch(p -> p.getTrack().equals(track));
    }

    @Override
    public PlaylistTrack getPlaylistTrack(Track track) {
        return tracksList.stream().filter(p -> p.getTrack().equals(track)).findFirst().orElse(null);
    }

    @Override
    public ObservableSortedPlaylistTrackList getTracksList() {
        return tracksList;
    }

    @Override
    public boolean isSaved() {
        return false;
    }
}
