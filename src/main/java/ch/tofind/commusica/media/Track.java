package ch.tofind.commusica.media;

import ch.tofind.commusica.database.DatabaseManager;

import java.text.SimpleDateFormat;
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
    private String dateAdded;

    //! When was the track played for the last time
    private String datePlayed;

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
        this.dateAdded = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDatePlayed() {
        return datePlayed;
    }

    public void setDatePlayed(String datePlayed) {
        this.datePlayed = datePlayed;
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
