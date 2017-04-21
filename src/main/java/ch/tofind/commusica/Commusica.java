package ch.tofind.commusica;

import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.ui.UIController;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;

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

    public static void dropDatabase() {
        String filePath = "commusica.db";
        File dbFile = new File(filePath);

        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public static void main(String... args) throws Exception {

        System.out.println("App démarrée :)");

        try {
            Configuration.getInstance().load("commusica.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        dropDatabase();

        Playlist playlist1 = new Playlist("Test1");
        Playlist playlist2 = new Playlist("Test2");

        Track track1 = new Track("Test1", "Test", "Test", 123, "/home/ludelafo/Music/Temp/sample1.wav");
        Track track2 = new Track("Test2", "Test", "Test", 132, "/home/ludelafo/Music/Temp/sample2.wav");
        Track track3 = new Track("Test3", "Test", "Test", 321, "/home/ludelafo/Music/Temp/sample3.wav");

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

        Player player = new Player(Double.parseDouble(Configuration.getInstance().get("DEFAULT_VOLUME_STEP")));

        PlaylistManager playlistManager = PlaylistManager.getInstance();

        playlistManager.loadPlaylist(playlist1);

        pt11.downvote();
        pt11.downvote();
        pt12.upvote();

        playlistManager.addPlaylistTrack(pt11);
        playlistManager.addPlaylistTrack(pt12);

        /*BorderPane root = new BorderPane() {{
            VBox vbox = new VBox() {{

                HBox hbox = new HBox() {{

                    Button playButton = new Button("Play");
                    Button pauseButton = new Button("Pause");
                    Button stopButton = new Button("Stop");
                    Button nextButton = new Button("Next");
                    Button volUpButton = new Button("Volume up");
                    Button volDownButton = new Button("Volume down");

                    playButton.setOnAction(e -> {
                        player.play();

                    });

                    pauseButton.setOnAction(e -> {
                        player.pause();

                    });

                    nextButton.setOnAction(e -> {
                        player.stop();
                        player.load();
                        player.play();
                    });

                    stopButton.setOnAction(e -> {
                        player.stop();
                    });

                    volUpButton.setOnAction(e -> {
                        player.riseVolume();
                    });

                    volDownButton.setOnAction(e -> {
                        player.lowerVolume();
                    });

                    getChildren().addAll(playButton, pauseButton, stopButton, nextButton, volUpButton, volDownButton);
                }};

                getChildren().add(hbox);

            }};

            setCenter(vbox);

        }};

        Scene scene = new Scene(root, 400, 100);

        primaryStage.setScene(scene);
        primaryStage.show();*/

        UIController.launch(UIController.class);

        DatabaseManager.getInstance().close();
    }
}
