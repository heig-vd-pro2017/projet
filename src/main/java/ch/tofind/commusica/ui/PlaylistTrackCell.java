package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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

    //! Path to the file containing the 'fav_empty' image.
    private static final String FAV_EMPTY_IMAGE = "ui/icons/fav_empty.png";

    //! Path to the file containing the 'fav_full' image.
    private static final String FAV_FULL_IMAGE = "ui/icons/fav_full.png";

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/PlaylistTrackCell.fxml";

    //! Link between the current playlist and the current track.
    private PlaylistTrack playlistTrack;

    //! Track associate to the playlistTrack.
    private Track track;

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
        votesLabel.setText(String.valueOf(playlistTrack.getVotesProperty().intValue()));

        favoriteImageView.setImage((new Image(track.getFavoritedProperty().getValue() ? FAV_FULL_IMAGE : FAV_EMPTY_IMAGE)));

        track.getFavoritedProperty().addListener(((observable, oldValue, newValue) -> {
            favoriteImageView.setImage(new Image(newValue ? FAV_FULL_IMAGE : FAV_EMPTY_IMAGE));
        }));

        playlistTrack.getVotesProperty().addListener(((observable, oldValue, newValue) -> {
            votesLabel.setText(String.valueOf(newValue.intValue()));
        }));

        // Set value at loading.
        if (playlistTrack.getBeenPlayedProperty().getValue()) {
            trackPane.getStyleClass().add("played-song");
        } else {
            trackPane.getStyleClass().remove("played-song");
        }

        // Listen to changes.
        playlistTrack.getBeenPlayedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                trackPane.getStyleClass().add("played-song");
            } else {
                trackPane.getStyleClass().remove("played-song");
            }
        }));
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
    }

    /**
     * @brief Favorite the track.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void favorite(MouseEvent e) {
        track.getFavoritedProperty().setValue(!track.getFavoritedProperty().getValue());
    }

    /**
     * @brief Upvote the track.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void upvote(MouseEvent e) {
        playlistTrack.upvote();
    }
}
