package ch.tofind.commusica.playlist;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.EphemeralPlaylist;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.ObservableSortedPlaylistTrackList;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class PlaylistManager {

    private static final Logger LOG = new Logger(PlaylistManager.class.getSimpleName());

    //! Shared instance of the playlist for all the application
    private static PlaylistManager instance = null;

    //! All available playlists
    private List<Playlist> playlists;

    //! Playlist currently running
    private EphemeralPlaylist playlist;

    //! Playlist containing favorited tracks.
    private Playlist favoritesPlaylist;

    /**
     * PlaylistManager single constructor. Avoid the instantiation.
     */
    private PlaylistManager() {
        playlist = new EphemeralPlaylist();
        playlists = retrievePlaylists();
        favoritesPlaylist = retrieveFavoritesPlaylist();
    }

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

    public Playlist createPlaylist(String name) {
        Playlist playlist = new Playlist(name);
        DatabaseManager.getInstance().save(playlist);

        playlists.add(playlist);

        return playlist;
    }

    public void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
    }

    /*public void loadPlaylist(Playlist playlist) {

        this.playlist = playlist;

        // Update timestamp.
        playlist.update();

        Session session = DatabaseManager.getInstance().getSession();

        playlistTracksList.clear();
        playlistTracksList.addAll(session.createQuery(String.format("from PlaylistTrack where playlist_id = '%d'", playlist.getId())).list());
    }*/

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    /*public PlaylistTrack addTrack(Track track) {

        PlaylistTrack playlistTrack = new PlaylistTrack(playlist, track);

        addPlaylistTrack(playlistTrack);

        return playlistTrack;
    }*/

    public void addTrackToPlaylist(Track track, Playlist playlist) {
        PlaylistTrack playlistTrack = new PlaylistTrack(playlist, track);

        // TODO: Check if the track already is in the playlist.
        DatabaseManager.getInstance().save(playlistTrack);
    }

    /*public void addPlaylistTrack(PlaylistTrack playlistTrack) {
        playlistTracksList.add(playlistTrack);
    }*/

    /* public PlaylistTrack getPlaylistTrack(Track track) {

        for (PlaylistTrack playlistTrack : playlistTracksList) {
            if (playlistTrack.getTrack() == track) {
                return playlistTrack;
            }
        }

        return null;

    }*/

    public PlaylistTrack getPlaylistTrackInPlaylist(Track track, Playlist playlist) {
        List<PlaylistTrack> tracksList = getTracksForPlaylist(playlist);

        // Get the first result.
        return tracksList.stream().filter(p -> p.getTrack().equals(track)).findFirst().get();
    }

    public List<PlaylistTrack> getTracksForPlaylist(Playlist playlist) {
        Session session = DatabaseManager.getInstance().getSession();

        String queryString = String.format("from PlaylistTrack where playlist_id = '%d'", playlist.getId());
        Query<PlaylistTrack> query = session.createQuery(queryString, PlaylistTrack.class);

        /*ObservableSortedPlaylistTrackList list = new ObservableSortedPlaylistTrackList();
        list.addAll(query.list());

        return list;*/

        return query.list();
    }

    public Playlist getFavoritesPlaylist() {
        return favoritesPlaylist;
    }

    public void addTrackToFavorites(Track track) {
        PlaylistTrack playlistTrack = new PlaylistTrack(favoritesPlaylist, track);

        DatabaseManager.getInstance().save(playlistTrack);
    }

    public void removeTrackFromFavorites(Track track) {
        List<PlaylistTrack> list = getTracksForPlaylist(favoritesPlaylist);

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
    private Playlist retrieveFavoritesPlaylist() {
        Session session = DatabaseManager.getInstance().getSession();

        // Our implementation is made so that the playlist containing favorited songs has the ID '0'.
        Query<Playlist> fetchQuery = session.createQuery("from Playlist where id = '0'", Playlist.class);

        // Retrieve the playlist.
        Playlist playlist = null;
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
                NativeQuery<Playlist> createQuery = session.createNativeQuery(queryString, Playlist.class);
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

    private List<Playlist> retrievePlaylists() {
        Session session = DatabaseManager.getInstance().getSession();

        // Retrieve all playlists but the favorites one.
        Query<Playlist> query = session.createQuery("from Playlist where id > '0'", Playlist.class);

        return query.list();
    }
}
