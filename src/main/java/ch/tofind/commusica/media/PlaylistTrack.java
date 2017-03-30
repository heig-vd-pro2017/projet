/* https://blog.axopen.com/2013/11/les-cles-primaires-composees-avec-hibernate-4/ */
package ch.tofind.commusica.media;


import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

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

    public Integer getPlaylistId() {
        return id.getPlaylistId();
    }

    public Integer getTrackId() {
        return id.getTrackId();
    }
}
