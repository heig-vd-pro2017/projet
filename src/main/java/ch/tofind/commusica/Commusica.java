package ch.tofind.commusica;

import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

import ch.tofind.commusica.utils.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
        LOG.info("Starting application...");

        dropDatabase();

        Playlist playlist1 = new Playlist("Test1");
        Playlist playlist2 = new Playlist("Test2");

        Track track1 = new Track("Test1", "Test", "Test", 123, "/Users/faku99/Desktop/tmp/BLOOD.mp3");
        Track track2 = new Track("Test2", "Test", "Test", 132, "/Users/faku99/Desktop/tmp/DNA.mp3");
        Track track3 = new Track("Test3", "Test", "Test", 321, "/Users/faku99/Desktop/tmp/HUMBLE.mp3");

        PlaylistTrack pt11 = new PlaylistTrack(playlist1, track1);
        PlaylistTrack pt12 = new PlaylistTrack(playlist1, track2);
        PlaylistTrack pt13 = new PlaylistTrack(playlist1, track3);

        PlaylistTrack pt21 = new PlaylistTrack(playlist2, track1);
        PlaylistTrack pt22 = new PlaylistTrack(playlist2, track3);

        DatabaseManager.getInstance().save(playlist1);
        DatabaseManager.getInstance().save(playlist2);

        DatabaseManager.getInstance().save(track1);
        DatabaseManager.getInstance().save(track2);
        DatabaseManager.getInstance().save(track3);

        DatabaseManager.getInstance().save(pt11);
        DatabaseManager.getInstance().save(pt12);
        DatabaseManager.getInstance().save(pt13);

        DatabaseManager.getInstance().save(pt21);
        DatabaseManager.getInstance().save(pt22);

        PlaylistManager playlistManager = PlaylistManager.getInstance();

        playlistManager.loadPlaylist(playlist1);

        pt11.downvote();
        pt11.downvote();
        pt12.upvote();

        playlistManager.addPlaylistTrack(pt11);
        playlistManager.addPlaylistTrack(pt12);

        UIController.launch(UIController.class);

        DatabaseManager.getInstance().close();
    }
}
