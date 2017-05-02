package ch.tofind.commusica.ui;

import ch.tofind.commusica.media.Player;
import ch.tofind.commusica.utils.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerControlsView extends GridPane implements Initializable {

    //! CSS class.
    public static final String CSS_CLASS = "player-controls-view";

    private static final String CSS_FILE = "ui/styles/PlayerControlsView.css";

    private static final String FXML_FILE = "ui/PlayerControlsView.fxml";

    private static final Double VOLUME_STEP = 1.0 / 16.0;

    private static final Logger LOG = new Logger(PlayerControlsView.class.getSimpleName());

    private static Player player = Player.getCurrentPlayer();

    @FXML
    private ImageView playPauseImageView;

    @FXML
    private Slider volumeSlider;

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

        if(player.isPlaying()) {
            playPauseImageView.setImage(new Image("ui/icons/pause.png"));
        } else {
            playPauseImageView.setImage(new Image("ui/icons/play.png"));
        }
    }

    @FXML
    private void lowerVolume(MouseEvent e) {
        volumeSlider.adjustValue(volumeSlider.getValue() - VOLUME_STEP);
    }

    @FXML
    private void next(MouseEvent e) {
        LOG.info("Asked for next track.");
    }

    @FXML
    private void playPause(MouseEvent e) {
        if(player.isPlaying()) {
            player.pause();
            playPauseImageView.setImage(new Image("ui/icons/play.png"));
        } else {
            player.play();
            playPauseImageView.setImage(new Image("ui/icons/pause.png"));
        }

        player.setVolume(volumeSlider.getValue());
    }

    @FXML
    private void previous(MouseEvent e) {
        LOG.info("Asked for previous track.");
    }

    @FXML
    private void riseVolume(MouseEvent e) {
        volumeSlider.adjustValue(volumeSlider.getValue() + VOLUME_STEP);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (player != null) {
                player.setVolume(newValue.doubleValue());
            }
        });
    }
}
