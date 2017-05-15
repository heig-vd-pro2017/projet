package ch.tofind.commusica.media;

import ch.tofind.commusica.playlist.PlaylistTrack;

import java.util.List;

public interface IPlaylist {
    boolean addTrack(Track track);
    boolean contains(Track track);
    PlaylistTrack getPlaylistTrack(Track track);
    List<PlaylistTrack> getTracksList();
    boolean isSaved();
}
