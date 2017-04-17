package ch.tofind.commusica.player;

import ch.tofind.commusica.media.Track;
import ch.tofind.commusica.util.TrackListUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static javafx.application.Application.launch;

/**
 * Created by Thibaut-PC on 02.04.17.
 */


public class Player {

    public final Logger logger = Logger.getLogger(getClass().getName());

//


    //media player

    private MediaPlayer player;

    //current track
    private Track currentTrack;

    int currentindex;

    private Track prevTrack;

    private Track nextTrack;

    private Media currentMedia;

    final MediaView view = new MediaView();

    Iterator<Track> itr;

    List<Track> list = new ArrayList<Track>();

    private final boolean repeat = false;

    private boolean stopRequested = false;

    private boolean atEndOfMedia = false;

    private static int i = 0;


    /**
     * @param list
     */

    public Player(List<Track> list) {

        itr = list.iterator();
        this.list = list;
    }


    /**
     * @param index
     * @return
     */


    public Track prev(int index) {

        Track track = list.get(index);

        if (track != null) {
            player(track);
            return track;
        }
        return null;
    }

    /**
     * @param index
     * @return
     */

    public Track Next(int index) {

        Track track = list.get(index);
        if (track != null) {
            player(track);
            return track;
        }
        return null;
    }


    /**
     *
     */

    public void playPause() {

        if (player == null) {

            player(list.get(0));
        } else if (player.getStatus() == player.getStatus().PLAYING) {

            player.pause();
        } else {
            player.play();
        }


    }


    /**
     *
     */

    public void stop() {
        if (player != null) {
            player.stop();
        }
        Platform.exit();
    }


    /**
     * @param volume
     */

    public void setVolume(double volume) {
        if (player != null) {
            player.setVolume(volume);
        }
    }

    /**
     * @param track
     */

    private void player(Track track) {
        if (player != null) {
            player.stop();
            player.setAudioSpectrumListener(null);
        }

        final Media media = new Media(track.getUri());
        player = new MediaPlayer(media);
        player.setAutoPlay(true);
        player.setOnError(new Runnable() {
            @Override
            public void run() {
                System.out.println("player.getErro() =" + player.getError());
            }
        });
    }


    public MediaPlayer getplayer() {

        return this.player;
    }


    public void handleChange() {
        if (player != null) {
            player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    player.stop();

                    System.out.println("playing the second audio file...");
                    if (i < list.size())
                        Next(i);
                    player.setOnError(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("mediaPlayer.getError() = " + player.getError());
                        }
                    });
                }
            });


        }


    }

}


