package ch.tofind.commusica.media;

import javax.persistence.OneToMany;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Playlist {

    //! ID of the playlist
    private Integer id;

    //! Name of the playlist
    private String name;

    @OneToMany(mappedBy = "id.trackId")
    private List tracks;

    //! When was the playlist added for the first time in the database
    private Date dateAdded;

    //! When was the track played for the last time
    private Date datePlayed;

    //! Version control for concurrency
    private Integer version;

    public Playlist(String name) {
        this.name = name;
        this.dateAdded = new Date();
    }

    public Integer getId() {
        return id;
    }
}
