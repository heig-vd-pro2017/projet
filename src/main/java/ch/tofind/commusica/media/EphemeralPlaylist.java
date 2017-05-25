package ch.tofind.commusica.media;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.ObservableSortedPlaylistTrackList;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

/**
 * @brief This class represents a playlist that is currently constructed.
 * This is the "Playing" playlist in the UI.
 */
public class EphemeralPlaylist implements IPlaylist {

    private List<Track> dbTracks;

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

        dbTracks = retrieveDbTracks();
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

    public void save() {
        if (PlaylistManager.getInstance().getPlaylist() == this) {
            DatabaseManager.getInstance().save(delegate);

            dbTracks = retrieveDbTracks();
        }
    }

    public void saveTrack(Track track) {
        PlaylistTrack playlistTrack = getPlaylistTrack(track);

        if (playlistTrack != null) {
            DatabaseManager.getInstance().save(playlistTrack);
        }
    }

    public void setName(String name) {
        delegate.setName(name);
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
            if (dbTracks.stream().noneMatch(item -> item.getId().equals(track.getId()))) {
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

    private List<Track> retrieveDbTracks() {
        Session session = DatabaseManager.getInstance().getSession();

        Query<Track> query = session.createQuery("from Track", Track.class);

        return query.list();
    }
}
