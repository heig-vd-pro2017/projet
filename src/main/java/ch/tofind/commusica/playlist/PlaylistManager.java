package ch.tofind.commusica.playlist;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class
PlaylistManager {

    //! Shared instance of the playlist for all the application
    private static PlaylistManager instance = null;

    //! All available playlists
    private List<Playlist> playlists;

    //! Playlist currently running
    private Playlist playlist;

    //! PlaylistTracks composed by the current playlist
    private List<PlaylistTrack> playlistTracksList;

    //! PlaylistTracks composed by the current playlist
    private PriorityQueue<PlaylistTrack> playlistTracksPriorityQueue;

    /**
     * PlaylistManager single constructor. Avoid the instantiation.
     */
    private PlaylistManager() {

        Session session = DatabaseManager.getInstance().getSession();
        playlists = session.createQuery("from Playlist").list();
        playlistTracksList = new ArrayList<>(0);
        playlistTracksPriorityQueue = new PriorityQueue<>(1, new VoteComparator());

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

        Session session = DatabaseManager.getInstance().getSession();

        playlistTracksList = session.createQuery(String.format("from PlaylistTrack where playlist_id = '%d'", playlist.getId())).list();

        playlistTracksPriorityQueue.addAll(playlistTracksList);

    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public List<PlaylistTrack> getPlaylistTracks() {
        return playlistTracksList;
    }

    public PlaylistTrack addTrack(Track track) {

        PlaylistTrack playlistTrack = new PlaylistTrack(playlist, track);

        addPlaylistTrack(playlistTrack);

        return playlistTrack;
    }

    public void addPlaylistTrack(PlaylistTrack playlistTrack) {

        if (playlistTracksList.contains(playlistTrack)) {
            playlistTracksPriorityQueue.remove(playlistTrack);
        } else {
            playlistTracksList.add(playlistTrack);
        }

        playlistTracksPriorityQueue.add(playlistTrack);
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
        return playlistTracksPriorityQueue.poll().getTrack();
    }
}
