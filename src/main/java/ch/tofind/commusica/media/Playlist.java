package ch.tofind.commusica.media;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Playlist {

    //! ID of the playlist
    private Integer id;

    //! Name of the playlist
    private String name;

    //! Tracks to which the playlist is associated
    private Set<Track> tracks = new HashSet<>(0);

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
