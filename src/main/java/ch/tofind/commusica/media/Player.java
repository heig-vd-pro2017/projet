package ch.tofind.commusica.media;

import ch.tofind.commusica.playlist.PlaylistManager;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Player {

    private MediaPlayer player;

    private Track track;

    private double volumeStep;

    /**
     * @brief Player single constructor. Avoid the instantiation.
     */
    public Player(double volumeStep) {
        this.volumeStep = volumeStep;
    }

    public void load() {
        
        track = PlaylistManager.getInstance().nextTrack();

        if (track != null) {

            Media media = new Media(new File(track.getUri()).toURI().toString());

            player = new MediaPlayer(media);

            player.setOnEndOfMedia(() -> {
                stop();
                load();
                play();
            });

        } else {
            stop();
        }
    }

    public void play() {
        if (track == null) {
            load();
        }
        player.play();
    }

    public void pause() {
        player.pause();
    }

    public void stop() {
        player.stop();
    }

    public void riseVolume() {
        double currentVolume = player.getVolume();
        player.setVolume(currentVolume + volumeStep);
    }

    public void lowerVolume() {
        double currentVolume = player.getVolume();
        player.setVolume(currentVolume - volumeStep);
    }
}


