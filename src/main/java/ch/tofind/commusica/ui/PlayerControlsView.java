package ch.tofind.commusica.ui;

import ch.tofind.commusica.core.ApplicationProtocol;
import ch.tofind.commusica.core.Core;
import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * @brief This class represents the player controls view.
 */
public class PlayerControlsView extends GridPane {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(PlayerControlsView.class.getSimpleName());

    //! CSS class.
    public static final String CSS_CLASS = "player-controls-view";

    //! CSS file to apply to the view.
    private static final String CSS_FILE = "ui/styles/PlayerControlsView.css";

    //! FXML file to use for the view.
    private static final String FXML_FILE = "ui/PlayerControlsView.fxml";

    //! Player to use.
    private static Player player = Player.getCurrentPlayer();

    //! Image of the play icon.
    private static final String PLAY_IMAGE = "ui/icons/play.png";

    //! Image of the pause icon.
    private static final String PAUSE_IMAGE = "ui/icons/pause.png";

    //! Volume step.
    private static final Double VOLUME_STEP = Double.valueOf(Configuration.getInstance().get("VOLUME_STEP"));

    @FXML
    private ImageView playPauseImageView;

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

    @FXML
    private void lowerVolume(MouseEvent e) {
        //volumeSlider.adjustValue(volumeSlider.getValue() - VOLUME_STEP);
        Core.execute(ApplicationProtocol.SEND_TURN_VOLUME_DOWN_REQUEST, null);
    }

    @FXML
    private void next(MouseEvent e) {
        Core.execute(ApplicationProtocol.SEND_NEXT_TRACK_REQUEST, null);
    }

    @FXML
    private void playPause(MouseEvent e) {
        /*
        if(player.isPlaying()) {
            player.pause();
        } else {
            player.play();
        }

        player.setVolume(volumeSlider.getValue());
        */
        Core.execute(ApplicationProtocol.SEND_PLAY_PAUSE_REQUEST, null);
    }

    @FXML
    private void previous(MouseEvent e) {
        Core.execute(ApplicationProtocol.SEND_PREVIOUS_TRACK_REQUEST, null);
    }

    @FXML
    private void riseVolume(MouseEvent e) {
        //volumeSlider.adjustValue(volumeSlider.getValue() + VOLUME_STEP);
        Core.execute(ApplicationProtocol.SEND_TURN_VOLUME_UP_REQUEST, null);
    }
}
