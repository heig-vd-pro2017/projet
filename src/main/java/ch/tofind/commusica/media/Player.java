package ch.tofind.commusica.media;

import ch.tofind.commusica.playlist.PlaylistManager;
import ch.tofind.commusica.playlist.PlaylistTrack;
import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;
import ch.tofind.commusica.utils.PlaylistTrackProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * @brief This class represents a player that can play a track.
 */
public class Player {

    //! Logger for debugging.
    private static final Logger LOG = new Logger(Player.class.getSimpleName());

    // The current player.
    private static Player currentPlayer;

    //! The current track.
    private MediaPlayer player;

    //! The current played time of the track.
    private final IntegerProperty currentTimeProperty;

    //! The current playing track.
    private final PlaylistTrackProperty currentTrackProperty;

    //! The previously played track.
    private final PlaylistTrackProperty previousTrackProperty;

    //! The volume steps.
    private double volumeStep;

    private final BooleanProperty isPlayingProperty;

    private final DoubleProperty volumeProperty;

    //! Minimum value for the volumeProperty
    private final double MIN_VOLUME = 0.0;

    //! Maximum value for the volumeProperty
    private final double MAX_VOLUME = 1.0;



    /**
     * @brief Player single constructor. Avoid the instantiation.
     */
    private Player(double volumeStep) {
        this.volumeStep = volumeStep;

        currentTimeProperty = new SimpleIntegerProperty(0);
        currentTrackProperty = new PlaylistTrackProperty();
        previousTrackProperty = new PlaylistTrackProperty();
        isPlayingProperty = new SimpleBooleanProperty(false);
        volumeProperty = new SimpleDoubleProperty(MAX_VOLUME / 2.0);

        previousTrackProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                PlaylistManager.getInstance().getPlaylist().saveTrack(newValue.getTrack());
            }
        });

        volumeProperty.addListener((obs, oldValue, newValue) -> {
            if (player != null) {
                player.setVolume(newValue.doubleValue());
            }
        });
    }

    /**
     * @brief Get the object instance.
     *
     * @return The instance of the object.
     */
    public static Player getCurrentPlayer() {
        if (currentPlayer == null) {
            synchronized (Player.class) {
                if (currentPlayer == null) {
                    currentPlayer = new Player(Double.parseDouble(Configuration.getInstance().get("VOLUME_STEP")));
                }
            }
        }

        return currentPlayer;
    }

    /**
     * @brief Ask the playlist manager the next playlist and load it in the player and plays it.
     */
    public void load() {

        if (currentTrackProperty.getValue() != null) {
            previousTrackProperty.setValue(currentTrackProperty.getValue());
        }

        currentTrackProperty.setValue(PlaylistManager.getInstance().getPlaylist().getTracksList().getNextTrack());

        if (currentTrackProperty.getValue() != null) {
            Media media = null;

            LOG.info("new track: " + currentTrackProperty.getTrack().getTitle());

            try {
                media = new Media(new File(currentTrackProperty.getTrack().getUri()).toURI().toString());
            } catch (MediaException e) {
                LOG.error(e);
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
            player = null;
        }
    }

    /**
     * @brief Get the current time of a playimg track.
     *
     * @return The current time of the playing track.
     */
    public IntegerProperty getCurrentTimeProperty() {
        return currentTimeProperty;
    }

    /**
     * @brief Get the current track in the player.
     *
     * @return The current track.
     */
    public Track getCurrentTrack() {
        return currentTrackProperty.getTrack();
    }

    /**
     * @brief Get the current track property in the player.
     *
     * @return The current track property.
     */
    public PlaylistTrackProperty getCurrentTrackProperty() {
        return currentTrackProperty;
    }

    /**
     * @brief Get the privous track property in the player.
     *
     * @return The previous track property.
     */
    public PlaylistTrackProperty getPreviousTrackProperty() {
        return previousTrackProperty;
    }

    /**
     * @brief Get the playing status of the running player.
     *
     * @return The playing property of the running player.
     */
    public BooleanProperty getIsPlayingProperty() {
        return isPlayingProperty;
    }

    public DoubleProperty getVolumeProperty() {
        return volumeProperty;
    }

    /**
     * @brief Get the playing status of the running player.
     *
     * @return The playing status of the running player.
     */
    public boolean isPlaying() {
        return isPlayingProperty.getValue();
    }

    /**
     * @brief Start the player. Ask the PlaylistManager for tracks if needed.
     */
    public void play() {
        if (currentTrackProperty.getValue() == null) {
            load();
        }

        if (player != null) {
            player.play();
            isPlayingProperty.setValue(true);
        }
    }

    /**
     * @brief Pause the player.
     */
    public void pause() {
        if (player != null) {
            player.pause();
            isPlayingProperty.setValue(false);
        }
    }

    /**
     * @brief Stop the player.
     */
    public void stop() {
        if (player != null) {
            player.stop();
            isPlayingProperty.setValue(false);
        }
    }

    /**
     * @brief Rise the volume of the player.
     */
    public void riseVolume() {
        if (volumeProperty.get() + volumeStep > MAX_VOLUME) {
            volumeProperty.setValue(MAX_VOLUME);
        } else {
            volumeProperty.setValue(volumeProperty.getValue() + volumeStep);
        }
    }

    /**
     * @brief Lower the volume of the player.
     */
    public void lowerVolume() {
        if (volumeProperty.get() - volumeStep < MIN_VOLUME) {
            volumeProperty.setValue(MIN_VOLUME);
        } else {
            volumeProperty.setValue(volumeProperty.getValue() - volumeStep);
        }
    }

    /**
     * @brief Get the current player's volume.
     *
     * @return The volume of the current player.
     */
    /*public double getVolume() {
        return player.getVolume();
    }*/

    /**
     * @brief Set the current player's volume.
     *
     * @param volume The volume to apply on the current player.
     */
    /*public void setVolume(double volume) {
        if (player != null) {
            player.setVolume(volume);
        }
    }*/
}