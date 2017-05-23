package ch.tofind.commusica.media;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Configuration;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @brief This class represents a playlist.
 */
public class SavedPlaylist implements Serializable, IPlaylist {

    //! ID of the playlist.
    private Integer id;

    //! Name of the playlist.
    private String name;

    //! When was the playlist added for the first time in the database.
    private Date dateAdded;

    //! When was the track played for the last time.
    private Date datePlayed;

    //! Version control for concurrency.
    private Integer version;

    /**
     * @brief Empty constructor for Hibernate.
     */
    private SavedPlaylist() {

    }

    /**
     * @brief Create a playlist.
     * @param name Name of the playlist.
     */
    public SavedPlaylist(String name) {
        this.name = name;
        this.dateAdded = new Date();
    }

    /**
     * @brief Get the playlist's ID.
     * @return The playlist's ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @brief Get the playlist's name.
     * @return The playlist's name.
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Set the playlist's name.
     * @param name The playlist's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Get the date when the playlist was added.
     * @return The added date.
     */
    public Date getDateAdded() {
        return dateAdded;
    }

    /**
     * @brief Get the date when the playlist was played.
     * @return The played date.
     */
    public Date getDatePlayed() {
        return datePlayed;
    }

    /**
     * @brief Update the object.
     */
    public void update() {
        this.datePlayed = new Date();
    }

    @Override
    public boolean equals(Object object) {

        if (object == this) {
            return true;
        }

        if (!(object instanceof SavedPlaylist)) {
            return false;
        }

        SavedPlaylist playlist = (SavedPlaylist) object;

        return Objects.equals(name, playlist.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {

        String format = Configuration.getInstance().get("DATE_FORMAT");

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        String dateAddedString = dateAdded == null ? "N/A" : dateFormat.format(dateAdded);
        String datePlayedString = datePlayed == null ? "N/A" : dateFormat.format(datePlayed);

        return "Playlist"                         + '\n' + '\t' +
               "Name.......: " + name             + '\n' + '\t' +
               "Date added.: " + dateAddedString  + '\n' + '\t' +
               "Date played: " + datePlayedString + '\n';
    }

    public boolean addTrack(Track track) {
        if (!contains(track)) {
            PlaylistTrack playlistTrack = new PlaylistTrack(this, track);
            DatabaseManager.getInstance().save(playlistTrack);

            return true;
        }

        return false;
    }

    public boolean contains(Track track) {
        // Get tracks list.
        List<PlaylistTrack> tracksList = getTracksList();

        return tracksList.stream().anyMatch(p -> p.getTrack().equals(track));
    }

    public PlaylistTrack getPlaylistTrack(Track track) {
        List<PlaylistTrack> trackList = getTracksList();

        return trackList.stream().filter(p -> p.getTrack().equals(track)).findFirst().orElse(null);
    }

    public List<PlaylistTrack> getTracksList() {
        Session session = DatabaseManager.getInstance().getSession();

        String queryString = String.format("from PlaylistTrack where playlist_id = '%d'", id);
        Query<PlaylistTrack> query = session.createQuery(queryString, PlaylistTrack.class);

        return query.list();
    }

    public boolean isSaved() {
        return true;
    }
}
