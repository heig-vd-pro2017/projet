package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * @brief This class represents
 */
public class CurrentTrackView extends GridPane {

    //! Component CSS class.
    public static final String CSS_CLASS = "current-track-view";

    //! CSS file to apply to the view.
    private static final String CSS_FILE = "ui/styles/CurrentTrackView.css";

    //! Path to the file containing the 'fav_empty' image.
    private static final String FAV_EMPTY_IMAGE = "ui/icons/fav_empty.png";

    //! Path to the file containing the 'fav_full' image.
    private static final String FAV_FULL_IMAGE = "ui/icons/fav_full.png";

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/CurrentTrackView.fxml";

    //! Commusica player.
    private static final Player player = Player.getCurrentPlayer();

    //! Playlist manager.
    private static final PlaylistManager playlistManager = PlaylistManager.getInstance();

    @FXML
    private Label albumLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private ProgressBar durationBar;

    @FXML
    private Label durationLabel;

    @FXML
    private ImageView favoriteImageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label votesLabel;

    /**
     * @brief View constructor.
     */
    public CurrentTrackView() {
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

        // What to do when the current track changes.
        player.getCurrentTrackProperty().addListener((observable, oldTrack, newTrack) -> {
            PlaylistTrack playlistTrack = playlistManager.getPlaylistTrack(newTrack);
            albumLabel.setText(newTrack.getAlbum());
            artistLabel.setText(newTrack.getArtist());
            titleLabel.setText(newTrack.getTitle());
            votesLabel.setText(String.valueOf(playlistTrack.getVotesProperty().intValue()));

            favoriteImageView.setImage((new Image(newTrack.getFavoritedProperty().getValue() ? FAV_FULL_IMAGE : FAV_EMPTY_IMAGE)));
            newTrack.getFavoritedProperty().addListener(((obs, oldValue, newValue) -> {
                favoriteImageView.setImage((new Image(newValue ? FAV_FULL_IMAGE : FAV_EMPTY_IMAGE)));
            }));

            playlistTrack.getVotesProperty().addListener((obs, oldValue, newValue) -> {
                votesLabel.setText(String.valueOf(newValue.intValue()));
            });
        });

        // What to do when the elapsed time for the current track changes.
        player.getCurrentTimeProperty().addListener((observable, oldValue, newValue) -> {
            int total = player.getCurrentTrack().getLength();
            int current = newValue.intValue();
            int diff = total - current;

            durationLabel.setText(String.format("-%02d:%02d", diff / 60, diff % 60));
            durationBar.setProgress((double)current / (double)total);
        });
    }
}
