package ch.tofind.commusica.media;

import ch.tofind.commusica.playlist.PlaylistManager;

import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.PlaylistTrackProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Player {

    private static final Logger LOG = new Logger(Player.class.getSimpleName());

    private MediaPlayer player;
    
    private final IntegerProperty currentTimeProperty;

    private final PlaylistTrackProperty currentTrackProperty;

    private final PlaylistTrackProperty previousTrackProperty;

    private double volumeStep;

    private static Player currentPlayer;

    private final BooleanProperty isPlayingProperty;

    /**
     * @brief Player single constructor. Avoid the instantiation.
     */
    private Player(double volumeStep) {
        this.volumeStep = volumeStep;

        currentTimeProperty = new SimpleIntegerProperty(0);
        currentTrackProperty = new PlaylistTrackProperty();
        previousTrackProperty = new PlaylistTrackProperty();
        isPlayingProperty = new SimpleBooleanProperty(false);
    }

    public static Player getCurrentPlayer() {
        if (currentPlayer == null) {
            synchronized (Player.class) {
                if (currentPlayer == null) {
                    currentPlayer = new Player(Double.parseDouble(Configuration.getInstance().get("DEFAULT_VOLUME_STEP")));
                }
            }
        }

        return currentPlayer;
    }

    public void load() {
        if (currentTrackProperty.getValue() != null) {
            previousTrackProperty.setValue(currentTrackProperty.getValue());
        }

        //currentTrackProperty.setValue(PlaylistManager.getInstance().nextTrack());
        currentTrackProperty.setValue(PlaylistManager.getInstance().getPlaylist().tracksList().getNextTrack());

        if (currentTrackProperty.getValue() != null) {
            Media media = null;

            LOG.info("new track: " + currentTrackProperty.getTrack().getTitle());

            try {
                media = new Media(new File(currentTrackProperty.getTrack().getUri()).toURI().toString());
            } catch (MediaException e) {
                LOG.log(Logger.Level.SEVERE, e);
            }

            // Stop current player if there is one.
            if(player != null) {
                LOG.info("Previous player stopped.");
                isPlayingProperty.setValue(false);
                player.stop();
            }

            // Start the new player.
            player = new MediaPlayer(media);

            // Notify 'currentTimeProperty' each time it changes.
            // Can't do better (I think?).
            player.currentTimeProperty().addListener(((observable, oldValue, newValue) -> {
                currentTimeProperty.setValue(newValue.toSeconds());
            }));

            // Tell what to do when the track is finished.
            player.setOnEndOfMedia(() -> {
                stop();
                load();
            });

            play();
        } else {
            stop();
            currentTrackProperty.setValue(null);
        }
    }

    public IntegerProperty getCurrentTimeProperty() {
        return currentTimeProperty;
    }

    public Track getCurrentTrack() {
        return currentTrackProperty.getTrack();
    }

    public PlaylistTrackProperty getCurrentTrackProperty() {
        return currentTrackProperty;
    }

    public PlaylistTrackProperty getPreviousTrackProperty() {
        return previousTrackProperty;
    }

    public BooleanProperty getIsPlayingProperty() {
        return isPlayingProperty;
    }

    public boolean isPlaying() {
        return isPlayingProperty.getValue();
    }

    public void play() {
        if (currentTrackProperty.getValue() == null) {
            load();
        }
        player.play();
        isPlayingProperty.setValue(true);
    }

    public void pause() {
        player.pause();
        isPlayingProperty.setValue(false);
    }

    public void stop() {
        player.stop();
        isPlayingProperty.setValue(false);
    }

    public void riseVolume() {
        double currentVolume = player.getVolume();
        player.setVolume(currentVolume + volumeStep);
    }

    public void lowerVolume() {
        double currentVolume = player.getVolume();
        player.setVolume(currentVolume - volumeStep);
    }

    public double getVolume() {
        return player.getVolume();
    }

    public void setVolume(double volume) {
        player.setVolume(volume);
    }
}


