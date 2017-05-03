package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * @brief This class represents
 */
public class PlaylistTrackCell {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(PlaylistTrackCell.class.getSimpleName());

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/PlaylistTrackCell.fxml";

    //! Link between the current playlist and the current track.
    private PlaylistTrack playlistTrack;

    //! Track associate to the playlistTrack.
    private Track track;

    //! Tell if the track is favorite.
    private boolean favorite;

    //!
    @FXML
    private AnchorPane trackPane;

    //!
    @FXML
    private Label albumLabel;

    //!
    @FXML
    private Label artistLabel;

    //!
    @FXML
    private Label titleLabel;

    //!
    @FXML
    private Label votesLabel;

    //!
    @FXML
    private ImageView favoriteImageView;

    /**
     * @brief Constructor.
     *
     * @param playlistTrack
     */
    public PlaylistTrackCell(PlaylistTrack playlistTrack) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_FILE));
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.playlistTrack = playlistTrack;
        this.track = playlistTrack.getTrack();

        titleLabel.setText(track.getTitle());
        artistLabel.setText(track.getArtist());
        albumLabel.setText(track.getAlbum());

        votesLabel.setText(String.valueOf(playlistTrack.getVotes()));

        if(favorite) {
            favoriteImageView.setImage(new Image("ui/icons/fav_full.png"));
        } else {
            favoriteImageView.setImage(new Image("ui/icons/fav_empty.png"));
        }
    }

    /**
     * @brief
     *
     * @return
     */
    public AnchorPane getPane() {
        return trackPane;
    }

    /**
     * @brief Downvote the track.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void downvote(MouseEvent e) {
        playlistTrack.downvote();

        votesLabel.setText(String.valueOf(playlistTrack.getVotes()));
    }

    /**
     * @brief Favorite the track.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void favorite(MouseEvent e) {
        favorite = !favorite;

        if (favorite) {
            favoriteImageView.setImage(new Image("ui/icons/fav_full.png"));
        } else {
            favoriteImageView.setImage(new Image("ui/icons/fav_empty.png"));
        }

        LOG.log(Logger.Level.INFO, String.format("%s favorited!", track));
    }

    /**
     * @brief Upvote the track.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void upvote(MouseEvent e) {
        playlistTrack.upvote();

        votesLabel.setText(String.valueOf(playlistTrack.getVotes()));
    }
}
