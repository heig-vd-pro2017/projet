package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * @brief This class represents a cell with the PlaylistTrack information.
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

    @FXML
    private AnchorPane trackPane;

    @FXML
    private Label albumLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label votesLabel;

    @FXML
    private ImageView favoriteImageView;

    /**
     * @brief Make a new cell with the PlaylistTrack informations.
     *
     * @param playlistTrack The PlaylistTrack to show in the cell.
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
        if (playlistTrack.hasBeenPlayed().getValue()) {
            trackPane.getStyleClass().add("played-song");
        } else {
            trackPane.getStyleClass().remove("played-song");
        }

        // Listen to changes.
        playlistTrack.hasBeenPlayed().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                trackPane.getStyleClass().add("played-song");
            } else {
                trackPane.getStyleClass().remove("played-song");
            }
        }));
    }

    /**
     * @brief Get the current pane.
     *
     * @return The pane.
     */
    public AnchorPane getPane() {
        return trackPane;
    }


    @FXML
    private void downvote(MouseEvent event) {
        if (!UIController.getController().getCurrentPlaylist().isSaved()) {
            String playlistTrackId = playlistTrack.getTrack().getId();
            ArrayList<Object> args = new ArrayList<>();
            args.add(playlistTrackId);

            Core.execute(ApplicationProtocol.SEND_DOWNVOTE_TRACK_REQUEST, args);
        }

        event.consume();
    }

    @FXML
    private void favorite(MouseEvent event) {
        track.getFavoritedProperty().setValue(!track.getFavoritedProperty().getValue());

        event.consume();
    }

    @FXML
    private void upvote(MouseEvent event) {
        if (!UIController.getController().getCurrentPlaylist().isSaved()) {
            String playlistTrackId = playlistTrack.getTrack().getId();
            ArrayList<Object> args = new ArrayList<>();
            args.add(playlistTrackId);

            Core.execute(ApplicationProtocol.SEND_UPVOTE_TRACK_REQUEST, args);
        }

        event.consume();
    }
}
