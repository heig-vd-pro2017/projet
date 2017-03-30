package ch.tofind.commusica.media;


import javax.persistence.*;

@Entity
@Table(name="playlist_track")
public class PlaylistTrack {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "id.playlistId", column = @Column(name = "playlist_id")),
            @AttributeOverride(name = "id.trackId", column = @Column(name = "track_id")) })
    private PlaylistTrackPK id;


    //! Number of votes for the track
    @Column(name = "votes")
    private Integer votes;

    /**
     * Create a link between playlist and track
     * @param playlistId
     * @param trackId
     */
    public PlaylistTrack(PlaylistTrackPK id) {
        this.id = id;
        this.votes = 0;
    }

    public void upvote() {
        votes++;
    }

    public void downvote() {
        votes--;
    }
}
