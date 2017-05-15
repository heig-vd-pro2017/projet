package ch.tofind.commusica.media;

import ch.tofind.commusica.utils.Configuration;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * @brief This class represents an audio track and can be stored in a database
 */
public class Track implements Serializable {

    //! ID of the track in the database.
    private Integer id;

    //! Title of the track.
    private String title;

    //! Artist of the track.
    private String artist;

    //! Album of the track.
    private String album;

    //! Length (in seconds) of the track.
    private Integer length;

    //! URI of the file.
    private String uri;

    //! When was the track added for the first time in the database.
    private Date dateAdded;

    //! When was the track played for the last time.
    private Date datePlayed;

    /**
     * If the track is favorited or not.
     * This field is useful for Hibernate integration, otherwise the property defined below should be used.
     */
    private boolean favorited;

    //! If the track is favorited or not.
    private transient BooleanProperty favoritedProperty;

    //! Version control for concurrency.
    private Integer version;

    /**
     * @brief Empty constructor for Hibernate.
     */
    protected Track() {

    }

    /**
     * @brief Create a track.
     *
     * @param title Title of the track.
     * @param artist Artist of the track.
     * @param album Album of the track.
     * @param length Length (in seconds) of the track.
     * @param uri URI of the file.
     */
    public Track(Integer id, String title, String artist, String album, Integer length, String uri, boolean favorited) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.uri = uri;
        this.dateAdded = new Date();
        this.favorited = favorited;
        this.favoritedProperty = new SimpleBooleanProperty(favorited);
        this.favoritedProperty.addListener(((observable, oldValue, newValue) -> this.favorited = newValue));
    }

    /**
     * @brief Create a Track from an AudioFile. It is useful when you want to transfer a file and
     * want to do some check on a Track instead of checking the AudioFile itself
     *
     * @param audioFile an AudioFile object that represents your track
     */
    public Track(AudioFile audioFile) {

        AudioHeader header = audioFile.getAudioHeader();
        Tag tags = audioFile.getTag();

        // We consider that all tags are unknown/default values
        this.title = AudioFile.getBaseFilename(audioFile.getFile());
        this.artist = "Unknown";
        this.album = "Unknown";
        this.length = header.getTrackLength();

        if (!Objects.equals(tags.getFirst(FieldKey.TITLE), "")) {
            this.title = tags.getFirst(FieldKey.TITLE);
        }

        if (!Objects.equals(tags.getFirst(FieldKey.ARTIST), "")) {
            this.artist = tags.getFirst(FieldKey.ARTIST);
        }

        if (!Objects.equals(tags.getFirst(FieldKey.ALBUM), "")) {
            this.album = tags.getFirst(FieldKey.ALBUM);
        }

        this.uri = audioFile.getFile().toString();

        this.length = header.getTrackLength();
        
        this.favorited = false;

        this.favoritedProperty = new SimpleBooleanProperty(favorited);

        this.favoritedProperty.addListener(((observable, oldValue, newValue) -> this.favorited = newValue));
    }

    /**
     * @brief Get the track's ID.
     * @return The track's ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @brief Get the track's title.
     * @return The track's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @brief Get the track's artist.
     * @return The track's artist.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @brief Get the track's album.
     * @return The track's album.
     */
    public String getAlbum() {
        return album;
    }

    /**
     * @brief Get the track's length.
     * @return The track's length.
     */
    public Integer getLength() {
        return length;
    }

    /**
     * @brief Get the track's URI.
     * @return The track's URI.
     */
    public String getUri() {
        return uri;
    }

    /**
     * @brief Set the uri of the track.
     * @param uri The URI of the track.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @brief Get the date when the track was added.
     * @return The added date.
     */
    public Date getDateAdded() {
        return dateAdded;
    }

    /**
     * @brief Get the date when the track was played.
     * @return The played date.
     */
    public Date getDatePlayed() {
        return datePlayed;
    }

    /**
     * Returns the property about if the track is a favorite or not.
     * @return The property about if the track is a favorite or not.
     */
    public BooleanProperty getFavoritedProperty() {
        return favoritedProperty;
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

        if (!(object instanceof Track)) {
            return false;
        }

        Track track = (Track) object;

        return Objects.equals(title, track.title)   &&
               Objects.equals(artist, track.artist) &&
               Objects.equals(album, track.album)   &&
               getFavoritedProperty().getValue() == track.getFavoritedProperty().getValue() &&
               length == track.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist, album);
    }

    @Override
    public String toString() {

        String format = Configuration.getInstance().get("DEFAULT_DATE_FORMAT");

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        String dateAddedString = dateAdded == null ? "N/A" : dateFormat.format(dateAdded);
        String datePlayedString = datePlayed == null ? "N/A" : dateFormat.format(datePlayed);

        return "Track"                                        + '\n' + '\t' +
               "Title......: " + title                        + '\n' + '\t' +
               "Artist.....: " + artist                       + '\n' + '\t' +
               "Album......: " + album                        + '\n' + '\t' +
               "Length.....: " + length                       + '\n' + '\t' +
               "URI........: " + uri                          + '\n' + '\t' +
               "Date added.: " + dateAddedString              + '\n' + '\t' +
               "Date played: " + datePlayedString             + '\n' + '\t' +
               "Favorited..: " + favoritedProperty.getValue() + '\n';
    }
}
