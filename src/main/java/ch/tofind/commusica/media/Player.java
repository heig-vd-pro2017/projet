package ch.tofind.commusica.media;

import ch.tofind.commusica.playlist.PlaylistManager;

import ch.tofind.commusica.utils.Configuration;
import ch.tofind.commusica.utils.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Player {

    private static final Logger LOG = new Logger(Player.class.getSimpleName());

    private MediaPlayer player;

    private Track track;

    private double volumeStep;

    private static Player currentPlayer;

    private boolean isPlaying = false;

    /**
     * @brief Player single constructor. Avoid the instantiation.
     */
    private Player(double volumeStep) {
        this.volumeStep = volumeStep;
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
        track = PlaylistManager.getInstance().nextTrack();

        if (track != null) {
            Media media = null;

            try {
                new Media(new File(track.getUri()).toURI().toString());
            } catch (MediaException e) {
                LOG.log(Logger.Level.SEVERE, e);
            }

            // Stop current player if there is one.
            if(player != null) {
                player.stop();
            }

            // Start the new player.
            player = new MediaPlayer(media);

            // Tell what to do when the track is finished.
            player.setOnEndOfMedia(() -> {
                stop();
                load();
                play();
            });

        } else {
            stop();
        }
    }

    public Track getCurrentTrack() {
        return track;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void play() {
        if (track == null) {
            load();
        }
        player.play();
        isPlaying = true;
    }

    public void pause() {
        player.pause();
        isPlaying = false;
    }

    public void stop() {
        player.stop();
        isPlaying = false;
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


