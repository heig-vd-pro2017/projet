package ch.tofind.commusica;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Track;
import org.hibernate.Session;

import java.io.File;
import java.util.List;

public class Commusica {

    public static void dropDatabase() {
        String filePath = "commusica.db";
        File dbFile = new File(filePath);

        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public static void main(String[] args) {

        dropDatabase();

        Track track1 = new Track("Track1", "Track", "Tracks", 43, "/tmp/test1");
        Track track2 = new Track("Track2", "Track", "Tracks", 98, "/tmp/test2");
        Track track3 = new Track("Track3", "Track", "Tracks", 234, "/tmp/test3");

        track1.save();
        track2.save();
        track3.save();

        track2.update();

        track3.delete();

        Session session = DatabaseManager.getInstance().getSession();

        List<Track> tracks = session.createQuery("from Track").list();

        for (Track track : tracks) {
            System.out.println(track);
        }

        DatabaseManager.getInstance().close();
    }
}
