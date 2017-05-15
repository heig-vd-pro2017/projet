package ch.tofind.commusica.utils;

import ch.tofind.commusica.media.Track;

import javafx.beans.property.ObjectPropertyBase;

public class TrackProperty extends ObjectPropertyBase<Track> {
    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public String getName() {
        return "currentTrack";
    }
}
