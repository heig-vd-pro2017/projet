package ch.tofind.commusica.utils;

import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.playlist.VoteComparator;
import javafx.collections.ObservableListBase;

import java.util.ArrayList;
import java.util.List;

public class ObservableSortedPlaylistTrackList extends ObservableListBase<PlaylistTrack> {

    private final Logger LOG = new Logger(ObservableSortedPlaylistTrackList.class.getSimpleName());

    private final List<PlaylistTrack> delegate = new ArrayList<>();

    private VoteComparator comparator = new VoteComparator();

    private int count = 0;

    @Override
    public boolean add(PlaylistTrack track) {
        if(delegate.contains(track)) {
            return false;
        }

        beginChange();

        boolean result = delegate.add(track);

        track.getVotesProperty().addListener(((observable, oldValue, newValue) -> {
            beginChange();
            sort();
            endChange();
        }));

        sort();

        endChange();

        return result;
    }

    public void clear() {
        beginChange();

        delegate.clear();

        endChange();
    }

    @Override
    public PlaylistTrack get(int index) {
        return delegate.get(index);
    }

    public PlaylistTrack getNextTrack() {
        // View the previous has 'already played' when going to next track, not before.
        if(count >= size()) {
            return null;
        }

        return get(count++);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    private void sort() {
        if(count < size()) {
            // WARNING: Second parameter is exclusive!
            delegate.subList(count, size()).sort(comparator);
        }
    }
}
