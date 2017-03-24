package ch.tofind.commusica.tests;

import java.io.File;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class Player extends Application {

    private static Media media;
    private static MediaPlayer mediaPlayer;

    private File resource;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        resource = new File("doc/commusica/player/sample.wav");
        media = new Media(resource.toURI().toString());

        // Print the metadata
        System.out.println(media.getMetadata());

        // Print the duration
        System.out.println(media.getDuration();
        
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
}
