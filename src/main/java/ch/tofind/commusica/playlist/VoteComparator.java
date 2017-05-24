package ch.tofind.commusica.playlist;

import java.util.Comparator;

/**
 * @brief Class that tells how a PlaylistTrack is considered "greater" than an other.
 */
public class VoteComparator implements Comparator<PlaylistTrack> {

    @Override
    public int compare(PlaylistTrack x, PlaylistTrack y) {
        return y.getVotesProperty().intValue() - x.getVotesProperty().intValue();
    }
}
