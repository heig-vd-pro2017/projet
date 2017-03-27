package ch.tofind.commusica.media;

import java.util.Date;
import java.util.Set;

public class Playlist {
    private Integer id;
    private String name;
    private Set tracks;
    private Date dateAdded;
    private Date datePlayed;

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
