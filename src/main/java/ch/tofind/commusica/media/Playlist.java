package ch.tofind.commusica.media;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Playlist implements Serializable {

    //! ID of the playlist
    private Integer id;

    //! Name of the playlist
    private String name;

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


    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }

        if (!(object instanceof Playlist)) {
            return false;
        }

        Playlist playlist = (Playlist) object;

        return Objects.equals(name, playlist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
