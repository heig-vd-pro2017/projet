package ch.tofind.commusica.player;

import ch.tofind.commusica.media.Track;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test extends Application {
 private MediaPlayer mediaplayer;

     public Test(){};
    public static void main(String[] args) {
        launch(args);
    }

    //public Test(){};

    @Override
    public void start(Stage primaryStage) throws Exception {



        Track track = new Track();
        Track track1 = new Track();


        final Group root = new Group();

        File resource = new File("/Users/Thibaut-PC/Documents/Cour HEIG 5 semestre/Projet de semestre/repos_projet/projet/projet/commusica/563[kb]074_the-dream-click-bass.wav.mp3");
        File  resource1 = new File("/Users/Thibaut-PC/Documents/Cour HEIG 5 semestre/Projet de semestre/repos_projet/projet/projet/commusica/837[kb]074_heartbeat-noise-machine.wav.mp3");

        track.setUri(resource.toURI().toString());
        track1.setUri(resource1.toURI().toString());
        List<Track> list = new ArrayList<Track>();
        list.add(track);
        list.add(track1);

Player player = new Player(list);

  player.Next(0);
       // player.handleChange();
       mediaplayer = player.getplayer();
        int i = 0;


    }

}
