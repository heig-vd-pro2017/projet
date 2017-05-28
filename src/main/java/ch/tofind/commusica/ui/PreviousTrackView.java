package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.media.Track;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * This class represents the previous track view.
 */
public class PreviousTrackView extends GridPane {

    //! CSS class value.
    public static final String CSS_CLASS = "previous-track-view";

    //! CSS file to apply to the view.
    private static final String CSS_FILE = "ui/styles/PreviousTrackView.css";

    //! Path to the file containing the 'fav_empty' image.
    private static final String FAV_EMPTY_IMAGE = "ui/icons/fav_empty.png";

    //! Path to the file containing the 'fav_full' image.
    private static final String FAV_FULL_IMAGE = "ui/icons/fav_full.png";

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/PreviousTrackView.fxml";

    //! The player of the previous track.
    private static final Player player = Player.getCurrentPlayer();

    //! The previous track.
    private Track previousTrack;

    @FXML
    private Label albumLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private ImageView favoriteImageView;

    @FXML
    private Label titleLabel;

    /**
     * View constructor.
     */
    public PreviousTrackView() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FXML_FILE));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getStyleClass().add(CSS_CLASS);
        getStylesheets().add(CSS_FILE);

        player.getPreviousTrackProperty().addListener(((observable, oldPTrack, newPTrack) -> {
            previousTrack = newPTrack.getTrack();

            if (previousTrack != null) {
                albumLabel.setText(previousTrack.getAlbum());
                artistLabel.setText(previousTrack.getArtist());
                titleLabel.setText(previousTrack.getTitle());

                favoriteImageView.setImage(new Image(previousTrack.getFavoritedProperty().getValue() ? FAV_FULL_IMAGE : FAV_EMPTY_IMAGE));
                previousTrack.getFavoritedProperty().addListener(((obs, oldValue, newValue) -> {
                    favoriteImageView.setImage(new Image(newValue ? FAV_FULL_IMAGE : FAV_EMPTY_IMAGE));
                }));
            } else {
                albumLabel.setText("-");
                artistLabel.setText("-");
                titleLabel.setText("-");
            }
        }));
    }

    @FXML
    private void favorite(MouseEvent e) {
        if (previousTrack != null) {
            previousTrack.getFavoritedProperty().setValue(!previousTrack.getFavoritedProperty().getValue());
        }
    }
}
