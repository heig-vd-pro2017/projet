package ch.tofind.commusica;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.*;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
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


        dropDatabase();

        /*
        Commusica commusica = new Commusica();

        Playlist playlist1 = new Playlist("Test1");
        Playlist playlist2 = new Playlist("Test2");

        DatabaseManager.getInstance().save(playlist1);
        DatabaseManager.getInstance().save(playlist2);

        Track track1 = new Track("Track1", "Track", "Tracks", 43, "/tmp/test1");
        Track track2 = new Track("Track2", "Track", "Tracks", 43, "/tmp/test2");
        Track track3 = new Track("Track3", "Track", "Tracks", 43, "/tmp/test3");

        DatabaseManager.getInstance().save(track1);
        DatabaseManager.getInstance().save(track2);
        DatabaseManager.getInstance().save(track3);

        PlaylistTrack playlistTrack1 = new PlaylistTrack(playlist1, track1);
        playlistTrack1.upvote();

        DatabaseManager.getInstance().save(playlistTrack1);
        DatabaseManager.getInstance().save(new PlaylistTrack(playlist1, track2));
        DatabaseManager.getInstance().save(new PlaylistTrack(playlist1, track3));
        DatabaseManager.getInstance().save(new PlaylistTrack(playlist2, track2));
        DatabaseManager.getInstance().save(new PlaylistTrack(playlist2, track3));
        DatabaseManager.getInstance().save(new PlaylistTrack(playlist2, track1));

        commusica.listPlaylist();

        DatabaseManager.getInstance().close();
        */

        Playlist playlist = new Playlist("Test");
        PlaylistManager.getInstance().loadPlaylist(playlist);

        Track track1 = new Track("Track1", "Track", "Tracks", 43, "/tmp/test1");
        Track track2 = new Track("Track2", "Track", "Tracks", 43, "/tmp/test2");
        Track track3 = new Track("Track3", "Track", "Tracks", 43, "/tmp/test3");

        PlaylistManager.getInstance().addTrack(track1);
        PlaylistManager.getInstance().addTrack(track2);
        PlaylistManager.getInstance().addTrack(track3);
        PlaylistManager.getInstance().addTrack(track3);



    }

    /* Method to add an employee record in the database */
    public void addPlaylist(String name, Set tracks) {
        Session session = DatabaseManager.getInstance().getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Playlist playlist1 = new Playlist(name);
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
            List playlists = session.createQuery("FROM Playlist").getResultList();

            Iterator playlistIterator = playlists.iterator();

            while (playlistIterator.hasNext()) {

                Playlist playlist = (Playlist)playlistIterator.next();
                System.out.println(playlist);

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
