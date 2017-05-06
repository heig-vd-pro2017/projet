package ch.tofind.commusica.playlist;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import ch.tofind.commusica.utils.ObservableSortedPlaylistTrackList;
import javafx.collections.ObservableList;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class PlaylistManager {

    //! Shared instance of the playlist for all the application
    private static PlaylistManager instance = null;

    //! All available playlists
    private List<Playlist> playlists;

    //! Playlist currently running
    private Playlist playlist;

    private final ObservableSortedPlaylistTrackList playlistTracksList;

    /**
     * PlaylistManager single constructor. Avoid the instantiation.
     */
    private PlaylistManager() {

        Session session = DatabaseManager.getInstance().getSession();
        playlists = session.createQuery("from Playlist").list();
        playlistTracksList = new ObservableSortedPlaylistTrackList();
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

    public Playlist getPlaylist() {
        return playlist;
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
    }

    public void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
    }

    public void loadPlaylist(Playlist playlist) {

        this.playlist = playlist;

        // Update timestamp.
        playlist.update();

        Session session = DatabaseManager.getInstance().getSession();

        playlistTracksList.clear();
        playlistTracksList.addAll(session.createQuery(String.format("from PlaylistTrack where playlist_id = '%d'", playlist.getId())).list());
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public PlaylistTrack addTrack(Track track) {

        PlaylistTrack playlistTrack = new PlaylistTrack(playlist, track);

        addPlaylistTrack(playlistTrack);

        return playlistTrack;
    }

    public void addPlaylistTrack(PlaylistTrack playlistTrack) {
        playlistTracksList.add(playlistTrack);
    }

    public PlaylistTrack getPlaylistTrack(Track track) {

        for (PlaylistTrack playlistTrack : playlistTracksList) {
            if (playlistTrack.getTrack() == track) {
                return playlistTrack;
            }
        }

        return null;

    }

    public Track nextTrack() {
        PlaylistTrack nextTrack = playlistTracksList.getNextTrack();
        return nextTrack != null ? nextTrack.getTrack() : null;
    }

    public ObservableSortedPlaylistTrackList getObservableTracksList() {
        return playlistTracksList;
    }
}
