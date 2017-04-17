package ch.tofind.commusica.player;

import ch.tofind.commusica.media.Track;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test extends Application {
    private MediaPlayer mediaplayer;

    public Test() {
    }

    ;

    public static void main(String[] args) {
        launch(args);
    }

    //public Test(){};

    @Override
    public void start(Stage primaryStage) throws Exception {


        Track track = new Track();
        Track track1 = new Track();
        Track track2 = new Track();
        Track track3 = new Track();
        Track track4 = new Track();
        Track track5 = new Track();


        File resource = new File("/Users/Thibaut-PC/Documents/Cour HEIG 5 semestre/Projet de semestre/repos_projet/projet/projet/commusica/563[kb]074_the-dream-click-bass.wav.mp3");
        File resource1 = new File("/Users/Thibaut-PC/Documents/Cour HEIG 5 semestre/Projet de semestre/repos_projet/projet/projet/commusica/837[kb]074_heartbeat-noise-machine.wav.mp3");
        File resource2 = new File("/Users/Thibaut-PC/Documents/Cour HEIG 5 semestre/Projet de semestre/repos_projet/projet/projet/commusica/563[kb]074_the-dream-click-bass.wav.mp3");
        File resource3 = new File("/Users/Thibaut-PC/Documents/Cour HEIG 5 semestre/Projet de semestre/repos_projet/projet/projet/commusica/837[kb]074_heartbeat-noise-machine.wav.mp3");
        File resource4 = new File("/Users/Thibaut-PC/Documents/Cour HEIG 5 semestre/Projet de semestre/repos_projet/projet/projet/commusica/563[kb]074_the-dream-click-bass.wav.mp3");
        File resource5 = new File("/Users/Thibaut-PC/Documents/Cour HEIG 5 semestre/Projet de semestre/repos_projet/projet/projet/commusica/837[kb]074_heartbeat-noise-machine.wav.mp3");

        track.setUri(resource.toURI().toString());
        track1.setUri(resource1.toURI().toString());
        track2.setUri(resource2.toURI().toString());
        track3.setUri(resource3.toURI().toString());
        track4.setUri(resource4.toURI().toString());
        track5.setUri(resource5.toURI().toString());
        List<Track> list = new ArrayList<Track>();
        list.add(track);
        list.add(track1);
        list.add(track2);
        list.add(track3);
        list.add(track4);
        list.add(track5);


        Player player = new Player(list);

       // player.Next(0);

        BorderPane root = new BorderPane() {
            {
                VBox vbox = new VBox() {{

                    HBox hbox = new HBox() {{

                        Button playButton = new Button("Play");
                        Button pauseButton = new Button("Pause");
                        Button nextButton = new Button("next");
                        Button prevButton = new Button("Prev");
                        mediaplayer = player.getplayer();


                        playButton.setOnAction(e -> {

                            player.playPause();

                            player.getplayer().setOnEndOfMedia(() ->  player.handleChange()

                                 );

                        });
                        pauseButton.setOnAction(e -> {
                            player.playPause();

                        });

                        prevButton.setOnAction(e -> {
                            player.Next(1);
                        });

                        nextButton.setOnAction(e -> {
                            player.prev(0);
                        });

                        Button stopButton = new Button("Stop") {{
                            setOnAction(e -> {
                                player.stop();
                            });
                        }};

                        getChildren().addAll(playButton, pauseButton, stopButton, nextButton, prevButton);
                    }};
                    getChildren().add(hbox);
                }};
                setCenter(vbox);
            }};


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.err.println("dans le run");

                        try {
                           // player.handleChange();
                            Thread.sleep(900);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Scene scene = new Scene(root, 400, 100);

                primaryStage.setScene(scene);
                primaryStage.show();
            }


        }

