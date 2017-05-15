package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/**
 * @brief This class represents
 */
public class PlayerControlsView extends GridPane {

    //! CSS class.
    public static final String CSS_CLASS = "player-controls-view";

    //! CSS file to apply to the view.
    private static final String CSS_FILE = "ui/styles/PlayerControlsView.css";

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/PlayerControlsView.fxml";

    //! Logger for debugging.
    private static final Logger LOG = new Logger(PlayerControlsView.class.getSimpleName());

    //! Player to use.
    private static Player player = Player.getCurrentPlayer();

    private static final String PLAY_IMAGE = "ui/icons/play.png";

    private static final String PAUSE_IMAGE = "ui/icons/pause.png";

    //! Volume step.
    private static final Double VOLUME_STEP = 1.0 / 16.0; // Pourrait être récupéré depuis le fichier de configuration ?

    //! Name of the view.
    @FXML
    private ImageView playPauseImageView;

    //! Volume slider.
    @FXML
    private Slider volumeSlider;

    /**
     * @brief View constructor.
     */
    public PlayerControlsView() {
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

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (player != null) {
                player.setVolume(newValue.doubleValue());
            }
        });

        playPauseImageView.setImage(new Image(player.isPlaying() ? PAUSE_IMAGE : PLAY_IMAGE));
        player.getIsPlayingProperty().addListener(((observable, oldValue, newValue) -> {
            playPauseImageView.setImage(new Image(newValue ? PAUSE_IMAGE : PLAY_IMAGE));
        }));
    }

    /**
     * @brief Lower the volume.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void lowerVolume(MouseEvent e) {
        volumeSlider.adjustValue(volumeSlider.getValue() - VOLUME_STEP);
    }

    /**
     * @brief Ask for the next track.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void next(MouseEvent e) {
        LOG.log(Logger.Level.INFO, "Asked for next track.");

        if(Configuration.getInstance().get("DEBUG").equals("1")) {
            player.load();
        }
    }

    /**
     * @brief Play or pause the current music.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void playPause(MouseEvent e) {
        if(player.isPlaying()) {
            player.pause();
        } else {
            player.play();
        }

        player.setVolume(volumeSlider.getValue());
    }

    /**
     * @brief Ask for the previous track.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void previous(MouseEvent e) {
        LOG.log(Logger.Level.INFO, "Asked for previous track.");
    }

    /**
     * @brief Rise the volume.
     *
     * @param e MouseEvent that triggered the function.
     */
    @FXML
    private void riseVolume(MouseEvent e) {
        volumeSlider.adjustValue(volumeSlider.getValue() + VOLUME_STEP);
    }
}
