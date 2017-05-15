package ch.tofind.commusica.media;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.ObservableSortedPlaylistTrackList;

public class EphemeralPlaylist {

    private ObservableSortedPlaylistTrackList tracksList;

    //! The playlist that will be saved into the database for keeping track of this one.
    private Playlist staticPlaylist;

    public EphemeralPlaylist() {
        staticPlaylist = new Playlist("Soirée lambda");
        DatabaseManager.getInstance().save(staticPlaylist);

        tracksList = new ObservableSortedPlaylistTrackList();
    }

    public boolean addTrack(Track track) {
        // Check that the playlist doesn't already contains the track.
        if (tracksList.stream().noneMatch(p -> p.getTrack().equals(track))) {
            // Create the PlaylistTrack object by associating it with the static playlist.
            PlaylistTrack playlistTrack = new PlaylistTrack(staticPlaylist, track);
            tracksList.add(playlistTrack);

            return true;
        }

        return false;
    }

    public ObservableSortedPlaylistTrackList tracksList() {
        return tracksList;
    }

    public boolean downvoteTrack(Track track) {
        PlaylistTrack playlistTrack = getPlaylistTrackForTrack(track);

        if (playlistTrack != null) {
            playlistTrack.downvote();

            return true;
        }

        return false;
    }

    public boolean upvoteTrack(Track track) {
        PlaylistTrack playlistTrack = getPlaylistTrackForTrack(track);

        if (playlistTrack != null) {
            playlistTrack.upvote();

            return true;
        }

        return false;
    }

    private PlaylistTrack getPlaylistTrackForTrack(Track track) {
        return tracksList.stream().filter(p -> p.getTrack().equals(track)).findFirst().get();
    }

}
