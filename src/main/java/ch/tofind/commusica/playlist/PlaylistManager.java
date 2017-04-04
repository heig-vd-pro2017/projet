package ch.tofind.commusica.playlist;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Set;

public class PlaylistManager {

    //! Shared instance of the playlist for all the application
    private static PlaylistManager instance = null;

    //! Tracks composed by the current playlist
    private Set<Playlist> playlists;

    //! Playlist currently running
    private Playlist playlist;

    //! Tracks composed by the current playlist
    private PriorityQueue<PlaylistTrack> tracks;

    public class VoteComparator implements Comparator<PlaylistTrack> {

        @Override
        public int compare(PlaylistTrack x, PlaylistTrack y) {
            return x.getVotes() - y.getVotes();
        }
    }

    /**
     * PlaylistManager single constructor. Avoid the instantiation.
     */
    private PlaylistManager() {
        VoteComparator comparator = new VoteComparator();
        tracks = new PriorityQueue<>(1, comparator);
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

    public void addTrack(Track track) {
        PlaylistTrack playlistTrack = new PlaylistTrack(playlist, track);
        if (tracks.contains(playlistTrack)) {
            System.out.println("Déjà présent copain :)");
        }

        PlaylistTrack id = (PlaylistTrack)DatabaseManager.getInstance().save(playlistTrack);
        tracks.add(playlistTrack);

        for (PlaylistTrack p : tracks) {
            System.out.println(p.getTrack());
        }
    }

    public void loadPlaylist(Playlist playlist) {

    }


}
