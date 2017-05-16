package ch.tofind.commusica.utils;

import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistTrack;
import javafx.beans.property.ObjectPropertyBase;

public class PlaylistTrackProperty extends ObjectPropertyBase<PlaylistTrack> {
    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public String getName() {
        return "currentTrack";
    }

    /**
     * Shortcut accessor for the getValue().getTrack() property.
     *
     * @return Track property of the current value.
     */
    public Track getTrack() {
        return getValue().getTrack();
    }
}
