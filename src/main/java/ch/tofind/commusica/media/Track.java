package ch.tofind.commusica.media;

import ch.tofind.commusica.database.DatabaseManager;

import java.util.Date;

public class Track {

    //! ID of the track in the database
    private Integer id;

    //! Title of the track
    private String title;

    //! Artist of the track
    private String artist;

    //! Album of the track
    private String album;

    //! Length (in seconds) of the track
    private Integer length;

    //! URI of the file
    private String uri;

    //! When was the track added for the first time in the database
    private Date dateAdded;

    //! When was the track played for the last time
    private Date datePlayed;

    /**
     * Create a track
     * @param title
     * @param artist
     * @param album
     * @param length
     * @param uri
     */
    public Track(String title, String artist, String album, Integer length, String uri) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.uri = uri;
        this.dateAdded = new Date();
    }

    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        Track obj2 = (Track)obj;

        return this.id == obj2.id ? true : false;
    }

    public int hashCode() {
        return (id + title + artist + album + length).hashCode();
    }

    public void save() {
        DatabaseManager.getInstance().save(this);
    }

    public void update() {
        DatabaseManager.getInstance().update(this);
    }

    public void delete() {
        DatabaseManager.getInstance().delete(this);
    }

    /**
     * Format the track to be displayed
     * @return The formatted String
     */
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", artist, title, uri);
    }
}
