package ch.tofind.commusica.utils;

import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistTrack;
import javafx.beans.property.ObjectPropertyBase;

/**
 * @brief This class can get properties from a PlaylistTrack.
 */
public class PlaylistTrackProperty extends ObjectPropertyBase<PlaylistTrack> {

    /**
     * Shortcut accessor for the getValue().getTrack() property.
     *
     * @return Track property of the current value.
     */
    public Track getTrack() {
        return getValue().getTrack();
    }

    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public String getName() {
        return "currentTrack";
    }
}