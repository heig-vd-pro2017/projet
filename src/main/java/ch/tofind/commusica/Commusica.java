package ch.tofind.commusica;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.media.Playlist;
import ch.tofind.commusica.media.Track;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Commusica {

    public static void dropDatabase() {
        String filePath = "commusica.db";
        File dbFile = new File(filePath);

        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public static void main(String[] args) {

        /*
        dropDatabase();

        Track track1 = new Track("Track1", "Track", "Tracks", 98, "/tmp/test1");
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
        */

        dropDatabase();

        Commusica commusica = new Commusica();

        HashSet tracks1 = new HashSet();
        HashSet tracks2 = new HashSet();

        Track track1 = new Track("Track1", "Track", "Tracks", 43, "/tmp/test1");
        Track track2 = new Track("Track2", "Track", "Tracks", 43, "/tmp/test2");
        Track track3 = new Track("Track3", "Track", "Tracks", 43, "/tmp/test3");

        tracks1.add(track1);
        tracks1.add(track2);
        tracks1.add(track3);

        tracks2.add(track1);
        tracks2.add(track3);

        commusica.addPlaylist("Test1", tracks1);
        commusica.addPlaylist("Test2", tracks2);

        commusica.listPlaylist();

        DatabaseManager.getInstance().close();

    }

    /* Method to add an employee record in the database */
    public void addPlaylist(String name, Set tracks) {
        Session session = DatabaseManager.getInstance().getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Playlist playlist1 = new Playlist(name, tracks);
            session.save(playlist1);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    /* Method to list all the employees detail */
    public void listPlaylist(){
        Session session = DatabaseManager.getInstance().getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            List playlists = session.createQuery("FROM Playlist").list();

            Iterator playlistIterator = playlists.iterator();

            while (playlistIterator.hasNext()) {

                Playlist playlist = (Playlist)playlistIterator.next();
                System.out.println("Playlist: " + playlist.getName());

                Set tracks = playlist.getTracks();

                Iterator trackIterator = tracks.iterator();
                while (trackIterator.hasNext()) {
                    Track track = (Track)trackIterator.next();
                    System.out.println("Track: " + track);
                }
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
