package ch.tofind.commusica;

import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.ui.UIController;

import java.io.File;
import java.io.IOException;

public class Commusica {

    public static void dropDatabase() {
        String filePath = "commusica.db";
        File dbFile = new File(filePath);

        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public static void main(String[] args) {
        
        System.out.println("App démarrée :)");

        try {
            Configuration.getInstance().load("commusica.cfg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        dropDatabase();

        Playlist playlist = new Playlist("Test");

        DatabaseManager.getInstance().save(playlist);

        Track track1 = new Track("Test1", "Test", "Test", 123, "/tmp/sample1.wav");
        Track track2 = new Track("Test2", "Test", "Test", 132, "/tmp/sample2.wav");
        Track track3 = new Track("Test3", "Test", "Test", 321, "/tmp/sample3.wav");

        track2.update();

        DatabaseManager.getInstance().save(track1);
        DatabaseManager.getInstance().save(track2);
        DatabaseManager.getInstance().save(track3);

        System.out.println(playlist);

        System.out.println(track1);
        System.out.println(track2);
        System.out.println(track3);

        UIController.launch(UIController.class);
    }
}
