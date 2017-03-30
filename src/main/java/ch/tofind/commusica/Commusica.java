package ch.tofind.commusica;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.media.*;
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

        PlaylistTrackPK playlistTrackPK1 = new PlaylistTrackPK(playlist1.getId(), track1.getId());
        PlaylistTrackPK playlistTrackPK2 = new PlaylistTrackPK(playlist1.getId(), track2.getId());
        PlaylistTrack playlistTrack1 = new PlaylistTrack(playlistTrackPK1);
        PlaylistTrack playlistTrack2 = new PlaylistTrack(playlistTrackPK2);

        playlistTrack1.upvote();
        playlistTrack1.upvote();
        playlistTrack2.downvote();

        DatabaseManager.getInstance().save(playlistTrack1);
        DatabaseManager.getInstance().save(playlistTrack2);

        commusica.listPlaylist();

        DatabaseManager.getInstance().close();

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
            List playlists = session.createQuery("FROM PlaylistTrack").getResultList();

            Iterator playlistIterator = playlists.iterator();

            while (playlistIterator.hasNext()) {

                PlaylistTrack playlistTrack = (PlaylistTrack) playlistIterator.next();

                System.out.println("coucou");
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
