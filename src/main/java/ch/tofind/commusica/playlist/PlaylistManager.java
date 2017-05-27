package ch.tofind.commusica.playlist;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.EphemeralPlaylist;
import ch.tofind.commusica.media.SavedPlaylist;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;

import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

/**
 * @brief Class used to manages playlist.
 */
public class PlaylistManager {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(PlaylistManager.class.getSimpleName());

    //! Shared instance of the playlist for all the application
    private static PlaylistManager instance = null;

    //! All saved playlists
    private List<SavedPlaylist> savedPlaylists;

    //! SavedPlaylist currently running
    private EphemeralPlaylist playlist;

    //! SavedPlaylist containing favorited tracks.
    private SavedPlaylist favoritesPlaylist;

    /**
     * PlaylistManager single constructor. Avoid the instantiation.
     */
    private PlaylistManager() {
        String playlistName = ApplicationProtocol.serverName;
        playlist = new EphemeralPlaylist(playlistName);
        savedPlaylists = retrievePlaylists();
        favoritesPlaylist = retrieveFavoritesPlaylist();
    }

    /**
     * @brief Get the object instance.
     *
     * @return The instance of the object.
     */
    public static PlaylistManager getInstance() {
        if (instance == null) {
            synchronized (PlaylistManager.class) {
                if (instance == null) {
                    instance = new PlaylistManager();
                }
            }
        }

        return instance;
    }

    /**
     * @brief Returns the ephemeral playlist currently playing.
     *
     * @return The ephemeral playlist currently playing.
     */
    public EphemeralPlaylist getPlaylist() {
        return playlist;
    }

    /**
     * @brief Creates a playlist with the given name as parameter.
     *
     * The playlist is automatically saved into the database and added
     * to manager list.
     *
     * @param name The name of the new playlist.
     *
     * @return The newly created playlist.
     */
    public SavedPlaylist createPlaylist(String name) {
        SavedPlaylist playlist = new SavedPlaylist(name);
        DatabaseManager.getInstance().save(playlist);

        savedPlaylists.add(playlist);

        return playlist;
    }

    /**
     * @brief Removes an existing playlist.
     *
     * The playlist is automatically removed from the database and from
     * the manager list.
     *
     * @param playlist The playlist to remove.
     */
    public void removePlaylist(SavedPlaylist playlist) {
        savedPlaylists.remove(playlist);

        DatabaseManager.getInstance().delete(playlist);
    }

    /**
     * @brief Returns the list of the saved playlists.
     *
     * Every time it is invoked, this method will ask the database
     * the list of saved playlists.
     *
     * @return The list of the saved playlists.
     */
    public List<SavedPlaylist> getSavedPlaylists() {
        savedPlaylists = retrievePlaylists();

        return savedPlaylists;
    }

    /**
     * @brief Returns the Favorites playlist.
     *
     * @return The Favorites playlist.
     */
    public SavedPlaylist getFavoritesPlaylist() {
        return favoritesPlaylist;
    }

    /**
     * @brief Adds the given track in the Favorites playlist.
     *
     * This method will automatically save the track passed as parameter
     * if it is not already in the database.
     *
     * @param track The track to add to the Favorites playlist.
     */
    public void addTrackToFavorites(Track track) {
        // Save the track if it isn't already done.
        DatabaseManager.getInstance().getSession().saveOrUpdate(track);

        PlaylistTrack playlistTrack = new PlaylistTrack(favoritesPlaylist, track);

        DatabaseManager.getInstance().save(playlistTrack);
    }

    /**
     * @brief Removes the given track form the Favorites playlist.
     *
     * This method will automatically delete the association between the
     * track and the Favorites playlist from the database.
     *
     * If the currently showed playlist is the Favorites one, then the
     * view is automatically refreshed.
     *
     * @param track The track to remove from the Favorites playlist.
     */
    public void removeTrackFromFavorites(Track track) {
        PlaylistTrack playlistTrack = favoritesPlaylist.getPlaylistTrack(track);

        if (playlistTrack != null) {
            DatabaseManager.getInstance().delete(playlistTrack);
            favoritesPlaylist.getTracksList().remove(playlistTrack);
        }

        // We need to refresh the view since it's not an Observable.
        UIController.getController().refreshPlaylist();
    }

    /**
     * @brief Returns the Favorites playlist.
     *
     * @return The Favorites playlist.
     */
    private SavedPlaylist retrieveFavoritesPlaylist() {
        Session session = DatabaseManager.getInstance().getSession();

        // Our implementation is made so that the playlist containing favorited songs has the ID '0'.
        Query<SavedPlaylist> fetchQuery = session.createQuery("from SavedPlaylist where id = '0'", SavedPlaylist.class);

        // Retrieve the playlist.
        SavedPlaylist playlist = null;
        try {
            playlist = fetchQuery.getSingleResult();
        } catch (NoResultException e1) {
            // If the playlist wasn't found, we create it and we update the database.
            LOG.warning("Favorites playlist doesn't exist. Creating it...");

            // Since we can't set an ID for a playlist (and changing it would be idiot), we use a native SQL query
            // to create the Favorites playlist with the ID '0'.
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                String queryString = String.format("INSERT INTO playlist (id, date_added, name, version) VALUES (0, %d, 'Favorites', 0);", new Date().getTime());
                NativeQuery<SavedPlaylist> createQuery = session.createNativeQuery(queryString, SavedPlaylist.class);
                createQuery.executeUpdate();
                transaction.commit();

                LOG.info("Favorites playlist successfully created.");
                playlist = fetchQuery.getSingleResult();
            } catch (HibernateException e2) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e2.printStackTrace();
            }
        }

        return playlist;
    }

    /**
     * @brief Returns the list of saved playlists.
     *
     * @return The list of saved playlists.
     */
    private List<SavedPlaylist> retrievePlaylists() {
        Session session = DatabaseManager.getInstance().getSession();

        // Retrieve all playlists but the favorites one.
        Query<SavedPlaylist> query = session.createQuery("from SavedPlaylist where id > '0'", SavedPlaylist.class);

        return query.list();
    }
}
