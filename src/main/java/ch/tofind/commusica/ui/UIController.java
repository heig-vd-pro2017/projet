package ch.tofind.commusica.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class UIController extends Application implements Initializable {

    private final String FXFILE = "main.fxml";

    //! JavaFX components.
    @FXML
    private ListView<String> playlistsListView;

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(FXFILE));

        Scene scene = new Scene(root);

        stage.setTitle("Commusica");
        stage.setScene(scene);

        stage.show();
    }

    private void populatePlaylists() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (int i = 1; i <= 3; ++i) {
            items.add(String.format("Playlist %d", i));
        }

        if(playlistsListView != null) {
            playlistsListView.setItems(items);
        }
    }

    public void initialize(URL location, ResourceBundle resources) {
        populatePlaylists();
    }
}
