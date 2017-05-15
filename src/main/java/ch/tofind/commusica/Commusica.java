package ch.tofind.commusica;

import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import ch.tofind.commusica.utils.Logger;

import java.io.File;

public class Commusica {

    private static final Logger LOG = new Logger(Commusica.class.getSimpleName());

    public static void dropDatabase() {
        String filePath = "commusica.db";
        File dbFile = new File(filePath);

        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public static void main(String... args) throws Exception {
        LOG.log(Logger.Level.INFO, "Starting application...");

        dropDatabase();

        /*Playlist playlist1 = new Playlist("Test1");
        Playlist playlist2 = new Playlist("Test2");*/

        Track track1 = new Track("BLOOD", "Kendrick Lamar", "DAMN.", 119, "/Users/faku99/Desktop/tmp/BLOOD.mp3");
        Track track2 = new Track("DNA", "Kendrick Lamar", "DAMN.", 186, "/Users/faku99/Desktop/tmp/DNA.mp3");
        Track track3 = new Track("HUMBLE", "Kendrick Lamar", "DAMN.", 177, "/Users/faku99/Desktop/tmp/HUMBLE.mp3");

        // TODO: Saving the track into the DB should be done when it is sent/received. Am I right?
        DatabaseManager.getInstance().save(track1);
        DatabaseManager.getInstance().save(track2);
        DatabaseManager.getInstance().save(track3);

        PlaylistManager playlistManager = PlaylistManager.getInstance();

        Playlist playlist1 = playlistManager.createPlaylist("Test1");
        Playlist playlist2 = playlistManager.createPlaylist("Test2");

        playlistManager.addTrackToPlaylist(track1, playlist1);
        playlistManager.addTrackToPlaylist(track2, playlist1);
        playlistManager.addTrackToPlaylist(track3, playlist1);

        playlistManager.addTrackToPlaylist(track2, playlist2);
        playlistManager.addTrackToPlaylist(track3, playlist2);

        playlistManager.getPlaylistTrackInPlaylist(track3, playlist1).upvote();

        UIController.launch(UIController.class);

        DatabaseManager.getInstance().close();
    }
}
