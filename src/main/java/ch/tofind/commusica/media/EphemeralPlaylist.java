package ch.tofind.commusica.media;

import ch.tofind.commusica.database.DatabaseManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.ObservableSortedPlaylistTrackList;

public class EphemeralPlaylist implements IPlaylist {

    private ObservableSortedPlaylistTrackList tracksList;

    //! The playlist that will be saved into the database for keeping track of this one.
    private SavedPlaylist staticPlaylist;

    public EphemeralPlaylist() {
        staticPlaylist = new SavedPlaylist("Soirée lambda");
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

    public boolean downvoteTrack(Track track) {
        PlaylistTrack playlistTrack = getPlaylistTrack(track);

        if (playlistTrack != null) {
            playlistTrack.downvote();

            return true;
        }

        return false;
    }

    public boolean upvoteTrack(Track track) {
        PlaylistTrack playlistTrack = getPlaylistTrack(track);

        if (playlistTrack != null) {
            playlistTrack.upvote();

            return true;
        }

        return false;
    }

    public PlaylistTrack getPlaylistTrack(Track track) {
        return tracksList.stream().filter(p -> p.getTrack().equals(track)).findFirst().orElse(null);
    }

    public boolean contains(Track track) {
        return tracksList.stream().anyMatch(p -> p.getTrack().equals(track));
    }

    public ObservableSortedPlaylistTrackList getTracksList() {
        return tracksList;
    }

    public boolean isSaved() {
        return false;
    }

    @Override
    public String toString() {
        return staticPlaylist.getName() + "\n" +
                tracksList;
    }
}
