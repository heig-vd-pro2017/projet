package ch.tofind.commusica.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UIController extends Application implements Initializable {

    private static final String FXFILE = "main.fxml";

    //! JavaFX components.
    @FXML
    private ListView<String> playlistsListView;

    @FXML
    private ListView<String> songsListView;

    public void start(Stage stage) throws Exception {
        URL fileURL = getClass().getClassLoader().getResource(FXFILE);

        if (fileURL == null) {
            throw new NullPointerException("FXML file not found.");
        }

        Parent root = FXMLLoader.load(fileURL);

        Scene scene = new Scene(root);

        stage.setTitle("Commusica");
        stage.setScene(scene);
        stage.sizeToScene();

        stage.show();

        stage.setMinHeight(stage.getHeight());
    }

    public void initialize(URL location, ResourceBundle resources) {
        populatePlaylists();
        populateSongs();
    }

    private void populatePlaylists() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (int i = 1; i <= 3; ++i) {
            items.add(String.format("Playlist %d", i));
        }

        playlistsListView.setItems(items);
    }

    private void populateSongs() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (int i = 1; i <= 8; ++i) {
            items.add(String.format("Song %d", i));
        }

        songsListView.setItems(items);
        songsListView.setCellFactory((ListView<String> cell) -> new ListCell<String>() {
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item != null) {
                    try {
                        TrackCellController cellController = new TrackCellController(item);
                        setGraphic(cellController.getPane());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

}
