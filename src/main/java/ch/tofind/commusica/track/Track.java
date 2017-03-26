package ch.tofind.commusica.track;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "track")
@Access(AccessType.FIELD)
public class Track {

    //! ID of the track in the database
    @Id
    @GeneratedValue
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
    }

    @PrePersist
    protected void onCreate() {
        dateAdded = new Date();
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", artist, title, uri);
    }
}
