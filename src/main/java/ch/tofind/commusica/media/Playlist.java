package ch.tofind.commusica.media;

import java.util.Date;
import java.util.Set;

public class Playlist {

    //! ID of the playlist
    private Integer id;

    //! Name of the playlist
    private String name;

    //! Tracks of the playlist
    private Set tracks;

    //! When was the playlist added for the first time in the database
    private Date dateAdded;

    //! When was the track played for the last time
    private Date datePlayed;

    //! Version control for concurrency
    private Integer version;

    public Playlist(String name, Set tracks) {
        this.name = name;
        this.tracks = tracks;
        this.dateAdded = new Date();
    }

    public String getName() {
        return name;
    }

    public Set getTracks() {
        return tracks;
    }
}
