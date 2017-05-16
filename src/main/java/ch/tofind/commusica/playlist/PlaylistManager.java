package ch.tofind.commusica.playlist;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.EphemeralPlaylist;
import ch.tofind.commusica.media.SavedPlaylist;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.utils.Logger;

import java.util.List;

import javax.persistence.NoResultException;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

public class PlaylistManager {

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
        playlist = new EphemeralPlaylist();
        savedPlaylists = retrievePlaylists();
        favoritesPlaylist = retrieveFavoritesPlaylist();
    }

    /**
     * @brief Get the object instance.
     *
     * @return The instance of the object.
     */
    public static PlaylistManager getInstance() {

        if(instance == null) {
            synchronized (PlaylistManager.class) {
                if (instance == null) {
                    instance = new PlaylistManager();
                }
            }
        }

        return instance;
    }

    public EphemeralPlaylist getPlaylist() {
        return playlist;
    }

    public SavedPlaylist createPlaylist(String name) {
        SavedPlaylist playlist = new SavedPlaylist(name);
        DatabaseManager.getInstance().save(playlist);

        savedPlaylists.add(playlist);

        return playlist;
    }

    public void removePlaylist(SavedPlaylist playlist) {
        savedPlaylists.remove(playlist);

        DatabaseManager.getInstance().delete(playlist);
    }

    public List<SavedPlaylist> getSavedPlaylists() {
        return savedPlaylists;
    }

    public SavedPlaylist getFavoritesPlaylist() {
        return favoritesPlaylist;
    }

    public void addTrackToFavorites(Track track) {
        PlaylistTrack playlistTrack = new PlaylistTrack(favoritesPlaylist, track);

        DatabaseManager.getInstance().save(playlistTrack);
    }

    public void removeTrackFromFavorites(Track track) {
        List<PlaylistTrack> list = favoritesPlaylist.getTracksList();

        for (int i = 0; i < list.size(); ++i) {
            PlaylistTrack playlistTrack = list.get(i);
            if (playlistTrack.getTrack().equals(track)) {
                DatabaseManager.getInstance().delete(playlistTrack);
                list.remove(playlistTrack);
            }
        }

        // We need to refresh the view since it's not an Observable...
        UIController.getController().refreshPlaylist();
    }

    /**
     * Returns the playlist containing the favorited tracks.
     *
     * @return The playlist containing the favorited tracks.
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

    private List<SavedPlaylist> retrievePlaylists() {
        Session session = DatabaseManager.getInstance().getSession();

        // Retrieve all playlists but the favorites one.
        Query<SavedPlaylist> query = session.createQuery("from SavedPlaylist where id > '0'", SavedPlaylist.class);

        return query.list();
    }
}
